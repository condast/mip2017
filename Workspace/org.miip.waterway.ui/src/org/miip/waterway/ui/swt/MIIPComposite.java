package org.miip.waterway.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;
import org.condast.commons.ui.session.ISessionListener;
import org.condast.commons.ui.session.RefreshSession;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.miip.waterway.model.Ship;
import org.miip.waterway.ui.eco.MIIPEnvironment;
import org.miip.waterway.ui.images.MIIPImages;
import org.miip.waterway.ui.images.MIIPImages.Images;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

public class MIIPComposite extends Composite {
	private static final long serialVersionUID = 1L;

	public static final String S_MIIP_URL = "http://www.maritiemland.nl/news/startbijeenkomst-maritieme-innovatie-impuls-projecten-2017/";
	public static final String S_NMT_URL = "http://www.maritiemland.nl/innovatie/projecten/maritieme-innovatie-impuls-projecten/";

	private MIIPPresentation canvas;
	private Text text_name;
	private Text text_speed;
	private Text text_bearing;
	private Text text_lng;
	private Text text_lat;
	private Label lblSpeedLabel;

	private Button miipButton;
	private Button nmtButton;

	private MIIPEnvironment environment; 
	
	private IEnvironmentListener listener = new IEnvironmentListener() {
		
		@Override
		public void notifyEnvironmentChanged(final EnvironmentEvent event) {
			if( getDisplay().isDisposed() )
				return;
			getDisplay().asyncExec( new Runnable(){

				@Override
				public void run() {
					setInput();
					spinner_ships.setSelection( environment.getWaterway().getNrOfShips());					canvas.redraw();
					switch( event.getType() ){
					case INITIALSED:
						break;
					default:
						session.refresh();
						break;
					}
				}
			});
		}
	};
	private Button btnStartButton;
	private Button btnClearButton;
	private Slider slider_speed;
	private Spinner spinner_ships;
	private Label lblActiveShips;
	
	private IRadarUI radar;
	private Combo combo_radar;
	private Slider slider_sense;
	private Slider slider_range;
	
	private RefreshSession<MIIPEnvironment> session;
	private ISessionListener<MIIPEnvironment> slistener = new ISessionListener<MIIPEnvironment>(){

		@Override
		public void notifySessionChanged(SessionEvent<MIIPEnvironment> event) {
			layout();
		}	
	};
	
	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MIIPComposite(Composite parent, Integer style) {
		super(parent, style);
		this.environment = MIIPEnvironment.getInstance();
		this.environment.addListener(listener);
		this.createComposite(parent, style);
		this.session = new RefreshSession<MIIPEnvironment>();
		this.session.addSessionListener(slistener);
		this.session.start();
	}

	protected void createComposite( Composite parent, int style ){
		setLayout(new GridLayout(2, false));
		canvas = new MIIPPresentation(this, SWT.BORDER );
		canvas.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		createImageBar(this, style);
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		Group group_control = new Group( composite, SWT.NONE );
		group_control.setText("Control");
		GridData gd_control = new GridData( SWT.FILL, SWT.FILL, false, true);
		gd_control.widthHint = 300;
		group_control.setLayout(new GridLayout(3, false));
		group_control.setLayoutData( gd_control );

		Label lblSpeedTextLabel = new Label(group_control, SWT.NONE);
		lblSpeedTextLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSpeedTextLabel.setText("Speed:");

		slider_speed = new Slider( group_control, SWT.BORDER );
		GridData gd_slider = new GridData( SWT.FILL, SWT.FILL, false, false );
		gd_slider.widthHint = 80;
		slider_speed.setLayoutData( gd_slider);
		slider_speed.setMinimum(1);
		slider_speed.setMaximum(1000);
		slider_speed.setSelection( environment.getTimer());
		slider_speed.setIncrement(10);
		slider_speed.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				lblSpeedLabel.setText( String.valueOf( slider_speed.getSelection()));
				environment.setTimer( slider_speed.getSelection());
				super.widgetSelected(e);
			}
		});

		lblSpeedLabel = new Label(group_control, SWT.BORDER);
		lblSpeedLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		lblSpeedLabel.setText( String.valueOf( slider_speed.getSelection() ));

		Label lblShipsLabel = new Label(group_control, SWT.NONE);
		lblShipsLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		lblShipsLabel.setText("Amount:");

		spinner_ships = new Spinner( group_control, SWT.BORDER );
		spinner_ships.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false ));
		spinner_ships.setMaximum(10);
		spinner_ships.setMaximum( 50 );
		spinner_ships.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				environment.getWaterway().setNrOfShips( spinner_ships.getSelection());
				super.widgetSelected(e);
			}
		});

		lblActiveShips = new Label(group_control, SWT.BORDER);
		lblActiveShips.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		new Label( group_control, SWT.NONE );

		btnStartButton = new Button( group_control, SWT.NONE);
		btnStartButton.setText("Start");
		btnStartButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false));
		btnStartButton.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				if( !environment.isRunning() )
					environment.start();
				else
					environment.pause();
				btnClearButton.setEnabled( false );//!environment.isRunning() || environment.isPaused());
				Button btn = (Button) e.widget;
				btn.setText( !environment.isPaused()? "Stop": "Start");
				getDisplay().asyncExec( new Runnable(){

					@Override
					public void run() {
						layout();
					}		
				});
				super.widgetSelected(e);
			}		
		});

		btnClearButton = new Button( group_control, SWT.NONE);
		btnClearButton.setText("Clear");
		btnClearButton.setLayoutData(new GridData(SWT.LEFT, SWT.FILL, false, false ));
		btnClearButton.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				environment.clear();
				super.widgetSelected(e);
			}			
		});

		Group group_ship = new Group( composite, SWT.NONE );
		GridData gd_ship= new GridData( SWT.FILL, SWT.FILL, true, true );
		gd_ship.widthHint = 120;
		group_ship.setText("Details Ship");
		group_ship.setLayout(new GridLayout(3, false));
		group_ship.setLayoutData(gd_ship);
		
		Label lblNameLabel = new Label(group_ship, SWT.NONE);
		lblNameLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNameLabel.setText("Name:");
		
		text_name = new Text(group_ship, SWT.BORDER);
		text_name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblNewLabel_1 = new Label(group_ship, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Speed:");
		
		text_speed = new Text(group_ship, SWT.BORDER);
		text_speed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		text_bearing = new Text(group_ship, SWT.BORDER);
		text_bearing.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));

		Label lblPosition = new Label(group_ship, SWT.NONE);
		lblPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblPosition.setText("Position:");
		
		text_lng = new Text(group_ship, SWT.BORDER);
		text_lng.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));	
		
		text_lat = new Text(group_ship, SWT.BORDER);
		text_lat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));		
		
		Group grp_radar = new Group(composite, SWT.NONE); 
		grp_radar.setText("Radar");
		grp_radar.setLayout(new GridLayout(1, false));
		grp_radar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		
		int radar_width = 80;
		GridData gd_radar = new GridData( SWT.FILL, SWT.FILL, true, false);
		gd_radar.widthHint = radar_width;
		combo_radar = new Combo( grp_radar, SWT.BORDER );
		combo_radar.setLayoutData( gd_radar);		
		combo_radar.setItems( IRadarUI.RadarSelect.getItems() );
		combo_radar.select( IRadarUI.RadarSelect.WARP.ordinal() );
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
						switch( IRadarUI.RadarSelect.getRadar( combo_radar.getSelectionIndex())){
						case WARP:
							radar = new Radar(parent, SWT.BORDER);
							break;
						case AVERAGE:
							AveragingRadar avr = new AveragingRadar(parent, SWT.BORDER);
							avr.setExpand( 1);
							radar = avr;
							break;
						default:
							radar = new HumanAssist( parent, SWT.BORDER );	
							break;
						}
						radar.setInput(environment.getSituationalAwareness());
						radar.refresh();
						parent.layout();
					}
				});
				super.widgetSelected(e);
			}
		});
		
		slider_sense = new Slider( grp_radar, SWT.BORDER );
		gd_radar = new GridData( SWT.FILL, SWT.FILL, true, false);
		gd_radar.widthHint = radar_width;
		slider_sense.setLayoutData( gd_radar);
		slider_sense.setMinimum(1);
		slider_sense.setMaximum(900);
		slider_sense.setSelection( IRadarUI.DEFAULT_SENSITIVITY );
		slider_sense.setIncrement(2);
		slider_sense.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				radar.setSensitivity( slider_sense.getSelection());
				super.widgetSelected(e);
			}
		});

		slider_range = new Slider( grp_radar, SWT.BORDER );
		gd_radar = new GridData( SWT.FILL, SWT.FILL, true, false);
		gd_radar.widthHint = radar_width;
		slider_range.setLayoutData( gd_radar);
		slider_range.setMinimum(1);
		slider_range.setMaximum(3000);
		slider_range.setSelection( IRadarUI.DEFAULT_RANGE );
		slider_range.setIncrement(20);
		slider_range.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				radar.setRange( slider_range.getSelection());
				super.widgetSelected(e);
			}
		});
		
		Composite comp_radar = new Composite(this, SWT.NONE); 
		comp_radar.setLayout(new FillLayout());
		comp_radar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true));
		radar = new Radar( comp_radar, SWT.BORDER );		
	}

	private void createImageBar(Composite parent, int style) {
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false ));
		comp.setLayout( new GridLayout( 1, false));

		miipButton = new Button(comp, SWT.FLAT);
		miipButton.setImage( MIIPImages.getImage(Images.MIIP));
		miipButton.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				UrlLauncher launcher = RWT.getClient().getService( UrlLauncher.class );
				launcher.openURL( S_NMT_URL );
				super.widgetSelected(e);
			}
		});
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		miipButton.setLayoutData(layoutData);	

		nmtButton = new Button(comp, SWT.FLAT);
		nmtButton.setImage( MIIPImages.getImage(Images.NMT));
		nmtButton.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				Display.getCurrent().asyncExec( new Runnable(){

					@Override
					public void run() {
						UrlLauncher launcher = RWT.getClient().getService( UrlLauncher.class );
						launcher.openURL( S_NMT_URL );
					}					
				});
			}
		});
		GridData nmtData = new GridData(SWT.FILL, SWT.FILL, true, true);
		nmtButton.setLayoutData(nmtData);
	}

	protected void setInput(){
		Ship ship = environment.getShip();
		this.text_name.setText( ship.getId() );
		this.text_speed.setText( String.valueOf( ship.getSpeed() ));
		this.text_bearing.setText( String.valueOf( ship.getBearing() ));
		this.text_lng.setText( String.valueOf( ship.getLnglat().getLongitude() ));
		this.text_lat.setText( String.valueOf( ship.getLnglat().getLatitude() ));
		this.lblActiveShips.setText( String.valueOf( environment.getWaterway().getShips().length));
		this.radar.setSensitivity(this.slider_sense.getSelection());
		this.radar.setInput( environment.getSituationalAwareness() );
	}
	
	public void dispose(){
		this.environment.removeListener(listener);
		this.environment.stop();
		this.session.stop();
		this.session.removeSessionListener(slistener);
		this.session.dispose();
		super.dispose();
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}
}
