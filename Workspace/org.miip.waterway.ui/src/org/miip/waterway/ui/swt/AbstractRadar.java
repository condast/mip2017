package org.miip.waterway.ui.swt;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

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
import org.miip.waterway.sa.SituationalAwareness;

public abstract class AbstractRadar extends Canvas implements IRadarUI{
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

		public static Color getLinearColour( Device device, int distance, int range, int sensitivity ){
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

	private SituationalAwareness sa;
	
	private int range;
	private int sensitivity; //part of the range

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	protected AbstractRadar(Composite parent, int style) {
		super(parent, style);
		this.sensitivity = DEFAULT_SENSITIVITY;
		this.range = DEFAULT_RANGE;
		setBackground(Display.getCurrent().getSystemColor( SWT.COLOR_DARK_GRAY));
		super.addPaintListener( listener );
	}

	protected SituationalAwareness getSituationalAwareness() {
		return sa;
	}

	@Override
	public int getSensitivity() {
		return sensitivity;
	}

	@Override
	public void setSensitivity( int sensitivity) {
		this.sensitivity = sensitivity;
	}

	@Override
	public int getRange() {
		return range;
	}

	@Override
	public void setRange(int range) {
		this.range = range;
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
		double part = (double)step/sa.getSteps();
		return 2*Math.PI*part;
	}

	protected void onDrawStart( GC gc ){
		logger.info( "Radar settings: rage = " + this.range + ", sensitivity = " + this.sensitivity );
	}
	
	protected void onDrawEnd( GC gc ){/* NOTHING */ }

	protected void drawField( GC gc ){
		if( sa == null )
			return;
		this.onDrawStart(gc);
		TreeMap<Integer, Double> drawMap = new TreeMap<Integer, Double>( sa.getRadar());
		logger.info( "Radar values found: " + drawMap.size() );
		Iterator<Map.Entry<Integer, Double>> iterator = drawMap.descendingMap().entrySet().iterator();
		while( iterator.hasNext() ){
			Map.Entry<Integer, Double> entry = iterator.next();
			if( entry.getValue() <= range )
				drawDegree(gc, entry.getKey(), entry.getValue());
		}
		this.onDrawEnd(gc);
	}

	protected Color getColour( double distance ){
		int colour = SWT.COLOR_BLACK;
		if( sa == null)
			return getDisplay().getSystemColor( colour );
		
		if( distance <= this.sensitivity )
			return getDisplay().getSystemColor( SWT.COLOR_RED );
		if( distance > this.range )
			return getDisplay().getSystemColor( SWT.COLOR_TRANSPARENT );
		//int index = (int)(( this.range - this.sensitivity )/distance );
		return RadarColours.getLinearColour(getDisplay(), (int) distance, this.range, (int) this.sensitivity );
		//double relax = ( Math.abs( distance)> sa.getRange() )? 1: Math.abs( distance/sa.getRange() );
		//int red = (int)( 255 * (1-relax*relax ));
		//int green = (int)( 255 * relax*relax );
		//return new Color (getDisplay(), red, green, 0);
	}
	
	protected abstract void drawDegree( GC gc, int angle, double distance );
	
	@Override
	public void setInput( SituationalAwareness sa ){
		this.sa = sa;
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
}
