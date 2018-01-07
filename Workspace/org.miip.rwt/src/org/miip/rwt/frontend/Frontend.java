package org.miip.rwt.frontend;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.layout.FillLayout;

import java.util.Collection;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.miip.waterway.ui.factory.ICompositeFactory;
import org.miip.waterway.ui.swt.MiipComposite;

public class Frontend extends Composite {
	private static final long serialVersionUID = 1L;

	private MiipComposite miipComposite;
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public Frontend(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));
		
		TabFolder tabFolder = new TabFolder(this, SWT.NONE);
		
		TabItem tbtmMain = new TabItem(tabFolder, SWT.NONE);
		tbtmMain.setText("Main");
		miipComposite = new MiipComposite(tabFolder, style);
		tbtmMain.setControl(miipComposite);
		
		TabItem tbtmDebug = new TabItem(tabFolder, SWT.NONE);
		tbtmDebug.setText("Debug");
		
		TabItem tbtmLog = new TabItem(tabFolder, SWT.NONE);
		tbtmLog.setText("Log");
	}
	
	public void setInput(Collection<ICompositeFactory> factories) {
		miipComposite.setInput(factories);
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
