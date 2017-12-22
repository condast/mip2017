package org.miip.rwt;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.miip.rwt.frontend.Frontend;
import org.miip.rwt.service.Dispatcher;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
        Frontend frontend = new Frontend( parent, SWT.NONE );
        frontend.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
        Dispatcher.getInstance().startApplication(frontend);
    }
}
