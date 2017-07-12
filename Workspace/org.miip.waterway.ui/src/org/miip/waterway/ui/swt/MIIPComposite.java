package org.miip.waterway.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.layout.GridLayout;

import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Slider;
import org.eclipse.swt.widgets.Spinner;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Text;
import org.miip.waterway.internal.model.Ship;
import org.miip.waterway.ui.eco.MIIPEnvironment;
import org.eclipse.swt.widgets.Button;

public class MIIPComposite extends Composite {
	private static final long serialVersionUID = 1L;

	private MIIPCanvas canvas;
	private Text text_name;
	private Text text_speed;
	private Text text_bearing;
	private Text text_lng;
	private Text text_lat;
	private Label lblSpeedLabel;
	
	private MIIPEnvironment environment; 
	
	private IEnvironmentListener listener = new IEnvironmentListener() {
		
		@Override
		public void notifyEnvironmentChanged(final EnvironmentEvent event) {
			getDisplay().asyncExec( new Runnable(){

				@Override
				public void run() {
					setInput();
					spinner_ships.setSelection( environment.getWaterway().getNrOfShips());					canvas.redraw();
					switch( event.getType() ){
					case INITIALSED:
						break;
					default:
						break;
					}
				}
				
			});
		}
	};
	private Button btnStartButton;
	private Slider slider_speed;
	private Spinner spinner_ships;
	
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
	}
	
	protected void createComposite( Composite parent, int style ){
		setLayout(new GridLayout(1, false));
		canvas = new MIIPCanvas(this, SWT.BORDER );
		canvas.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true ));
		
		Composite composite = new Composite(this, SWT.NONE);
		composite.setLayout(new GridLayout(3, false));
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 1, 1));

		Group group_control = new Group( composite, SWT.NONE );
		group_control.setText("Control");
		group_control.setLayout(new GridLayout(3, false));
		group_control.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ));

		Label lblSpeedTextLabel = new Label(group_control, SWT.NONE);
		lblSpeedTextLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblSpeedTextLabel.setText("Speed:");

		slider_speed = new Slider( group_control, SWT.BORDER );
		slider_speed.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ));
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
		lblSpeedLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblSpeedLabel.setText( String.valueOf( slider_speed.getSelection()));

		Label lblShipsLabel = new Label(group_control, SWT.NONE);
		lblShipsLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false));
		lblShipsLabel.setText("Amount:");

		spinner_ships = new Spinner( group_control, SWT.BORDER );
		spinner_ships.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false, 2, 1 ));
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
		
		btnStartButton = new Button( group_control, SWT.NONE);
		btnStartButton.setText("Start");
		btnStartButton.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				if( !environment.isRunning() )
					environment.start();
				else
					environment.pause();
				btnStartButton.setText( !environment.isPaused()? "Stop": "Start");
				super.widgetSelected(e);
			}
			
		});

		Group group_ship = new Group( composite, SWT.NONE );
		group_ship.setText("Details Ship");
		group_ship.setLayout(new GridLayout(3, false));
		group_ship.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ));
		
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
		lblPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPosition.setText("Position:");
		
		text_lng = new Text(group_ship, SWT.BORDER);
		text_lng.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		text_lat = new Text(group_ship, SWT.BORDER);
		text_lat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));		
	}

	protected void setInput(){
		Ship ship = environment.getShip();
		this.text_name.setText( ship.getId() );
		this.text_speed.setText( String.valueOf( ship.getSpeed() ));
		this.text_bearing.setText( String.valueOf( ship.getBearing() ));
		this.text_lng.setText( String.valueOf( ship.getLnglat().getLongitude() ));
		this.text_lat.setText( String.valueOf( ship.getLnglat().getLatitude() ));
	}
	
	public void dispose(){
		this.environment.removeListener(listener);
		this.environment.stop();
		super.dispose();
	}
	
	@Override
	protected void checkSubclass() {
		// Disable the check that prevents subclassing of SWT components
	}

}
