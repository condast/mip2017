package org.miip.waterway.ui.swt.pond;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.ui.images.MIIPImages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.condast.commons.Utils;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationListener;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.plane.IField;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.ui.utils.ScalingUtils;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

public class PondCanvas extends Canvas implements IInputWidget<IMIIPEnvironment>{
	private static final long serialVersionUID = 1L;

	public static final int GRIDX = 100;//meters
	public static final int GRIDY = 20;//meters
	
	private PaintListener listener = new PaintListener(){
		private static final long serialVersionUID = 1L;

		@Override
		public void paintControl(PaintEvent event) {
			try{
				drawField( event.gc );
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}
	};

	private ISituationListener<IPhysical> slistener = new ISituationListener<IPhysical>() {

		@Override
		public void notifySituationChanged(SituationEvent<IPhysical> event) {
			if( disposed || getDisplay().isDisposed() || ( event.getVessel() == null ))
				return;
			getDisplay().asyncExec( new Runnable() {

				@Override
				public void run() {
					redraw();
				}			
			});
			
		}
		
	};
	
	private IMIIPEnvironment environment;
	
	private Map<IVessel, List<LatLng>> trajectory;
	
	private boolean disposed;
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PondCanvas(Composite parent, Integer style) {
		super(parent, style);
		this.disposed = false;
		trajectory = new HashMap<>();
		setBackground(Display.getCurrent().getSystemColor( SWT.COLOR_WHITE));
		super.addPaintListener(listener);
	}

	@Override
	public Composite getParent(){
		return super.getParent();
	}
	
	@Override
	public IMIIPEnvironment getInput() {
		return this.environment;
	}

	@Override
	public void setInput( IMIIPEnvironment environment){
		if( this.environment != null )
			this.environment.getInhabitant().getSituationalAwareness().removeListener(slistener);
		
		this.environment = environment;
		if( this.environment != null ) {
			this.environment.getInhabitant().getSituationalAwareness().addListener(slistener);
		}
	}

	protected void drawField( GC gc ){
		if( environment == null )
			return;
		Rectangle clientArea = getClientArea();
		Color color = gc.getForeground();

		try {
			//The raster
			gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_LIGHT_SHADOW ));
			ScalingUtils su = new ScalingUtils( this, this.environment.getField());

			IField field = environment.getField();
			int i = 0;
			while( i < field.getLength() ){
				int xpos = su.scaleXToDisplay( i ); 
				int ypos1 = 0;
				int ypos2 = su.scaleYToDisplay( (int) (field.getWidth() ));
				i += GRIDX;
				gc.drawLine( xpos, ypos1, xpos, ypos2 );
			}
			i =0;
			while( i < field.getWidth()){
				int xpos1 = su.scaleXToDisplay( 0 );
				int xpos2 = su.scaleXToDisplay( (int) field.getLength());
				int ypos = su.scaleYToDisplay( i ); 
				i += GRIDY;
				gc.drawLine( xpos1, ypos, xpos2, ypos );
			}

			//The ship in the centre
			IVessel vessel = (IVessel) this.environment.getInhabitant();
			Point point = ( vessel == null )? new Point( (int)( clientArea.width/2), (int)(clientArea.height/2)):
				su.scaleToCanvas(vessel.getLocation());
			drawLine(gc, vessel, this.environment.getOthers());
			drawOval(gc, vessel, point);
			drawImage( gc, point, MIIPImages.Images.SHIP);

			if(!environment.isInitialised() )
				return;

			for( IPhysical phobj: this.environment.getOthers() ){
				if(( !( phobj instanceof IVessel )) || ( !field.isInField( phobj.getLocation(), 0)))
					continue;
				IVessel other = (IVessel) phobj;
				logger.fine("Distance: " + LatLngUtils.getDistance( vessel.getLocation(), phobj.getLocation()) );
				MIIPImages.Images img = ( other.getHeading() < LatLng.Compass.SOUTH.getAngle() )? MIIPImages.Images.SHIP_GRN: MIIPImages.Images.SHIP_RED;	
				Point otherPoint = su.scaleToCanvas( phobj.getLocation() );
				drawLine(gc, other, null);
				drawOval(gc, other, otherPoint);
				drawImage(gc, otherPoint, img );
			}
		}catch( Exception ex ) {
			ex.printStackTrace();
		}
		gc.setForeground(color);
		gc.dispose();
	}

	protected void drawLine( GC gc, IVessel vessel, Collection<? extends IPhysical> others ){
		int colorCode = vessel.hasCollisionAvoidance()?SWT.COLOR_GREEN: SWT.COLOR_GRAY;
		if( vessel.hasCollisionAvoidance()) {
			double minDistance = getMinDistance(vessel, others);
			if( minDistance < vessel.getCriticalDistance())
				colorCode = SWT.COLOR_DARK_RED;
		}
		gc.setForeground( getDisplay().getSystemColor( colorCode ));
		List<LatLng> list = trajectory.get( vessel );
		if( Utils.assertNull( list )) {
			list = new ArrayList<>();
			trajectory.put(vessel, list);
		}
		list.add( vessel.getLocation());
		if( list.size() == 1)
			return;
		Rectangle clientArea = getClientArea();
		ScalingUtils su = new ScalingUtils( this, this.environment.getField());
		Point defpoint = new Point( (int)( clientArea.width/2), (int)(clientArea.height/2));
		for( int i = 1; i< list.size(); i++ ) {
			LatLng previous = list.get(i-1);
			Point ppoint = ( previous == null )? defpoint: su.scaleToCanvas(previous);
			LatLng next = list.get(i); 
			Point npoint = ( next == null )? defpoint:su.scaleToCanvas(next);
			gc.drawLine(ppoint.x, ppoint.y, npoint.x, npoint.y);
		}
	}

	protected void drawOval( GC gc, IVessel vessel, Point point ){
		if( !vessel.hasCollisionAvoidance())
			return;
		ScalingUtils su = new ScalingUtils( this, this.environment.getField());
		
		int radius = su.scaleXToDisplay((int)vessel.getCriticalDistance()); 
		int transform = (int)( radius/2);
		gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_RED ));
		gc.drawOval(point.x-transform, point.y-transform, radius, radius );

		radius = (int)(su.scaleXToDisplay((int)vessel.getSituationalAwareness().getRange())); 
		radius *= 1.5;
		transform = (int)( radius/2);
		gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_GRAY ));
		gc.drawOval(point.x-transform, point.y-transform, radius, radius );

	}

	protected Image drawImage( GC gc, Point point, MIIPImages.Images image ){
		Image img = null;
		try{
			img = MIIPImages.getImageFromResource( getDisplay(), image );
			Rectangle bounds = img.getBounds();
			Point centre = new Point(( int )( bounds.width/2), (int)( bounds.height/2 ));
			int posx = (point.x-centre.x)<0? 0: point.x-centre.x;
			int posy = (point.y-centre.y)<0? 0: point.y-centre.y;
			gc.drawImage( img, posx, posy);
			img.dispose();
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return img;
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
	
	
	@Override
	public void dispose() {
		this.disposed = true;
		super.dispose();
	}

	protected static double getMinDistance( IPhysical reference, Collection<?extends IPhysical> others ) {
		double minDistance = Double.MAX_VALUE; 
		for( IPhysical phys: others ) {
			double dist = LatLngUtils.getDistance(reference.getLocation(), phys.getLocation());
			if( dist < minDistance)
				minDistance = dist;
		}
		return minDistance;
	}
}
