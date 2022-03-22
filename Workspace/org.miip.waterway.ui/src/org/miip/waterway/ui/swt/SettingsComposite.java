package org.miip.waterway.ui.swt;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.miip.waterway.ui.factory.ICompositeFactory;

public class SettingsComposite extends Composite {
	private static final long serialVersionUID = 1L;

	private TabFolder tabFolder;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public SettingsComposite(Composite parent, int style) {
		super(parent, style);
		setLayout(new FillLayout(SWT.HORIZONTAL));

		tabFolder = new TabFolder(this, SWT.NONE);

	}

	public void setInput( ICompositeFactory[] factories ){
		for( ICompositeFactory factory: factories ){
			Composite composite = factory.createComposite( tabFolder, SWT.NONE);
			TabItem tbtmRadar = new TabItem(tabFolder, SWT.NONE);
			tbtmRadar.setText( factory.getName());
			tbtmRadar.setControl(composite);
			composite.setLayout(new GridLayout(2, false));
		}
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
