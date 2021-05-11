package org.miip.waterway.ui.swt;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.data.colours.RGBA;
import org.condast.commons.ui.xy.AbstractXYMap;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;

public class PixelXYMap extends AbstractXYMap<Integer, RGBA> {
	private static final long serialVersionUID = 1L;
		
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
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
		gc.fillRectangle( xpos, ypos, xpos-xprev+1, ypos-yprev+1);
	}

	public void setInput( RGBA[] colours, int size) {
		getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				try {
					Map<Integer, List<RGBA>> input = getInput();
					input.clear();
					for( int i=0; i<colours.length; i++ ) {
						int row = (int)((double)i/size);
						List<RGBA> elements = input.get(row);
						if( elements == null ) {
							elements = new ArrayList<>();
							input.put(row, elements );
						}
						elements.add(colours[i]);
					}
					System.err.println( input.size());
				} catch (Exception e) {
					e.printStackTrace();
				}
				refresh();				
			}	
		});
	}
}