package org.miip.waterway.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;

import java.util.EnumSet;
import java.util.logging.Logger;

import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationListener;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.thread.IExecuteThread;
import org.condast.commons.ui.player.PlayerImages;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.ui.widgets.AbstractButtonBar;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.miip.waterway.model.CentreShip;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Ship;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.ui.radar.RadarGroup;
import org.eclipse.swt.widgets.Button;

public class MiipComposite extends Composite implements IInputWidget<IMIIPEnvironment> {
	private static final long serialVersionUID = 1L;

	//Themes
	public static final String S_MIIP = "miip";
	public static final String S_MIIP_ITEM = "miipitem";

	public static final String S_MIIP_URL = "http://www.maritiemland.nl/news/startbijeenkomst-maritieme-innovatie-impuls-projecten-2017/";
	public static final String S_NMT_URL = "http://www.maritiemland.nl/innovatie/projecten/maritieme-innovatie-impuls-projecten/";
	public static final String S_KC_DHS_URL = "https://www.hogeschoolrotterdam.nl/onderzoek/kenniscentra/duurzame-havenstad/over-het-kenniscentrum/";
	public static final String S_RDM_COE_URL = "http://www.rdmcoe.nl//";
	public static final String S_CONDAST_URL = "http://www.condast.com/";
	public static final String S_JUROD_URL = "https://www.jurod.nl/";

	private MIIPPresentation canvas;
	private Text text_name;
	private Label lblSpeedLabel;
	private Text text_speed;
	private Text text_bearing;
	private Text text_lng;
	private Text text_lat;
	private Label lblHits;
	private RadarGroup radarGroup;
	private IMIIPEnvironment environment;

	private PlayerComposite<IMIIPEnvironment> playerbar;

	private Slider slider_speed;
	private Spinner spinner_ships;
	private Label lblActiveShips;
	private Button btn_manual;

	private int hits;

	private SessionHandler handler;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private ISituationListener<IPhysical> shlistener = new ISituationListener<IPhysical>() {

		private StringBuffer buffer = new StringBuffer();

		@Override
		public void notifySituationChanged(SituationEvent<IPhysical> event) {
			if( event.getAngle() == 0 ){
				buffer = new StringBuffer();
				buffer.append( "vectors: " );
			}else if( event.getAngle() == 511 ){
				logger.info( buffer.toString() );				
			}else{
				buffer.append( "[" + event.getAngle() + ", " + event.getDistance() + "] " );
			}
			try {
				if( getDisplay().isDisposed())
					return;
				getDisplay().asyncExec( new Runnable(){

					@Override
					public void run() {
						try{
							hits++;
							lblHits.setText( String.valueOf(hits));
						}
						catch( Exception ex ){
							ex.printStackTrace();
						}
					}	
				});
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
	};

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public MiipComposite(Composite parent, int style) {
		super(parent, style);
		this.createComposite(parent, style);
		this.handler = new SessionHandler( getDisplay());
	}

	protected void createComposite( Composite parent, int style ){
		setLayout(new GridLayout(2, false));
		this.setData( RWT.CUSTOM_VARIANT, S_MIIP);

		canvas = new MIIPPresentation(this, SWT.BORDER );
		canvas.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,2,1));

		Group group_control = new Group( composite, SWT.NONE );
		group_control.setText("Control");
		GridData gd_control = new GridData( SWT.FILL, SWT.FILL, false, true);
		group_control.setLayout(new GridLayout(3, false));
		group_control.setLayoutData( gd_control );

		Label lblSpeedTextLabel = new Label(group_control, SWT.NONE);
		lblSpeedTextLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		lblSpeedTextLabel.setText("Speed:");

		slider_speed = new Slider( group_control, SWT.BORDER );
		GridData gd_slider = new GridData( SWT.FILL, SWT.FILL, false, false );
		slider_speed.setLayoutData( gd_slider);
		slider_speed.setMinimum(1);
		slider_speed.setMaximum(1000);
		slider_speed.setIncrement(10);
		slider_speed.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					lblSpeedLabel.setText( String.valueOf( slider_speed.getSelection()));
					IExecuteThread thread = (IExecuteThread) environment;
					thread.setTimer( slider_speed.getSelection());
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
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
				try{
					if(( environment != null ) && ( environment.getWaterway() != null ))
						environment.getWaterway().setNrOfShips( spinner_ships.getSelection());
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});


		lblActiveShips = new Label(group_control, SWT.BORDER);
		lblActiveShips.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		btn_manual = new Button( group_control, SWT.CHECK );
		btn_manual.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));
		btn_manual.setText("Manual");
		btn_manual.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					Button check = (Button) e.widget;
					environment.setManual( check.getSelection());
					if( check.getSelection() )
						canvas.setFocus();
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
		playerbar = new PlayerComposite<IMIIPEnvironment>( group_control, SWT.BORDER );
		playerbar.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 3, 1));
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

		Label lbl_hitText = new Label( group_ship, SWT.NONE );
		lbl_hitText.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false ));
		lbl_hitText.setText("Hits: ");
		lblHits = new Label( group_ship, SWT.BORDER );
		lblHits.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ));

		radarGroup = new RadarGroup(composite, SWT.NONE); 
		radarGroup.setText("Radar");
		radarGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		//radar = new DirectRadar( comp_radar, SWT.BORDER );	
		canvas.addKeyListener(new KeyAdapter(){
			private static final long serialVersionUID = 1L;

			public void keyPressed(KeyEvent e)
			{
				if( !btn_manual.getSelection())
					return;
				try{
					CentreShip ship = (CentreShip) environment.getInhabitant();
					CentreShip.Controls control = null; 
					if( ship == null )
						return;
					switch( e.keyCode ){
					case SWT.ARROW_UP:
						control = CentreShip.Controls.UP;
						break;
					case SWT.ARROW_DOWN:
						control = CentreShip.Controls.DOWN;
						break;
					case SWT.ARROW_LEFT:
						control = CentreShip.Controls.LEFT;
						break;
					case SWT.ARROW_RIGHT:
						control = CentreShip.Controls.RIGHT;
						break;
					default: 
						break;
					}
					ship.setControl(control);	
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
	}

	@Override
	public IMIIPEnvironment getInput() {
		return this.environment;
	}

	@Override
	public void setInput( IMIIPEnvironment environment ){
		this.environment = environment;
		if( this.environment != null ) {
			this.radarGroup.setInput( environment.getSituationalAwareness(), true);
		}
		this.canvas.setInput(environment);
	}

	protected void updateView(){
		Ship ship = (Ship) environment.getInhabitant();
		IExecuteThread thread = (IExecuteThread) environment;
		this.slider_speed.setSelection( thread.getTimer());
		this.spinner_ships.setSelection( environment.getWaterway().getNrOfShips());					
		canvas.redraw();
		this.text_name.setText( ship.getIdentifier() );
		this.text_speed.setText( String.valueOf( ship.getSpeed() ));
		this.text_bearing.setText( String.valueOf( ship.getHeading() ));
		this.text_lng.setText( String.valueOf( ship.getLocation().getLongitude() ));
		this.text_lat.setText( String.valueOf( ship.getLocation().getLatitude() ));
		this.lblActiveShips.setText( String.valueOf( environment.getWaterway().getShips().length));
	}

	public void dispose(){
		if( this.environment != null ){
			if( this.environment.getSituationalAwareness() != null )
				this.environment.getSituationalAwareness().removeListener(shlistener);
			this.environment.removeListener(handler);
			IExecuteThread thread = (IExecuteThread) environment;
			thread.stop();
		}
		handler.dispose();
		super.dispose();
	}

	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

	private class PlayerComposite<I extends Object> extends AbstractButtonBar<PlayerImages.Images, I> {
		private static final long serialVersionUID = 1L;

		public PlayerComposite(Composite parent, int style) {
			super(parent, style);
		}

		@Override
		protected EnumSet<PlayerImages.Images> setupButtonBar() {
			return EnumSet.of(PlayerImages.Images.START, 
					PlayerImages.Images.STOP, 
					PlayerImages.Images.NEXT,
					PlayerImages.Images.RESET);
		}

		@Override
		protected Control createButton(PlayerImages.Images type) {
			Button button = new Button( this, SWT.FLAT );
			IExecuteThread thread = (IExecuteThread) environment;
			switch( type ){
			case STOP:
				button.setEnabled(( environment != null ) && thread.isRunning());
				break;
			default:
				break;
			}
			button.setData(type);
			button.addSelectionListener( new SelectionAdapter() {
				private static final long serialVersionUID = 1L;

				@Override
				public void widgetSelected(SelectionEvent e) {
					try{
						Button button = (Button) e.getSource();
						PlayerImages.Images image = (PlayerImages.Images) button.getData();
						Button clear = (Button) getButton( PlayerImages.Images.RESET);
						IExecuteThread thread = (IExecuteThread) environment;
						switch( image ){
						case START:
							environment.addListener(handler);
							thread.start();
							environment.getSituationalAwareness().addListener(shlistener);
							radarGroup.setInput( environment.getSituationalAwareness(), true);
							getButton( PlayerImages.Images.STOP).setEnabled(true);
							button.setEnabled(false);
							clear.setEnabled( false );//!environment.isRunning() || environment.isPaused());
							getDisplay().asyncExec( new Runnable(){

								@Override
								public void run() {
									layout();
								}		
							});
							break;
						case STOP:
							thread.stop();
							environment.removeListener(handler);
							getButton( PlayerImages.Images.START).setEnabled(true);
							button.setEnabled(false);
							clear = (Button) getButton( PlayerImages.Images.RESET);
							clear.setEnabled( true );//!environment.isRunning() || environment.isPaused());
							break;
						case NEXT:
							thread.step();
							clear = (Button) getButton( PlayerImages.Images.RESET);
							clear.setEnabled( true );//!environment.isRunning() || environment.isPaused());
							break;
						case RESET:
							hits = 0;
							environment.clear();
						default:
							break;
						}

					}
					catch( Exception ex ){
						ex.printStackTrace();
					}
				}		
			});
			button.setImage( PlayerImages.getInstance().getImage(type));
			return button;
		}
	}	
	
	private class SessionHandler extends AbstractSessionHandler<EnvironmentEvent<IVessel>> 
	implements IEnvironmentListener<IVessel>{

		protected SessionHandler(Display display) {
			super(display);
		}

		@Override
		protected void onHandleSession(SessionEvent<EnvironmentEvent<IVessel>> sevent) {
			try{
				updateView();
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}

		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent<IVessel> event) {
			try{
				switch( event.getType() ){
				case INITIALSED:
					break;
				default:
					addData(event);
					break;
				}
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}	
	}
}
