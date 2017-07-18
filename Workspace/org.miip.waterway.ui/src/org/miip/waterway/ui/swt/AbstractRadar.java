package org.miip.waterway.ui.swt;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.miip.waterway.sa.SituationalAwareness;

public abstract class AbstractRadar extends Canvas implements IRadarUI{
	private static final long serialVersionUID = 1L;

	
	private PaintListener listener = new PaintListener(){
		private static final long serialVersionUID = 1L;

		@Override
		public void paintControl(PaintEvent event) {
			drawField( event.gc );
		}
	};

	private SituationalAwareness sa;
	
	private int range;
	private float sensitivity;
	
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
	public float getSensitivity() {
		return sensitivity;
	}

	@Override
	public void setSensitivity( int sensitivity) {
		this.sensitivity = (float)sensitivity/1000;
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

	protected void onDrawStart( GC gc ){/* NOTHING */ }
	protected void onDrawEnd( GC gc ){/* NOTHING */ }

	protected void drawField( GC gc ){
		if( sa == null )
			return;
		this.onDrawStart(gc);
		TreeMap<Integer, Double> drawMap = new TreeMap<Integer, Double>( sa.getRadar());
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
		
		if( distance > this.sensitivity * this.range )
			return getDisplay().getSystemColor( SWT.COLOR_GREEN );
		double relax = ( Math.abs( distance)> sa.getRange() )? 1: Math.abs( distance/sa.getRange() );
		int red = (int)( 255 * (1-relax*relax ));
		int green = (int)( 255 * relax*relax );
		return new Color (getDisplay(), red, green, 0);
	}
	
	protected abstract void drawDegree( GC gc, int angle, double distance );
	
	@Override
	public void setInput( SituationalAwareness sa ){
		this.sa = sa;
		this.redraw();
	}
	
	public void refresh(){
		this.redraw();
	}
}
