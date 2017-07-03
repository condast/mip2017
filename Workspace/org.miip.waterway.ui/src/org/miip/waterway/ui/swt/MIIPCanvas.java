package org.miip.waterway.ui.swt;

import org.eclipse.swt.widgets.Canvas;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.PaintEvent;
import org.eclipse.swt.events.PaintListener;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;

public class MIIPCanvas extends Canvas {
	private static final long serialVersionUID = 1L;

	public static final int BANK_SIZE = 80;
	
	private PaintListener listener = new PaintListener(){
		private static final long serialVersionUID = 1L;

		@Override
		public void paintControl(PaintEvent event) {
			drawField( event.gc );
		}
	};
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MIIPCanvas(Composite parent, Integer style) {
		super(parent, style);
		setBackground(Display.getCurrent().getSystemColor( SWT.COLOR_WHITE));
		super.addPaintListener(listener);
	}

	protected void drawField( GC gc ){
        Display display = super.getDisplay();
		Rectangle clientArea = getClientArea();
        gc.drawLine( 0, BANK_SIZE, clientArea.width, BANK_SIZE );
        
        Rectangle rect = new Rectangle(0, 0, clientArea.width, BANK_SIZE);
        gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN));
        gc.fillRectangle( rect );
        
        rect = new Rectangle(0, clientArea.height-BANK_SIZE, clientArea.width, clientArea.height );
        gc.setBackground(display.getSystemColor(SWT.COLOR_DARK_GREEN ));
        gc.fillRectangle( rect );
        gc.drawLine( 0, clientArea.height-BANK_SIZE, clientArea.width, clientArea.height-BANK_SIZE);
 	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
