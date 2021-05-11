package org.miip.waterway.ui.swt;

import java.util.Arrays;

import org.condast.commons.data.colours.RGBA;
import org.condast.commons.ui.xy.AbstractXYGraph;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

public class PixelXYGraph extends AbstractXYGraph<RGBA> {
	private static final long serialVersionUID = 1L;
		
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PixelXYGraph(Composite parent, int style) {
		super(parent, style);
	}
	
	@Override
	protected int onGetXValue(int xpos, int length) {
		return (int)((double)xpos * size()/length);
	}

	@Override
	protected Color onSetForeground(GC gc) {
		return getDisplay().getSystemColor(SWT.COLOR_BLUE);
	}

	@Override
	protected Color onSetBackground(GC gc) {
		return getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}

	public void setInput( RGBA[] colours) {
		getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				try {
					setInput( Arrays.asList( colours));
					setMaxValue(colours.length);
				} catch (Exception e) {
					e.printStackTrace();
				}		
			}		
		});
	}
	
	@Override
	protected int onPaint(GC gc, int xprev, int yprev, int xcor, int key, RGBA value) {
		if( value != null )
			gc.setBackground( new Color( getDisplay(), value.getRed(), value.getGreen(), value.getBlue()));
		gc.fillRectangle( xcor, 0, 1, yprev );
		return 0;
	}

	@Override
	protected void onCompleted(GC gc) {
		// TODO Auto-generated method stub
		
	}

}
