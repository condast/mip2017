package org.miip.waterway.ui;

import org.eclipse.swt.widgets.Composite;
import org.openlayer.map.controller.OpenLayerController;
import org.eclipse.swt.custom.SashForm;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.browser.Browser;

public class MIIPFrontend extends Composite {
	private static final long serialVersionUID = 1L;
	
	private OpenLayerController controller;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MIIPFrontend(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		SashForm sashForm = new SashForm(this, SWT.VERTICAL);
		
		Browser browser = new Browser(sashForm, SWT.NONE);
		controller = new OpenLayerController( browser );
		
		new Composite(sashForm, SWT.NONE);
		sashForm.setWeights(new int[] {175, 122});
	}

	
	public OpenLayerController getController() {
		return controller;
	}


	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
