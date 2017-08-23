package org.miip.rwt;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.ui.FrontEndComposite;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        Composite frontend = new FrontEndComposite( parent, SWT.NONE );
        frontend.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
    }
}
