package test.org.condast.rcp;

import java.util.Timer;
import java.util.TimerTask;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.js.commons.controller.IJavascriptController;
import org.condast.js.commons.images.IDefaultMarkers;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.SWT;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.miip.waterway.model.def.MapLocation;
import org.openlayer.map.control.IconsView;
import org.openlayer.map.controller.OpenLayerController;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	public static final int DEFAULT_HANDLERS = 1;
	public static final long DEFAULT_DELAY = 100;
	
	private Browser browser;
	private IJavascriptController controller; 

	private Timer timer;
	private TimerTask runTimer;

	private Text text;
	private int counter, x, y;
	
	private LatLng latlng;
	
	public BasicEntryPoint() {
		super();
		timer = new Timer();
		runTimer = new RunTimer();
		long delay = DEFAULT_DELAY;
		timer.schedule(runTimer, delay, delay);
		this.counter = 0;
		latlng = MapLocation.Location.HEIJPLAAT.toLatLng();
	}

	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new GridLayout(2, false));
        Button checkbox = new Button(parent, SWT.CHECK);
        checkbox.setText("Hello");
        text = new Text(parent, SWT.PUSH);
        text.setText("0");
        text.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, false ));
        browser = new Browser( parent, SWT.BORDER);
        browser.setLayoutData( new GridData( SWT.FILL, SWT.FILL, true, true, 2, 1 ));
        controller = new OpenLayerController(browser);   
	}
		
	private class RunTimer extends TimerTask{

		@Override
		public void run() {
			try {
				update( counter++);
			}
			catch( Exception ex) {
				ex.printStackTrace();
			}
		}
		
		private void update( int counter ) {
			try {
				IconsView icons = new IconsView( controller );
				x = (int)(Math.random()*counter)%100;
				y = (int)(Math.random()*counter)%100;

				int rand = (int)(Math.random()* IDefaultMarkers.Markers.values().length);
				IDefaultMarkers.Markers marker = IDefaultMarkers.Markers.values()[ rand ];
				LatLng location = LatLngUtils.transform(latlng, x, y);
				char sign = ( char)( counter%26 + 65);
				icons.clearIcons();
				icons.addMarker(location, marker, sign);
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}		
		}

	}
}
