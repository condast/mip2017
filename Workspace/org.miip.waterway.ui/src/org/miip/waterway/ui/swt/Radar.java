package org.miip.waterway.ui.swt;

import java.util.Iterator;
import java.util.Map;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.miip.waterway.sa.SituationalAwareness;

public class Radar extends Canvas {
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
	
	public Radar(Composite parent, int style) {
		super(parent, style);
		setBackground(Display.getCurrent().getSystemColor( SWT.COLOR_WHITE));
		super.addPaintListener( listener );
	}

	protected void drawField( GC gc ){
		if( sa == null )
			return;
		Iterator<Map.Entry<Integer, Double>> iterator = sa.getRadar().entrySet().iterator();
		while( iterator.hasNext() ){
			Map.Entry<Integer, Double> entry = iterator.next();
			drawDegree(gc, entry.getKey(), entry.getValue(), 20);
		}
	}

	protected Color getColour( double distance ){
		int colour = SWT.COLOR_BLACK;
		if( sa == null)
			return getDisplay().getSystemColor( colour );
		double range = sa.getRange();
		double relax = ( Math.abs( distance)> range )? 1: Math.abs( distance/range );
		int red = (int)( 255 * (1-relax*relax ));
		int green = (int)( 255 * relax*relax );
		return new Color (getDisplay(), red, green, 0);
	}
	
	protected void drawDegree( GC gc, int angle, double distance, int bar ){
		Rectangle clientArea = getClientArea();
		double centrex = clientArea.width/2;
		double centrey = clientArea.height/2;
		double length = Math.sqrt( centrex * centrex + centrey * centrey);
		double offset = distance * length/sa.getRange();
		
		double xpos1 = centrex + offset * Math.sin( Math.toRadians( angle ));
		double ypos1 = centrey + offset * Math.cos( Math.toRadians( angle ));
		gc.setForeground( getColour( distance ));
		//gc.fillPolygon(new int[]{(int) centrex, (int)centrey, (int)xpos1, (int)ypos1, (int)xpos2, (int)ypos2});
		gc.drawLine((int)centrex, (int)centrey, (int)xpos1, (int)ypos1);
		
	}
	
	public void setInput( SituationalAwareness sa ){
		this.sa = sa;
		this.redraw();
	}

}
