package org.miip.rwt.entry;

import java.util.Collection;

import org.eclipse.jface.window.Window;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.miip.rwt.service.Dispatcher;
import org.miip.waterway.ui.dialog.SettingsDialog;
import org.miip.waterway.ui.factory.ICompositeFactory;

public class SettingsEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	private Button settingsButton;
	private Dispatcher dispatcher = Dispatcher.getInstance();

	@Override
    protected void createContents( Composite parent) {
		parent.setLayout( new FillLayout());
 		settingsButton = new Button(parent, SWT.ARROW | SWT.DOWN);
		settingsButton.addSelectionListener(new SelectionAdapter() {
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					Composite modal = dispatcher.getMiipComposite();
					if( modal.getDisplay().isDisposed())
						return;
					modal.getDisplay().asyncExec( new Runnable() {

						@Override
						public void run() {
							Collection<ICompositeFactory> factories= dispatcher.getFactories().values();
							SettingsDialog dialog = new SettingsDialog( dispatcher.getMiipComposite(), factories.toArray( new ICompositeFactory[ factories.size()]) );
							if( Window.OK == dialog.open()){

							}
						}

					});
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