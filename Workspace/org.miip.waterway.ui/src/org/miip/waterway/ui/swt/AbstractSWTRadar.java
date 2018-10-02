package org.miip.waterway.ui.swt;

import java.util.Comparator;
import java.util.logging.Logger;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationListener;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.data.latlng.Vector;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.miip.waterway.model.def.IRadar;
import org.miip.waterway.radar.Radar;

public abstract class AbstractSWTRadar<V,O extends IPhysical> extends Canvas implements IRadar<V,O>{
	private static final long serialVersionUID = 1L;
	
	public enum RadarColours{
		NONE( SWT.COLOR_TRANSPARENT),
		TRANSPARANT( SWT.COLOR_TRANSPARENT),
		GREEN( SWT.COLOR_GREEN ),
		YELLOW( SWT.COLOR_YELLOW ),
		ORANGE( SWT.COLOR_RED | SWT.COLOR_YELLOW ),
		DARK_ORANGE( SWT.COLOR_RED | SWT.COLOR_YELLOW | 1),
		RED( SWT.COLOR_RED );
		
		private int index;
		
		private RadarColours( int index ){
			this.index = index;
		}
		
		public int getIndex(){
			return this.index;
		}
		
		public static RadarColours getColour( int index ){
			RadarColours colour;
			switch( index ){
			case SWT.COLOR_GREEN:
				colour = RadarColours.GREEN;
				break;
			case SWT.COLOR_RED:
				colour = RadarColours.RED;
				break;
			case SWT.COLOR_YELLOW:
				colour = RadarColours.YELLOW;
				break;
			default:
				colour = RadarColours.TRANSPARANT;
				break;
			}
			if( RadarColours.ORANGE.getIndex() == index )
				colour = RadarColours.ORANGE;
			else if( RadarColours.DARK_ORANGE.getIndex() == index )
				colour = RadarColours.DARK_ORANGE;
			return colour;
		}
		
		public static Color getColour( Device device, RadarColours rc ){
			Color colour = null;
			switch( rc ){
			case ORANGE:
				colour = new Color (device, 225, 113, 0);
			case DARK_ORANGE:
				colour = new Color (device, 215, 70, 0);
			default:
				colour = device.getSystemColor( rc.getIndex());
				break;
			}
			return colour;
		}

		public static Color getColour( Device device, int index ){
			RadarColours colour = RadarColours.values()[ index+1 ];
			return getColour( device, colour);
		}

		public static Color getLinearColour( Device device, int distance, double range, int sensitivity ){
			boolean far = ( distance > ( range - sensitivity ));
			int red = far? 50: (int)( 255 * ( 1 - distance/range ));
			int green = far? 255: (int)( 255 * distance/range );
			int blue = 50;
			return new Color( device, red, green, blue );
		}
	}
	
	private PaintListener listener = new PaintListener(){
		private static final long serialVersionUID = 1L;

		@Override
		public void paintControl(PaintEvent event) {
			drawField( event.gc );
		}
	};

	private ISituationalAwareness<V,O> sa;
	private ISituationListener<O> slistener = new ISituationListener<O>() {
		
		@Override
		public void notifySituationChanged(SituationEvent<O> event) {
			onPrepare( event );
			getDisplay().asyncExec( new Runnable() {

				@Override
				public void run() {
					redraw();
				}
			});
		}
	};
	
	private Radar<V,O> radar;

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	protected AbstractSWTRadar(Composite parent, int style) {
		super(parent, style);
		radar = new Radar<V,O>();
		setBackground(Display.getCurrent().getSystemColor( SWT.COLOR_DARK_GRAY));
		super.addPaintListener( listener );
	}

	@Override
	public int getSensitivity() {
		return radar.getSensitivity();
	}

	@Override
	public void setSensitivity( int sensitivity) {
		radar.setSensitivity(sensitivity);
	}

	@Override
	public double getRange() {
		return radar.getRange();
	}

	@Override
	public void setRange(double range) {
		radar.setRange(range);
	}

	public int getSteps() {
		return radar.getSteps();
	}

	public void setSteps(int steps) {
		this.radar.setSteps(steps);
	}

	@Override
	public double toRadians(int step) {
		return radar.toRadians(step);
	}

	/**
	 * Get the centre of the radar
	 * @return
	 */
	protected Point getCentre(){
		Rectangle clientArea = getClientArea();
		double centrex = clientArea.width/2;
		double centrey = clientArea.height/2;
		return new Point((int) centrex, (int)centrey);
	}
	
	protected void onPrepare( SituationEvent<O> event ) {
		/* DEFAULT NOTHING */
	}
	
	protected void onDrawStart( GC gc ){
		logger.fine( "Radar settings: rage = " + radar.getRange() + ", sensitivity = " + radar.getSensitivity() );
	}
	
	protected void onDrawEnd( GC gc ){/* NOTHING */ }

	protected void drawField( GC gc ){
		if( sa == null )
			return;
		this.onDrawStart(gc);
		for( O obj: sa.getRadar() ){
			drawObject(gc, obj );
		}
		this.onDrawEnd(gc);
	}

	protected Color getColour( double distance ){
		int colour = SWT.COLOR_GREEN;
		if( sa == null)
			return getDisplay().getSystemColor( colour );
		
		if( distance <= radar.getSensitivity() )
			return getDisplay().getSystemColor( SWT.COLOR_RED );
		if( distance > radar.getRange() )
			return getDisplay().getSystemColor( SWT.COLOR_TRANSPARENT );
		//int index = (int)(( radar.getRange() - radar.getSensitivity() )/distance );
		return RadarColours.getLinearColour(getDisplay(), (int) distance, radar.getRange(), (int) radar.getSensitivity() );
		//double relax = ( Math.abs( distance)> sa.getRange() )? 1: Math.abs( distance/sa.getRange() );
		//int red = (int)( 255 * (1-relax*relax ));
		//int green = (int)( 255 * relax*relax );
		//return new Color (getDisplay(), red, green, 0);
	}
	
	protected abstract void drawObject( GC gc, O object );

	@Override
	public ISituationalAwareness<V,O> getInput() {
		return sa;
	}

	@Override
	public void setInput( ISituationalAwareness<V,O> sa, boolean setRange ){
		if( this.sa != null ) {
			if( this.sa.equals(sa))
				return;
			this.sa.removelistener(slistener);
		}
		this.sa = sa;
		radar.setInput(sa);
		if( sa != null ) {
			this.sa.addlistener(slistener);
		}
		refresh();
	}

	@Override
	public void setInput( ISituationalAwareness<V,O> sa ){
		setInput( sa, false );
	}
	
	public void refresh(){
		if(( getDisplay() == null ) || getDisplay().isDisposed() )
			return;
		getDisplay().asyncExec( new Runnable(){

			@Override
			public void run() {
				if( isDisposed())
					return;
				redraw();
			}
			
		});
	}
		
	public void dispose() {
		if( this.sa != null )
			this.sa.removelistener(slistener);
		super.dispose();
	}

	protected class VectorComparator implements Comparator<Vector<Double>> {

		@Override
		public int compare(Vector<Double> arg0, Vector<Double> arg1) {
			return (int)( arg0.getValue() - arg1.getValue());
		}	
	}
}
