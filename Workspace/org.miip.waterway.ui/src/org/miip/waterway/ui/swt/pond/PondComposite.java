package org.miip.waterway.ui.swt.pond;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;

import java.util.EnumSet;

import org.condast.commons.Utils;
import org.condast.commons.autonomy.ca.ICollisionAvoidanceStrategy;
import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.thread.IExecuteThread;
import org.condast.commons.ui.player.PlayerImages;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.ui.widgets.AbstractButtonBar;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.ui.radar.RadarGroup;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;

public class PondComposite extends Composite implements IInputWidget<IMIIPEnvironment> {
	private static final long serialVersionUID = 1L;

	//Themes
	public static final String S_MIIP = "miip";
	public static final String S_MIIP_ITEM = "miipitem";

	public static enum Tools{
		SETTINGS
	}

	private SessionHandler handler;

	private PondCanvas canvas;
	private Text text_name;
	private Label lblSpeedLabel;
	private Text text_speed;
	private Text text_bearing;
	private Text text_lng;
	private Text text_lat;
	private Label lblHits;
	
	private PlayerComposite<IMIIPEnvironment> playerbar;

	private Slider slider_speed;
	private Label lblActiveShips;

	private RadarGroup radarGroup;
	private IMIIPEnvironment environment;

	private int hits;
	private boolean disposed;
	private Label lblStrategy;
	private Combo strategyCombo;

	/**
	 * Create the composite.
	 * @param parent
	 * @param style
	 */
	public PondComposite(Composite parent, int style) {
		super(parent, style);
		this.createComposite(parent, style | SWT.NO_SCROLL);
		this.disposed = false;
		this.handler = new SessionHandler(getDisplay());
	}

	protected void createComposite( Composite parent, int style ){
		setLayout(new GridLayout(2, false));
		this.setData( RWT.CUSTOM_VARIANT, S_MIIP);

		canvas = new PondCanvas(this, SWT.BORDER );
		canvas.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		new Label(this, SWT.NONE);

		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(4, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false,2,1));

		Group group_control = new Group( composite, SWT.NONE );
		group_control.setText("Control");
		group_control.setLayout(new GridLayout(3, false));
		group_control.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, true) );
		
		lblStrategy = new Label(group_control, SWT.NONE);
		lblStrategy.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblStrategy.setText("Strategy:");
		
		strategyCombo = new Combo(group_control, SWT.NONE);
		strategyCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		strategyCombo.setItems(ICollisionAvoidanceStrategy.DefaultStrategies.getItems());
		strategyCombo.select(ICollisionAvoidanceStrategy.DefaultStrategies.SIMPLE_COLLISION_AVOIDANCE.ordinal());
		strategyCombo.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				try{
					IVessel vessel = environment.getInhabitant();
					vessel.clearStrategies();
					vessel.addStrategy( StringStyler.styleToEnum(strategyCombo.getText()));
					super.widgetSelected(e);
				}
				catch( Exception ex ){
					ex.printStackTrace();
				}
			}
		});
		new Label(group_control, SWT.NONE);

		Label lblSpeedTextLabel = new Label(group_control, SWT.NONE);
		lblSpeedTextLabel.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		lblSpeedTextLabel.setText("Speed:");

		slider_speed = new Slider( group_control, SWT.BORDER );
		//gd_slider.widthHint = 80;
		slider_speed.setLayoutData( new GridData( SWT.FILL, SWT.FILL, false, false ));
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


		lblActiveShips = new Label(group_control, SWT.BORDER);
		lblActiveShips.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false));

		playerbar = new PlayerComposite<IMIIPEnvironment>( group_control, SWT.BORDER );
		new Label(group_control, SWT.NONE);
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
		new Label(group_ship, SWT.NONE);

		radarGroup = new RadarGroup(composite, SWT.NONE); 
		radarGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		new Label(composite, SWT.NONE);
	}

	@Override
	public IMIIPEnvironment getInput() {
		return this.environment;
	}

	@Override
	public void setInput( IMIIPEnvironment environment){
		if(( this.environment != null ) && ( this.environment.equals( environment )))
			return;
		this.environment = environment;
		if( this.environment == null )
			return;
		this.environment.setEnabled(true);
		this.canvas.setInput( this.environment);
		IVessel reference = (IVessel) this.environment.getInhabitant();
		ICollisionAvoidanceStrategy.DefaultStrategies strategy = Utils.assertNull(reference.getSelectedStrategies())?
				ICollisionAvoidanceStrategy.DefaultStrategies.SIMPLE_COLLISION_AVOIDANCE: 
				ICollisionAvoidanceStrategy.DefaultStrategies.valueOf( reference.getSelectedStrategies()[0]);
		this.strategyCombo.select(strategy.ordinal());
		this.radarGroup.setInput( reference.getSituationalAwareness(), false );
		if( this.environment != null )
			this.environment.addListener(handler);
		this.playerbar.getButton(PlayerImages.Images.START).setEnabled( this.environment != null );
	}

	protected void updateView(){
		IVessel vessel = (IVessel) environment.getInhabitant();
		IExecuteThread thread = (IExecuteThread) environment;
		this.slider_speed.setSelection( thread.getTimer());
		this.text_name.setText( vessel.getName() );
		this.text_speed.setText( String.valueOf( vessel.getSpeed() ));
		this.text_bearing.setText( String.valueOf( vessel.getHeading() ));
		this.text_lng.setText( String.valueOf( vessel.getLocation().getLongitude() ));
		this.text_lat.setText( String.valueOf( vessel.getLocation().getLatitude() ));

		this.lblHits.setText(String.valueOf(hits));

		this.canvas.redraw();
		layout(false);
	}
	
	@Override
	public void setVisible(boolean visible) {
		if( this.environment != null )
			this.environment.setEnabled(visible);
		super.setVisible(visible);
	}

	public void dispose(){
		this.disposed = true;
		if( this.environment != null ){
			this.environment.removeListener(handler);
			IExecuteThread thread = (IExecuteThread) environment;
			thread.stop();
		}
		this.handler.dispose();
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
		
		public void stop() {
			IExecuteThread thread = (IExecuteThread) environment;
			thread.stop();
			environment.removeListener(handler);
			getButton( PlayerImages.Images.START).setEnabled(true);
			Button clear = (Button) getButton( PlayerImages.Images.RESET);
			clear.setEnabled( true );
		}

		@Override
		protected Control createButton(PlayerImages.Images type) {
			Button button = new Button( this, SWT.FLAT );
			IExecuteThread thread = (IExecuteThread) environment;
			switch( type ){
			case START:
				button.setEnabled( environment != null );
				break;
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
						Button clear;
						IExecuteThread thread = (IExecuteThread) environment;
						switch( image ){
						case START:
							environment.addListener( handler);
							
							thread.start();
							getButton( PlayerImages.Images.STOP).setEnabled(true);
							button.setEnabled(false);
							clear = (Button) getButton( PlayerImages.Images.RESET);
							clear.setEnabled( false );//!environment.isRunning() || environment.isPaused());
							break;
						case STOP:
							stop();
							break;
						case NEXT:
							thread.step();
							clear = (Button) getButton( PlayerImages.Images.RESET);
							clear.setEnabled( true );//!environment.isRunning() || environment.isPaused());
							break;
						case RESET:
							hits = 0;
							environment.clear();
							getButton( PlayerImages.Images.RESET).setEnabled(false);
						default:
							break;
						}
						IVessel vessel = environment.getInhabitant();
						vessel.clearStrategies();
						vessel.addStrategy( StringStyler.styleToEnum(strategyCombo.getText()));
					}
					catch( Exception ex ){
						ex.printStackTrace();
					}
				}		
			});
			button.setImage( PlayerImages.getImage(type));
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
		public void notifyEnvironmentChanged( final EnvironmentEvent<IVessel> event) {
			if( disposed || ( getDisplay()== null ) || getDisplay().isDisposed() )
				return;

			getDisplay().asyncExec( new Runnable() {

				@Override
				public void run() {
					try{
						IVessel vessel = event.getData();
						ISituationalAwareness<IPhysical, IVessel> sa = ( vessel == null )?null: vessel.getSituationalAwareness();
						switch( event.getType() ){
						case INITIALSED:
							if( sa != null )
								radarGroup.setInput( sa, false);
							break;
						case PROCEED:
							if( sa == null )
								break;
							vessel.clearStrategies();
							vessel.addStrategy( StringStyler.styleToEnum(strategyCombo.getText()));
							radarGroup.setInput( sa, false);
							break;
						case COLLISION_DETECT:
						case OUT_OF_BOUNDS:
							playerbar.stop();
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
			});
		}	
	}
}
