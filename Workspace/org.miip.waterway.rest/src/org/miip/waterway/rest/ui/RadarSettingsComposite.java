package org.miip.waterway.rest.ui;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.miip.waterway.radar.IRadarData;
import org.miip.waterway.radar.IRadarData.Choices;
import org.miip.waterway.rest.service.Dispatcher;
import org.miip.waterway.rest.store.RadarOptions;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Spinner;

public class RadarSettingsComposite extends Composite {
	private static final long serialVersionUID = 1L;

	private Combo radarCombo;
	private Spinner rangeSpinner;
	private Spinner senseSpinner;
	private Spinner transparencySpinner;

	private Dispatcher dispatcher = Dispatcher.getInstance();
	private RadarOptions settings;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public RadarSettingsComposite(Composite parent, int style) {
		super(parent, style);
		settings = dispatcher.getOptions();
		setLayout(new GridLayout(2, false));
		
		Label lblView = new Label(this, SWT.NONE);
		lblView.setText("View:");
		
		radarCombo = new Combo(this, SWT.NONE);
		radarCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		radarCombo.setItems( IRadarData.Choices.getItems());
		radarCombo.select(0);
		radarCombo.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				Combo combo = (Combo) e.widget;
				settings.setChoice( Choices.values()[combo.getSelectionIndex()]);
				super.widgetSelected(e);
			}
		});
		Label lblRange = new Label(this, SWT.NONE);
		lblRange.setText("Range:");
		
		rangeSpinner = new Spinner(this, SWT.BORDER);
		rangeSpinner.setPageIncrement(1);
		rangeSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		rangeSpinner.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner spinner = (Spinner) e.widget;
				settings.setRange( spinner.getSelection() );
				super.widgetSelected(e);
			}
		});
		
		Label lblSensitivity = new Label(this, SWT.NONE);
		lblSensitivity.setText("Sensitivity:");
		
		this.senseSpinner = new Spinner(this, SWT.BORDER);
		senseSpinner.setPageIncrement(1);
		senseSpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		senseSpinner.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner spinner = (Spinner) e.widget;
				settings.setSensitivity( spinner.getSelection() );
				super.widgetSelected(e);
			}
		});

		Label lblTransparency = new Label(this, SWT.NONE);
		lblTransparency.setText("Transparency:");
		
		this.transparencySpinner = new Spinner(this, SWT.BORDER);
		transparencySpinner.setPageIncrement(1);
		transparencySpinner.setMinimum(0);
		transparencySpinner.setMaximum(100);
		transparencySpinner.setSelection(0);
		transparencySpinner.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		transparencySpinner.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				Spinner spinner = (Spinner) e.widget;
				settings.setTransparency( spinner.getSelection() );
				super.widgetSelected(e);
			}
		});
		setInput( settings );
	}

	private void setInput( RadarOptions settings ){
		this.radarCombo.select( settings.getChoice().ordinal());
		this.rangeSpinner.setSelection( settings.getRange());
		this.senseSpinner.setSelection( settings.getSensitivity());
		this.transparencySpinner.setSelection( settings.getTransparency());
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
