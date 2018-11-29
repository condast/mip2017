package org.miip.rwt.entry;

import java.util.Collection;

import org.condast.commons.ui.image.DashboardImages;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Layout;
import org.miip.waterway.ui.dialog.SettingsDialog;
import org.miip.waterway.ui.factory.ICompositeFactory;

public class SettingsEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	private Button settingsButton;
	private Composite parent;
	
	@Override
    protected void createContents(final Composite parent) {
		this.parent = parent;
		parent.setLayout( new FillLayout());
 		settingsButton = new Button(parent, SWT.ARROW);
		settingsButton.setImage( DashboardImages.getImage( DashboardImages.Images.SETTINGSGREEN ));
		settingsButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					SettingsDialog dialog = new SettingsDialog( parent, null);//factories.toArray( new ICompositeFactory[ factories.size()]) );
					if( Dialog.OK == dialog.open()){
						
					}
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}			
		});
	}
	
	public void setFactories( Collection<ICompositeFactory> factories ){
		//this.factories = factories;
		//banner.setFactories( this.factories);
	}
	
}