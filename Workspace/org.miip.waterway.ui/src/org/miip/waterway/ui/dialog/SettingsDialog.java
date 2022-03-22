package org.miip.waterway.ui.dialog;


import org.condast.commons.strings.StringStyler;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.miip.waterway.ui.factory.ICompositeFactory;
import org.miip.waterway.ui.swt.SettingsComposite;

public class SettingsDialog extends TitleAreaDialog {
	private static final long serialVersionUID = 1L;


	public enum Fields{
		SETTINGS,
		OK,
		CANCEL;

		@Override
		public String toString(){
			return StringStyler.prettyString( super.toString() );
		}
	/*
		public String getMessage(){
			return ProfileLanguage.getInstance().getMessage( this );
		}
		*/
	}

	protected int result;

	private SettingsComposite composite;

	private ICompositeFactory[] factories;

	/**
	 * Create the dialog.
	 * @param parent
	 * @param style
	 */
	public SettingsDialog( Composite parent, ICompositeFactory[] factories ) {
		super(parent.getDisplay().getActiveShell());
		this.factories = factories;
	}


	@Override
	protected Point getInitialSize() {
		return new Point( 600, 600 );
	}


	@Override
	protected void configureShell(Shell newShell) {
		newShell.setText( Fields.SETTINGS.toString() );
		super.configureShell(newShell);
	}


	/**
	 * Create contents of the dialog.
	 */
	@Override
	protected Control createDialogArea( Composite parent ) {
		setTitle( Fields.SETTINGS.toString() );

	    Composite area = (Composite) super.createDialogArea(parent);
		composite = new SettingsComposite( area, SWT.NONE );
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		composite.setInput(factories );
		return area;
	}

	@Override
	protected Control createButtonBar(Composite parent) {
		Control  buttonBar = super.createButtonBar(parent);
		Button button = super.getButton(IDialogConstants.OK_ID);
		button.setText( Fields.OK.toString() );
		super.getButton(IDialogConstants.CANCEL_ID).setText( Fields.CANCEL.toString() );
		return buttonBar;
	}

	public ICompositeFactory[] getInput(){
		return factories;
	}


	@Override
	protected void okPressed() {
		super.okPressed();
	}


}
