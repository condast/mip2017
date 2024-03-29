package org.miip.waterway.ui.radar;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.IRadar;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IMIIPRadar;
import org.miip.waterway.ui.swt.AveragingRadar;
import org.miip.waterway.ui.swt.DirectRadar;
import org.miip.waterway.ui.swt.HumanAssist;
import org.miip.waterway.ui.swt.LedRing;
import org.miip.waterway.ui.swt.LedRingRest;
import org.miip.waterway.ui.swt.pond.PredictiveRadar;

public class RadarGroup extends Group {
	private static final long serialVersionUID = 1L;

	private IRadar radar;
	private Combo combo_radar;
	private Slider slider_sense;
	private Label lbl_sense;
	private Slider slider_range;
	private Label lbl_range;
	private Composite composite;
	private Composite comp_radar;

	private ISituationalAwareness<VesselRadarData> sa;
	private Label lblNewLabel;
	private Label lblRange;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public RadarGroup(Composite parent, int style) {
		super(parent, style);
		this.createComposite(parent, style);
	}

	protected void createComposite( Composite parent, int style ) {
		setText("Radar");
		setLayout(new GridLayout(2, true));

		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));
		combo_radar = new Combo( composite, SWT.BORDER );
		combo_radar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		combo_radar.setSize(361, 23);
		combo_radar.setItems( IMIIPRadar.RadarSelect.getItems() );
		combo_radar.select( IMIIPRadar.RadarSelect.WARP.ordinal() );

		combo_radar.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				for( Control child: comp_radar.getChildren() )
					child.dispose();
				getDisplay().asyncExec( new Runnable(){

					@Override
					public void run() {
						try{
							switch( IMIIPRadar.RadarSelect.getRadar( combo_radar.getSelectionIndex())){
							case WARP:
								radar = new DirectRadar(comp_radar, SWT.BORDER);
								break;
							case AVERAGE:
								AveragingRadar<IVessel> avr = new AveragingRadar<>(comp_radar, SWT.BORDER);
								//avr.setExpand( 1);
								radar = avr;
								break;
							case LED_RING:
								LedRing<IPhysical> ledring = new LedRing<>(comp_radar, SWT.BORDER);
								//avr.setExpand( 1);
								radar = ledring;
								break;
							case LED_RING_REST:
								LedRingRest<IPhysical> ledringrest = new LedRingRest<>(comp_radar, SWT.BORDER);
								//avr.setExpand( 1);
								radar = ledringrest;
								break;
							case POND:
								PredictiveRadar<IVessel> pondr = new PredictiveRadar<>(comp_radar, SWT.BORDER);
								//avr.setExpand( 1);
								//radar = pondr;
								break;
							default:
								radar = new HumanAssist<IVessel>( comp_radar, SWT.BORDER );
								break;
							}
							//radar.setInput(sa);
							radar.setRange( slider_range.getSelection());
							radar.setSensitivity( slider_sense.getSelection());
							radar.refresh();
							comp_radar.layout();
						}
						catch( Exception ex ){
							ex.printStackTrace();
						}
					}
				});
				super.widgetSelected(e);
			}
		});
		combo_radar.select(IMIIPRadar.RadarSelect.LED_RING.ordinal());

		lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Sensitivity:");

		slider_sense = new Slider( composite, SWT.BORDER );
		GridData gd_slider_sense = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		slider_sense.setLayoutData(gd_slider_sense);
		slider_sense.setMinimum(0);
		slider_sense.setMaximum(100);
		slider_sense.setSelection( IMIIPRadar.DEFAULT_SENSITIVITY );
		slider_sense.setIncrement(1);
		slider_sense.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					if( radar != null )
						radar.setSensitivity( slider_sense.getSelection());
					lbl_sense.setText( String.valueOf( sa.getSensitivity()));
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});

		lbl_sense = new Label( composite, SWT.BORDER );
		GridData gd_lbl_sense = new GridData( SWT.FILL, SWT.FILL, true, false );
		lbl_sense.setLayoutData( gd_lbl_sense);

		lblRange = new Label(composite, SWT.NONE);
		lblRange.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblRange.setText("Range:");

		slider_range = new Slider(composite, SWT.BORDER );
		GridData gd_slider_range = new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1);
		slider_range.setLayoutData(gd_slider_range);
		slider_range.setMinimum(1);
		slider_range.setMaximum(3000);
		slider_range.setSelection( IMIIPRadar.DEFAULT_RANGE );
		slider_range.setIncrement(5);
		slider_range.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					if( radar != null )
						radar.setRange( slider_range.getSelection());
					lbl_range.setText( String.valueOf( sa.getRange() ));
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
		lbl_range = new Label( composite, SWT.BORDER );
		lbl_range.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, false, false, 1, 1));
		comp_radar = new Composite( this, SWT.NONE);
		comp_radar.setLayout(new FillLayout());
		comp_radar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		radar = new LedRing<IVessel>( comp_radar, SWT.BORDER );
	}

	public void setInput( ISituationalAwareness<VesselRadarData> sa, boolean overwriteRange ) {
		this.sa = sa;
		//this.radar.setInput( sa );
		if( sa != null ) {
			if( overwriteRange ) {
				//if( sa.getView() != null ){
				//	this.slider_range.setMaximum( (int) (sa.getView().getLength()));
				//	radar.setRange((int) sa.getView().getWidth());
				//}
			}else {
				sa.setRange(radar.getRange());
				sa.setSensitivity(radar.getSensitivity());
			}
		}
		this.slider_sense.setSelection( (int) radar.getSensitivity() );
		this.slider_range.setSelection( (int)radar.getRange() );
		this.lbl_sense.setText( String.valueOf( radar.getSensitivity()));
		this.lbl_range.setText( String.valueOf( radar.getRange()));
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
