package org.miip.waterway.ui.radar;

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
import org.miip.waterway.model.def.IInhabitedEnvironment;
import org.miip.waterway.model.def.IRadar;
import org.miip.waterway.sa.ISituationalAwareness;
import org.miip.waterway.ui.swt.AveragingRadar;
import org.miip.waterway.ui.swt.HumanAssist;
import org.miip.waterway.ui.swt.Radar;
import org.miip.waterway.ui.swt.pond.PondRadar;

public class RadarGroup<I extends Object> extends Group {
	private static final long serialVersionUID = 1L;

	private IRadar<IInhabitedEnvironment<I>> radar;
	private Combo combo_radar;
	private Slider slider_sense;
	private Label lbl_sense;
	private Slider slider_range;
	private Label lbl_range;
	private Composite composite;

	private ISituationalAwareness<IInhabitedEnvironment<I>> sa;

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
		setLayout(new GridLayout(2, false));

		//int radar_width = 80;

		composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(2, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
		combo_radar = new Combo( composite, SWT.BORDER );
		combo_radar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 2, 1));
		combo_radar.setSize(361, 23);
		combo_radar.setItems( IRadar.RadarSelect.getItems() );
		combo_radar.select( IRadar.RadarSelect.WARP.ordinal() );


		slider_sense = new Slider( composite, SWT.BORDER );
		slider_sense.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider_sense.setSize(254, 17);
		slider_sense.setMinimum(1);
		slider_sense.setMaximum(900);
		slider_sense.setSelection( IRadar.DEFAULT_SENSITIVITY );
		slider_sense.setIncrement(2);
		slider_sense.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					sa.setSensitivity( slider_sense.getSelection());
					lbl_sense.setText( String.valueOf( slider_sense.getSelection()));
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});


		lbl_sense = new Label( composite, SWT.BORDER );
		lbl_sense.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ));

		slider_range = new Slider(composite, SWT.BORDER );
		slider_range.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		slider_range.setSize(254, 17);
		slider_range.setMinimum(1);
		slider_range.setMaximum(3000);
		slider_range.setSelection( IRadar.DEFAULT_RANGE );
		slider_range.setIncrement(20);
		slider_range.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					sa.setRange( slider_range.getSelection());
					lbl_range.setText( String.valueOf( slider_range.getSelection()));
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
		lbl_range = new Label( composite, SWT.BORDER );
		lbl_range.setText("Range");
		lbl_range.setSize(153, 17);


		combo_radar.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(final SelectionEvent e) {
				final Composite parent = radar.getParent();
				for( Control child: parent.getChildren() )
					child.dispose();
				getDisplay().asyncExec( new Runnable(){

					@Override
					public void run() {
						try{
							switch( IRadar.RadarSelect.getRadar( combo_radar.getSelectionIndex())){
							case WARP:
								radar = new Radar<IInhabitedEnvironment<I>>(parent, SWT.BORDER);
								break;
							case AVERAGE:
								AveragingRadar<IInhabitedEnvironment<I>> avr = new AveragingRadar<IInhabitedEnvironment<I>>(parent, SWT.BORDER);
								//avr.setExpand( 1);
								radar = avr;
								break;
							case POND:
								PondRadar<IInhabitedEnvironment<I>> pondr = new PondRadar<IInhabitedEnvironment<I>>(parent, SWT.BORDER);
								//avr.setExpand( 1);
								radar = pondr;
								break;
							default:
								radar = new HumanAssist<IInhabitedEnvironment<I>>( parent, SWT.BORDER );	
								break;
							}
							radar.setInput(sa);
							radar.refresh();
							parent.layout();
						}
						catch( Exception ex ){
							ex.printStackTrace();
						}
					}
				});
				super.widgetSelected(e);
			}
		});

		Composite comp_radar = new Composite( this, SWT.NONE); 
		comp_radar.setLayout(new FillLayout());
		comp_radar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		radar = new Radar<IInhabitedEnvironment<I>>( comp_radar, SWT.BORDER );
	}

	public void setInput( ISituationalAwareness<IInhabitedEnvironment<I>> sa ) {
		this.sa = sa;
		this.lbl_sense.setText( String.valueOf( this.slider_sense.getSelection()));
		this.lbl_range.setText( String.valueOf( this.slider_range.getSelection()));
		if( sa != null ) {
			this.slider_sense.setSelection( sa.getSensitivity() );
			this.slider_range.setMaximum( (int) (sa.getInput().getField().getLength()/2) );
			this.slider_range.setSelection( sa.getRange() );
		}
		this.radar.setInput( sa );
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
