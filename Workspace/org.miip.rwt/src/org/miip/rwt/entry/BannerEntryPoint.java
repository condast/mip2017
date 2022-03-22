package org.miip.rwt.entry;

import java.util.Collection;

import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.ui.banner.Banner;
import org.miip.waterway.ui.factory.ICompositeFactory;

public class BannerEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	private Banner banner;

	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(1, false));
		banner = new Banner(parent, SWT.FULL_SELECTION);
		banner.setLayoutData( new GridData(SWT.TOP,  SWT.FILL, true, true ));
    }

	public void setFactories( Collection<ICompositeFactory> factories ){
		//this.factories = factories;
		//banner.setFactories( this.factories);
	}

}