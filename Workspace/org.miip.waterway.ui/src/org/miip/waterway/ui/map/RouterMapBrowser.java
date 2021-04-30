package org.miip.waterway.ui.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.session.AbstractSessionHandler;
import org.condast.commons.ui.session.SessionEvent;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.images.IDefaultMarkers.Markers;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.openlayer.map.control.IconsView;
import org.openlayer.map.control.MapField;
import org.openlayer.map.control.NavigationView;
import org.openlayer.map.control.PixelView;
import org.openlayer.map.control.ShapesView;
import org.openlayer.map.controller.OpenLayerController;
import org.openlayer.map.data.IconData;

public class RouterMapBrowser extends Browser {
	private static final long serialVersionUID = 1L;

	public static String S_SCRIPT = "/scripts/routermap.js";

	public static String S_ERR_NO_FIELD_DATA = "The vessel does not have any field data: ";
	public static String S_ERR_NO_GPS_SIGNAL = "NO GPS SIGNAL";

	public static final int DEFAULT_SCAN_DELAY = 20;//20 update pulses

	private OpenLayerController mapController;

	private Collection<LatLng> waypoints;
	private SessionHandler handler;

	private ProgressListener plistener = new ProgressListener() {
		private static final long serialVersionUID = 1L;
		@Override
		public void completed(ProgressEvent event) {
			try{
				logger.info("Browser activated" );
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}

		@Override
		public void changed(ProgressEvent event) {
		}
	};

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public RouterMapBrowser(Composite parent, int style) {
		super(parent, style);
		this.mapController = new OpenLayerController( this, OpenLayerController.createScript( RouterMapBrowser.class.getResourceAsStream(S_SCRIPT)));
		this.mapController.addEvaluationListener(e->onNotifyEvaluation(e));
		this.waypoints = new ArrayList<>();
		this.addProgressListener(plistener);
		this.handler = new SessionHandler();
	}

	private void onNotifyEvaluation(EvaluationEvent<Object> event) {
		try {
			if(OpenLayerController.S_TIMER_ID.equals(event.getId())) {
				logger.info("Timer");
				//updateMap();
				return;
			}
			if(!OpenLayerController.S_CALLBACK_ID.equals(event.getId()))
				return;
			if( Utils.assertNull( event.getData()))
				return;
			Collection<Object> eventData = Arrays.asList(event.getData());
			StringBuilder builder = new StringBuilder();
			builder.append("Map data: ");
			for( Object obj: eventData ) {
				if( obj != null )
					builder.append(obj.toString());
				builder.append(", ");
			}
			logger.fine(builder.toString());

			//If the location of the device was selected, then navigate there
			String str = (String) event.getData()[0];
			if( NavigationView.Commands.isValue(str)) {
				NavigationView.Commands cmd = NavigationView.Commands.valueOf(StringStyler.styleToEnum(str));
				switch( cmd ) {
				case GET_GEO_LOCATION:
					Object[] arr = (Object[]) event.getData()[2];
					LatLng latlng = new LatLng( "home", (double)arr[0], (double)arr[1]);
					return;
				default:
					break;
				}
			}

			//Add waypoints if they are selected
			Object[] coords = (Object[]) event.getData()[2];
			LatLng latlng = new LatLng(( Double) coords[1], (Double)coords[0]);				
			waypoints.add(latlng);
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	public void setInput( String context ){
		NavigationView navigation = new NavigationView(mapController);
		navigation.getLocation();
	}

	public void refresh() {
		try {
			handler.addData(waypoints);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * set up the map when the system is started
	 */
	protected void setupMap() {

		MapField mapfield = new MapField( mapController );
		IconsView icons = new IconsView( mapController );
		PixelView pixels = new PixelView( mapController );
		ShapesView shapes = new ShapesView( mapController );

		icons.clearIcons();
		mapfield.clearField();

		//First retrieve the pixels in order to prevent 
		//scanning markers

		int[] results = null;//pixels.getPixelColour(vesselData.location);
		if( results != null ) {
			//Surroundings surroundings = Legend.getLegend(results);
			//surroundings.setRgba(results);
			//vesselData.updateSurroundings(surroundings);
		}
	}

	protected void updateMap() {
		//if( mapController.isExecuting())
		//	return;

		MapField mapfield = new MapField( mapController );
		IconsView icons = new IconsView( mapController );
		PixelView pixels = new PixelView( mapController );
		ShapesView shapes = new ShapesView( mapController );
		//int[] results = pixels.getPixelColour(vesselData.location);
		//if( results != null ) {
		//Surroundings surroundings = Legend.getLegend(results);
		//surroundings.setRgba(results);
		//vesselData.updateSurroundings(surroundings);
		//}

		//icons.clearIcons();

		if( !Utils.assertNull(waypoints)) {
			Collection<IconData> data = new ArrayList<>();
			Markers marker = Markers.BLUE;
			StringBuilder builder = new StringBuilder();
			for( LatLng wp: waypoints) {
				IconData icon = new IconData( wp.getId(), wp.getId(), wp, marker, 'W', 0 );
				data.add( icon);
				builder.append(data.toString());
				//icons.addMarker( wp.getId(), wp.getId(), wp, marker, 'T', 0);
				logger.fine("Waypoints: " + wp.toString());
				if( StringUtils.isEmpty( icon.getPath()))
					logger.info("STOP");
			}
			String str = "test({\"id\": \"null\",\"name\": \"null\",\"latitude\": \"52.25242318903969\",\"longitude\": \"6.146584153393634\",\"path\": \"/openlayer/images/blue_MarkerW.png\"}" + 
			",{\"id\": \"null\",\"name\": \"null\",\"latitude\": \"52.25242318903971\",\"longitude\": \"6.146584153393636\",\"path\": \"/openlayer/images/blue_MarkerX.png\"}," + 
					" {\"id\": \"null\",\"name\": \"null\",\"latitude\": \"52.252904841094704\",\"longitude\": \"6.145754456629219\",\"path\": \"/openlayer/images/blue_MarkerY.png\"})";//"test(" + builder.toString() + ");";
			evaluate( str );
			requestLayout();
		}

		//logger.info( mapController.toString());
	}


	public void dispose() {
		this.mapController.dispose();
		this.mapController.removeEvaluationListener(e->onNotifyEvaluation(e));
		this.removeProgressListener(plistener);
		super.dispose();
	}
	
	private class SessionHandler extends AbstractSessionHandler<Collection<LatLng>> {

		protected SessionHandler() {
			super(getDisplay());
		}

		@Override
		protected void onHandleSession(SessionEvent<Collection<LatLng>> sevent) {
			updateMap();
		}
		
	}
}
