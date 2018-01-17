package org.miip.waterway.ui.swt.pond;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.EnumSet;
import java.util.logging.Logger;

import org.condast.commons.ui.player.PlayerImages;
import org.condast.commons.ui.session.ISessionListener;
import org.condast.commons.ui.session.RefreshSession;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.ui.widgets.AbstractButtonBar;
import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.rap.rwt.client.service.UrlLauncher;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.miip.pond.core.PondSituationalAwareness;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IReferenceEnvironment;
import org.miip.waterway.model.eco.MIIPEnvironment;
import org.miip.waterway.sa.IShipMovedListener;
import org.miip.waterway.sa.ISituationalAwareness;
import org.miip.waterway.sa.ShipEvent;
import org.miip.waterway.ui.dialog.SettingsDialog;
import org.miip.waterway.ui.factory.ICompositeFactory;
import org.miip.waterway.ui.images.MIIPImages;
import org.miip.waterway.ui.images.MIIPImages.Images;
import org.miip.waterway.ui.radar.RadarGroup;
import org.eclipse.swt.widgets.Button;

public class PondComposite extends Composite implements IInputWidget<IReferenceEnvironment<IVessel>> {
	private static final long serialVersionUID = 1L;

	public static enum Tools{
		SETTINGS
	}

	public static final String S_MIIP_URL = "http://www.maritiemland.nl/news/startbijeenkomst-maritieme-innovatie-impuls-projecten-2017/";
	public static final String S_NMT_URL = "http://www.maritiemland.nl/innovatie/projecten/maritieme-innovatie-impuls-projecten/";
	public static final String S_KC_DHS_URL = "https://www.hogeschoolrotterdam.nl/onderzoek/kenniscentra/duurzame-havenstad/over-het-kenniscentrum/";
	public static final String S_RDM_COE_URL = "http://www.rdmcoe.nl//";
	public static final String S_CONDAST_URL = "http://www.condast.com/";

	private IEnvironmentListener listener = new IEnvironmentListener() {

		@Override
		public void notifyEnvironmentChanged(final EnvironmentEvent event) {
			try{
				switch( event.getType() ){
				case INITIALSED:
					break;
				default:
					session.addData(event);
					break;
				}
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}
	};

	private RefreshSession<EnvironmentEvent> session;
	private ISessionListener<EnvironmentEvent> slistener = new ISessionListener<EnvironmentEvent>(){

		@Override
		public void notifySessionChanged(SessionEvent<EnvironmentEvent> event){
			try{
				updateView();
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}	
	};

	private PondPresentation canvas;
	private Text text_name;
	private Label lblSpeedLabel;
	private Text text_speed;
	private Text text_bearing;
	private Text text_lng;
	private Text text_lat;
	private Label lblHits;

	private Collection<ICompositeFactory> factories;
	private Composite frontend;
	
	private PlayerComposite<MIIPEnvironment> playerbar;

	private Slider slider_speed;
	private Label lblActiveShips;

	private RadarGroup radarGroup;
	private ISituationalAwareness<IReferenceEnvironment<IVessel>,IVessel> sa; 
	private IReferenceEnvironment<IVessel> environment;

	private int hits;

	private Logger logger = Logger.getLogger(this.getClass().getName());

	private IShipMovedListener<IVessel> shlistener = new IShipMovedListener<IVessel>() {

		private StringBuffer buffer = new StringBuffer();

		@Override
		public void notifyShipMoved( ShipEvent<IVessel> event ) {
			if( event.getAngle() == 0 ){
				buffer = new StringBuffer();
				buffer.append( "vectors: " );
			}else if( event.getAngle() == 511 ){
				logger.info( buffer.toString() );				
			}else{
				buffer.append( "[" + event.getAngle() + ", " + event.getDistance() + "] " );
			}

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
	};

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PondComposite(Composite parent, Integer style) {
		super(parent, style);
		this.createComposite(parent, style);
		this.frontend = this;
		this.sa = new PondSituationalAwareness(); 

		this.factories = new ArrayList<ICompositeFactory>();
		this.session = new RefreshSession<>(1000);
		this.session.addSessionListener(slistener);
		this.session.init(getDisplay());
		this.session.start();
	}

	protected void createComposite( Composite parent, int style ){
		setLayout(new GridLayout(2, false));

		createImageBar(this, style);
		canvas = new PondPresentation(this, SWT.BORDER );
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
		//gd_slider.widthHint = 80;
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
					environment.setTimer( slider_speed.getSelection());
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


		lblActiveShips = new Label(group_control, SWT.BORDER);
		lblActiveShips.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		playerbar = new PlayerComposite<MIIPEnvironment>( group_control, SWT.BORDER );
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
		radarGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
	}

	private void createImageBar(Composite parent, int style) {
		Composite comp = new Composite( parent, SWT.NONE );
		comp.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false ));
		comp.setLayout( new GridLayout( 1, false));
		Button button = new Button( comp, SWT.NONE );
		button.setImage( MIIPImages.getImage( Images.SETTINGS ));
		button.setLayoutData( new GridData(SWT.FILL, SWT.TOP, true, true));
		button.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				SettingsDialog dialog = new SettingsDialog( frontend, factories.toArray( new ICompositeFactory[ factories.size()]) );
				if( Dialog.OK == dialog.open()){
					
				}
				super.widgetSelected(e);
			}			
		});
		createImageButton( comp, Images.MIIP, S_MIIP_URL );
		createImageButton( comp, Images.NMT, S_NMT_URL );
		createImageButton( comp, Images.RDM_COE, S_RDM_COE_URL );
		createImageButton( comp, Images.KC_DHS, S_KC_DHS_URL );
		createImageButton( comp, Images.CONDAST, S_CONDAST_URL );
	}

	private Button createImageButton( Composite parent, Images image, final String url ){
		Button button = new Button(parent, SWT.FLAT);
		button.setImage( MIIPImages.getImage( image ));
		button.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				Display.getCurrent().asyncExec( new Runnable(){

					@Override
					public void run() {
						try{
							UrlLauncher launcher = RWT.getClient().getService( UrlLauncher.class );
							launcher.openURL( url );
						}
						catch( Exception ex ){
							ex.printStackTrace();
						}
					}					
				});
			}
		});
		GridData nmtData = new GridData(SWT.FILL, SWT.TOP, true, true);
		button.setLayoutData(nmtData);
		return button;
	}

	public void setInput( Collection<ICompositeFactory> factories ){
		this.factories = factories;
	}

	@Override
	public IReferenceEnvironment<IVessel> getInput() {
		return this.environment;
	}

	@Override
	public void setInput( IReferenceEnvironment<IVessel> environment){
		if(( this.environment != null ) && ( this.environment.equals( environment )))
			return;
		this.environment = environment;
		this.sa.setInput(this.environment);
		this.canvas.setInput( this.environment);
		if( this.environment != null )
			this.environment.addListener(listener);
		this.playerbar.getButton(PlayerImages.Images.START).setEnabled( this.environment != null );
	}

	protected void updateView(){
		IVessel ship = environment.getInhabitant();
		this.slider_speed.setSelection( environment.getTimer());
		this.text_name.setText( ship.getName() );
		this.text_speed.setText( String.valueOf( ship.getSpeed() ));
		this.text_bearing.setText( String.valueOf( ship.getBearing() ));
		this.text_lng.setText( String.valueOf( ship.getLocation().getLongitude() ));
		this.text_lat.setText( String.valueOf( ship.getLocation().getLatitude() ));

		this.lblHits.setText(String.valueOf(hits));

		this.radarGroup.setInput( sa );
		this.canvas.redraw();
		layout(false);
	}

	public void dispose(){
		if( this.environment != null ){
			//this.environment.getSituationalAwareness().removelistener(shlistener);
			this.environment.removeListener(listener);
			this.environment.stop();
		}
		this.session.stop();
		this.session.removeSessionListener(slistener);
		this.session.dispose();
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
		public Control getButton(Enum<org.condast.commons.ui.player.PlayerImages.Images> type) {
				return super.getButton(type);
		}

		@Override
		protected Control createButton(PlayerImages.Images type) {
			Button button = new Button( this, SWT.FLAT );
			switch( type ){
			case START:
				button.setEnabled( environment != null );
				break;
			case STOP:
				button.setEnabled(( environment != null ) && environment.isRunning());
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
						switch( image ){
						case START:
							environment.addListener(listener);
							environment.start();
							//environment.getSituationalAwareness().addlistener(shlistener);
							//setModels( environment.getModels());
							//setInput(ce.getBehaviour());
							getButton( PlayerImages.Images.STOP).setEnabled(true);
							button.setEnabled(false);
							Button clear = (Button) getButton( PlayerImages.Images.RESET);
							clear.setEnabled( false );//!environment.isRunning() || environment.isPaused());
							//Button btn = (Button) e.widget;
							//btn.setText( !environment.isPaused()? "Stop": "Start");
							getDisplay().asyncExec( new Runnable(){

								@Override
								public void run() {
									layout();
								}		
							});
							break;
						case STOP:
							environment.stop();
							environment.removeListener(listener);
							getButton( PlayerImages.Images.START).setEnabled(true);
							button.setEnabled(false);
							clear = (Button) getButton( PlayerImages.Images.RESET);
							clear.setEnabled( true );//!environment.isRunning() || environment.isPaused());
							break;
						case NEXT:
							environment.step();
							break;
						case RESET:
							hits = 0;
							environment.clear();
							getButton( PlayerImages.Images.RESET).setEnabled(false);
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
}
