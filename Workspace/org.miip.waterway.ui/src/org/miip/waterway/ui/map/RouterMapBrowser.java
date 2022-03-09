package org.miip.waterway.ui.map;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.autonomy.routing.AbstractRouter;
import org.condast.commons.data.colours.RGBA;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.plane.IField;
import org.condast.commons.strings.StringStyler;
import org.condast.js.commons.eval.EvaluationEvent;
import org.condast.js.commons.images.IDefaultMarkers.Markers;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.ProgressEvent;
import org.eclipse.swt.browser.ProgressListener;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.ui.map.ColourEvent.Types;
import org.openlayer.map.control.GeoView;
import org.openlayer.map.control.IconsView;
import org.openlayer.map.control.NavigationView;
import org.openlayer.map.control.PixelView;
import org.openlayer.map.control.ShapesView;
import org.openlayer.map.controller.OpenLayerController;

public class RouterMapBrowser extends Browser {
	private static final long serialVersionUID = 1L;

	public static String S_SCRIPT = "/scripts/routermap.js";

	public static String S_ERR_NO_FIELD_DATA = "The vessel does not have any field data: ";
	public static String S_ERR_NO_GPS_SIGNAL = "NO GPS SIGNAL";

	public static final int DEFAULT_SCAN_DELAY = 20;//20 update pulses

	private OpenLayerController mapController;

	private LatLng home;
	private LatLng[] selected;
	private int counter;
	
	private Router router;
	
	private boolean init;
	
	private boolean busy;
	
	private Collection<IColourListener> listeners;

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
		this.selected = new LatLng[2];
		this.counter = 0;
		this.busy = false;
		this.init = false;
		this.addProgressListener(plistener);
		this.listeners = new ArrayList<>();
	}

	public void addColourListener( IColourListener listener ) {
		this.listeners.add(listener);
	}

	public void removeColourListener( IColourListener listener ) {
		this.listeners.remove(listener);
	}

	public void notifyColourRead( ColourEvent event ) {
		for( IColourListener listener: this.listeners )
			listener.notifyColoursRead( event );
	}

	private void onNotifyEvaluation(EvaluationEvent<Object> event) {
		try {
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
					if( home == null )
						home = new LatLng( "home", (double)arr[0], (double)arr[1]);
					init = true;
					return;
				default:
					break;
				}
			}
			if( ShapesView.Commands.isValue(str))
				return;
			
			//Add waypoints if they are selected
			Object[] coords = (Object[]) event.getData()[2];
			LatLng latlng = new LatLng(( Double) coords[1], (Double)coords[0]);	
			selected[counter] = latlng;
			counter++;
			counter %=2;
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}

	public void setInput( String context ){
		NavigationView navigation = new NavigationView(mapController);
		navigation.getLocation();
	}


	public void setField( IField field ){
		this.router = new Router( field);
		home = field.getCentre();
		ShapesView shapes = new ShapesView( mapController);
		shapes.addShape(field.toWKT());
	}

	public LatLng[] getSelected() {
		return selected;
	}

	public boolean fill() {
		try {
			router.fill();
			notifyColourRead( new ColourEvent( this, router.getInput() ));		
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		return true;
	}

	public void refresh() {
		getDisplay().asyncExec( new Runnable() {

			@Override
			public void run() {
				updateMap();
			}
		});	
	}
	
	protected void updateMap() {
		if( mapController.isExecuting() || busy )
			return;

		if( this.init) {
			init = false;
			GeoView geo = new GeoView(mapController);
			geo.setFieldData( router.getField().toFieldData(17));
			geo.jump();
		}
		IconsView icons = new IconsView( mapController );
		//int[] results = pixels.getPixelColour(vesselData.location);
		//if( results != null ) {
		//Surroundings surroundings = Legend.getLegend(results);
		//surroundings.setRgba(results);
		//vesselData.updateSurroundings(surroundings);
		//}

		icons.clearIcons();
		Markers marker = Markers.PINK;
		if( home == null )
			return;
		icons.addMarker(home, marker, 'H');
		if( router.getField() == null )
			return;

		if( Utils.assertNull(selected))
			return;

		LatLng start = selected[0]; 	
		if( selected[0] == null )
			return;
		
		marker = Markers.BLUE;
		icons.addMarker(start, marker, 'S');

		PixelView pixels = new PixelView( mapController );
		int select = ( counter == 1)?0:1;
		int[] colour = pixels.getPixelColour(selected[select]);
		RGBA[] rgba = new RGBA[1];
		rgba[0] = new RGBA( colour );
		notifyColourRead( new ColourEvent( this, Types.POINT, rgba, select ));		

		if( selected[1] == null )
			return;
		LatLng end = selected[1]; 
		marker = Markers.BLUE;
		icons.addMarker(end, marker, 'E');

		try {
			Collection<RGBA> results = router.getPixels(selected[0], selected[1]);
			if( !Utils.assertNull(results))
				notifyColourRead( new ColourEvent( this, Types.LINE,  results.toArray( new RGBA[ results.size() ]), select ));		
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
		
		try {
			if(!router.isFilled())
				return;
		
			Collection<LatLng> results = router.findRoute();
			marker = Markers.PALEBLUE;
			for( LatLng wp: results) {
				if( wp.equals(start) || wp.equals(end))
					continue;
				icons.addMarker(wp, marker, 'N');
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}


	public void dispose() {
		this.mapController.dispose();
		this.mapController.removeEvaluationListener(e->onNotifyEvaluation(e));
		this.removeProgressListener(plistener);
		super.dispose();
	}
	
	private class Router extends AbstractRouter<RGBA> {

		public Router( IField field ) {
			super(field);
		}	

		@Override
		protected void onFill(Map<Integer, List<RGBA>> map) {
			busy = true;
			List<RGBA> results = null;
			try {
				PixelView pixels = new PixelView( mapController );
				IField field = getField();
				results = pixels.getPixelsColours( field );
				if( Utils.assertNull(results))
					return;
				
				int counter = 0;
				for( int y=0; y<field.getWidth(); y++ ) {
					for( int x=0; x< field.getLength(); x++ ) {
						put( x, y, results.get( counter++));
					}				
				}
				
			}
			finally {
				busy = false;
			}
			int index = (counter==0)?1:0;
			notifyColourRead( new ColourEvent( this, Types.AREA,  results.toArray( new RGBA[ results.size() ]), index ));		
		}

		public List<RGBA> getPixels( LatLng start, LatLng end ){
			busy = true;
			List<RGBA> results = new ArrayList<>();
			try {
				IField field = getField();
				if(!field.isInField(start, 1) || !field.isInField(end, 1))
					return results;
				super.prepare(start, end);
				Map.Entry<Double, Double> vs = field.getPoint(start);
				Map.Entry<Double, Double> diff = field.difference(end, start);
				int length = (int) LatLngUtils.getDistance(start, end);
				for( int i=0; i<length; i++ ) {
					int x = ( int )( vs.getKey() + (diff.getKey()*i)/length);
					int y = ( int )( vs.getValue() + (diff.getValue()*i)/length);
					RGBA rgba = get( x,y );
					if( rgba != null)
						results.add(get( x, y ));
				}
			} catch (Exception e) {
				e.printStackTrace();
			}	
			finally {
				busy = false;
			}
			return results;
		}
			
		@Override
		protected int checkObstruction(LatLng start, LatLng end) {
			List<RGBA> line = getPixels(start, end);
			if( Utils.assertNull(line) || line.size() < 2)
				return -1;
			int length = (int) LatLngUtils.getDistance(start, end);
			int distance = -1;
			RGBA first = line.get(0);		
			for( int i=1; i<line.size(); i++ ){
				RGBA next = line.get(i);
				if( !next.approximate(first, 20 ))
					return (int)((double)i*length/line.size() );
			}
			return distance;
		}

		@SuppressWarnings("unused")
		public boolean findObstruction(LatLng start, long distance, int angle) {
			busy = true;
			try {
				IField field = getField();
				if(!field.isInField(start, 1))
					return false;
				LatLng waypoint = LatLngUtilsDegrees.extrapolate(start, angle, distance);
				if(!field.isInField(waypoint, 1))
					return false;
				if( checkObstruction(start, waypoint) >= 0 )
					return true;
			} catch (Exception e) {
				e.printStackTrace();
			}	
			finally {
				busy = false;
			}
			return false;
		}

		@Override
		protected Collection<TreeNode> findPath(LatLng first, LatLng last, int distance) {
			Collection<TreeNode> results = new ArrayList<>();
			TreeNode node = findTreeNode(first, last, distance, false);
			if( node != null )
				results.add(node);	
			node = findTreeNode(first, last, distance, true);
			if( node != null )
				results.add(node);
			return results;
		}
		
		protected TreeNode findTreeNode( LatLng first, LatLng last, int distance, boolean secondary ) {
			int angle = (int) LatLngUtilsDegrees.getHeading(first, last);
			TreeNode node = null;
			LatLng best = last;
			IField field = getField();
			int degrees = angle %=360;
			int end = ( 180 + angle )%360;
			
			long length = (long) (Math.pow( field.getLength(), 2) + Math.pow( field.getWidth(), 2));
			if( length < 2 )
				return null;
			
			while(( node == null ) && ( degrees != end)){
				degrees = (360 + angle)%360;
				for( long d=1; d<length; d++ ) {
					LatLng waypoint = LatLngUtilsDegrees.extrapolate(first, angle, d);
					if(!field.isInField(waypoint, 1)) {
						if( best == null ) best = waypoint;
						break;
					}
					if( checkObstruction(first, waypoint) < 0 ) {
						if( checkObstruction(last, waypoint) < 0 ) 
							return new TreeNode( waypoint );
					}
					if( secondary ) angle--; else angle++;	
				}
			}
			return (node != null )?node: new TreeNode( best, NodeTypes.NODE );
		}
	}
}
