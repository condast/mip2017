package org.miip.waterway.ui.swt;

import java.util.Comparator;
import java.util.logging.Logger;

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
import org.miip.waterway.sa.ISituationListener;
import org.miip.waterway.sa.ISituationalAwareness;
import org.miip.waterway.sa.SituationEvent;

public abstract class AbstractSWTRadar<V extends Object> extends Canvas implements IRadar<V>{
	private static final long serialVersionUID = 1L;
	
	public enum RadarColours{
		NONE( SWT.COLOR_TRANSPARENT),
		TRANSPARANT( SWT.COLOR_TRANSPARENT),
		GREEN( SWT.COLOR_GREEN ),
		YELLOW( SWT.COLOR_YELLOW ),
		ORANGE( SWT.COLOR_RED | SWT.COLOR_YELLOW ),
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
			return colour;
		}
		
		public static Color getColour( Device device, RadarColours rc ){
			Color colour = null;
			switch( rc ){
			case ORANGE:
				colour = new Color (device, 225, 113, 0);
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

		public static Color getLinearColour( Device device, int distance, long range, int sensitivity ){
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

	private ISituationalAwareness<V,?> sa;
	private ISituationListener<V> slistener = new ISituationListener<V>() {
		
		@Override
		public void notifySituationChanged(SituationEvent<V> event) {
			getDisplay().asyncExec( new Runnable() {

				@Override
				public void run() {
					redraw();
				}
			});
		}
	};
	
	private int steps;
	private Radar<V> radar;

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	protected AbstractSWTRadar(Composite parent, int style) {
		super(parent, style);
		radar = new Radar<V>();
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
	public long getRange() {
		return radar.getRange();
	}

	@Override
	public void setRange(int range) {
		radar.setRange(range);
	}

	protected int getSteps() {
		return steps;
	}

	protected void setSteps(int steps) {
		this.steps = steps;
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
	
	/**
	 * Get the radians for the given step size
	 * @param step
	 * @return
	 */
	protected double toRadians( int step ){
		double part = (double)step/steps;
		return 2*Math.PI*part;
	}

	protected void onDrawStart( GC gc ){
		logger.fine( "Radar settings: rage = " + radar.getRange() + ", sensitivity = " + radar.getSensitivity() );
	}
	
	protected void onDrawEnd( GC gc ){/* NOTHING */ }

	protected void drawField( GC gc ){
		if( sa == null )
			return;
		this.onDrawStart(gc);
		for( V obj: sa.getRadar() ){
			drawObject(gc, obj );
		}
		this.onDrawEnd(gc);
	}

	protected Color getColour( double distance ){
		int colour = SWT.COLOR_BLACK;
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
	
	protected abstract void drawObject( GC gc, V object );

	protected ISituationalAwareness<V,?> getInput() {
		return sa;
	}

	@Override
	public void setInput( ISituationalAwareness<V,?> sa ){
		if( this.sa != null ) {
			if( this.sa.equals(sa))
				return;
			this.sa.removelistener(slistener);
		}
		this.sa = sa;
		if( sa != null ) {
			this.sa.addlistener(slistener);
			radar.setRange( (int) sa.getField().getLength());
		}
		refresh();
	}

	public void refresh(){
		if( Display.getCurrent().isDisposed() )
			return;
		Display.getCurrent().asyncExec( new Runnable(){

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
