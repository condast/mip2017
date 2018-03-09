package org.miip.waterway.ui.swt.pond;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.ui.images.MIIPImages;

import java.util.logging.Logger;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
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

public class PondPresentation extends Canvas implements IInputWidget<IReferenceEnvironment<IPhysical>>{
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

	private IReferenceEnvironment<IPhysical> environment;
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PondPresentation(Composite parent, Integer style) {
		super(parent, style);
		setBackground(Display.getCurrent().getSystemColor( SWT.COLOR_WHITE));
		super.addPaintListener(listener);
	}

	@Override
	public Composite getParent(){
		return super.getParent();
	}
	
	@Override
	public IReferenceEnvironment<IPhysical> getInput() {
		return this.environment;
	}

	@Override
	public void setInput( IReferenceEnvironment<IPhysical> environment){
		this.environment = environment;
	}
	
/*	
	protected Point drawOffset( CentreShip ship, Point centre ){
		if( ship == null )
			return centre;
		Vector<Integer> vector = ship.getOffset();
		if( vector == null )
			return centre;
		//double angle = Math.sin( Math.toRadians( vector.getKey()));
		int xoffset = (int) vector.getValue().doubleValue();//(int)((float)20 * vector.getValue() *  angle );
		int yoffset = vector.getKey();//(int)((float)20 * vector.getValue() *  Math.cos( Math.toRadians( vector.getKey())));
		return new Point( centre.x + xoffset, centre.y + yoffset );
	}
 */

	protected void drawField( GC gc ){
		if( environment == null )
			return;
		Rectangle clientArea = getClientArea();

		try {
			//The ship in the centre
			IVessel vessel = (IVessel) this.environment.getInhabitant();
			ScalingUtils su = new ScalingUtils( this, this.environment.getField());
			Point point = ( vessel == null )? new Point( (int)( clientArea.width/2), (int)(clientArea.height/2)):
				su.scaleToCanvas(vessel.getLocation());
			drawImage( gc, point, MIIPImages.Images.SHIP);

			if(!environment.isInitialsed() )
				return;

			//The raster
			Color color = gc.getForeground();
			gc.setForeground( getDisplay().getSystemColor( SWT.COLOR_WIDGET_LIGHT_SHADOW ));

			Field field = environment.getField();
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
			gc.setForeground(color);

			for( IPhysical phobj: this.environment.getOthers() ){
				if(( !( phobj instanceof IVessel )) || ( !field.isInField( phobj.getLocation(), 0)))
					continue;
				IVessel other = (IVessel) phobj;
				logger.fine("Distance: " + LatLngUtils.getDistance( vessel.getLocation(), phobj.getLocation()) );
				MIIPImages.Images img = ( other.getBearing() < LatLng.Compass.SOUTH.getAngle() )? MIIPImages.Images.SHIP_GRN: MIIPImages.Images.SHIP_RED;	
				drawImage(gc, su.scaleToCanvas( phobj.getLocation() ), img );
			}
		}catch( Exception ex ) {
			ex.printStackTrace();
		}

		gc.dispose();
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
}
