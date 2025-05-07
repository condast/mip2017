package org.miip.waterway.ui.swt;

import java.util.List;
import java.util.Map;

import org.condast.commons.data.colours.RGBA;
import org.condast.commons.ui.widgets.xy.AbstractXYMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

public class PixelXYMap extends AbstractXYMap<Integer, RGBA> {
	private static final long serialVersionUID = 1L;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PixelXYMap(Composite parent, int style) {
		super(parent, style);
	}

	@Override
	protected Color onSetForeground(GC gc) {
		return getDisplay().getSystemColor(SWT.COLOR_BLUE);
	}

	@Override
	protected Color onSetBackground(GC gc) {
		return getDisplay().getSystemColor(SWT.COLOR_BLACK);
	}

	@Override
	protected Integer getKey(int index) {
		return index;
	}

	@Override
	protected void drawObject(GC gc, int xprev, int yprev, int xpos, int ypos, Integer key, RGBA value) {
		if( value != null )
			gc.setBackground( new Color( getDisplay(), value.getRed(), value.getGreen(), value.getBlue()));
		gc.fillRectangle( xprev, yprev, xpos-xprev, ypos-yprev);
	}

	public void setLayer( Map<Integer,List<RGBA>> colours) {
		getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				try {
					setInput(colours);
				} catch (Exception e) {
					e.printStackTrace();
				}
				refresh();
			}
		});
	}
}