package org.miip.waterway.ui.swt;

import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.GridLayout;
import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Label;
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
	private Text text_lng;
	private Text text_lat;
	
	private MIIPEnvironment environment; 
	
	private IEnvironmentListener listener = new IEnvironmentListener() {
		
		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent event) {
			getDisplay().asyncExec( new Runnable(){

				@Override
				public void run() {
					switch( event.getType() ){
					case INITIALSED:
						initComposite();
						break;
					default:
						initComposite();
						break;
					}
				}
				
			});
		}
	};
	private Button btnNewButton;
	
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
		
		Label lblNewLabel = new Label(composite, SWT.NONE);
		lblNewLabel.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel.setText("Name:");
		
		text_name = new Text(composite, SWT.BORDER);
		text_name.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblNewLabel_1 = new Label(composite, SWT.NONE);
		lblNewLabel_1.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblNewLabel_1.setText("Speed:");
		
		text_speed = new Text(composite, SWT.BORDER);
		text_speed.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		
		Label lblPosition = new Label(composite, SWT.NONE);
		lblPosition.setLayoutData(new GridData(SWT.RIGHT, SWT.CENTER, false, false, 1, 1));
		lblPosition.setText("Position:");
		
		text_lng = new Text(composite, SWT.BORDER);
		text_lng.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		
		text_lat = new Text(composite, SWT.BORDER);
		text_lat.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 1, 1));
		new Label(composite, SWT.NONE);
		
		btnNewButton = new Button(composite, SWT.NONE);
		btnNewButton.setText("Start");
		btnNewButton.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				if( !environment.isRunning() )
					environment.start();
				else
					environment.pause();
				btnNewButton.setText( environment.isPaused()? "Stop": "Start");
				super.widgetSelected(e);
			}
			
		});
		new Label(composite, SWT.NONE);
		
	}

	protected void initComposite(){
		Ship ship = environment.getShip();
		this.text_name.setText( ship.getId() );
		this.text_speed.setText( String.valueOf( ship.getSpeed() ));
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
