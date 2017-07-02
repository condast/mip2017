package org.miip.waterway.ui.swt;

import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.openlayer.map.controller.OpenLayerController;

public class MapComposite extends Composite {
	private static final long serialVersionUID = 1L;
	
	private Browser browser;
	private OpenLayerController controller;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MapComposite(Composite parent, Integer style) {
		super(parent, style);
		this.setLayout( new FillLayout());
		this.browser = new Browser( this, style );
		controller = new OpenLayerController(browser);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
