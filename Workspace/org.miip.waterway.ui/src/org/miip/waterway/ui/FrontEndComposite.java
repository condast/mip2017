package org.miip.waterway.ui;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.model.eco.MIIPEnvironment;
import org.miip.waterway.ui.swt.MIIPComposite;

/**
 * @author Kees
 *
 */
public class FrontEndComposite extends Composite {
	private static final long serialVersionUID = 1L;
	
	private MIIPComposite mcomp;
	
	public FrontEndComposite( Composite parent, int style) {
		super(parent, style);
		this.createComposite( parent, style );
	}

	private void createComposite(Composite parent, int style) {
		setLayout(new GridLayout(1, false));

		mcomp = new MIIPComposite( this, style );
		mcomp.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true));
	}
	
	public void setInput( IMIIPEnvironment environment ){
		mcomp.setInput((MIIPEnvironment) environment);
	}
	
	protected void refresh(){
		layout(false);
	}
}