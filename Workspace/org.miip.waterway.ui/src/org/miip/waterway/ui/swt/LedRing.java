package org.miip.waterway.ui.swt;

import java.util.Map;
import java.util.TreeMap;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Motion;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IPhysical;

public class LedRing<I> extends AbstractSWTRadar<IPhysical> {
	private static final long serialVersionUID = 1L;

	public static final int NR_OF_LEDS = 24;
	public static final int RADIUS = 10;
	public static final int DEGREES = 360;
	
	private int leds;
	
	private Map<Integer, Motion> radar;

	public LedRing(Composite parent, int style ) {
		super(parent, style);
		this.leds = NR_OF_LEDS;
		radar = new TreeMap<Integer, Motion>( );
	}
	
	@Override
	protected void onDrawStart(GC gc) {
		this.radar.clear();
		super.onDrawStart(gc);
	}

	@Override
	protected void drawObject( GC gc, IPhysical ship ){
		IVessel reference = (IVessel) getInput().getReference(); 
		double angle = LatLngUtils.getBearing(reference.getLocation(), ship.getLocation());
		int key = ( int )( this.leds * angle /( 2*Math.PI ));
		Motion waypoint = calculate(key, ship );
		this.radar.put(key, waypoint);
	}

	public Motion calculate( int key, IPhysical phys) {
		IVessel reference = (IVessel) getInput().getReference(); 
		double latitude = 0; double longitude = 0;
		double angle = LatLngUtils.getBearing(reference.getLocation(), phys.getLocation());
		double distance = LatLngUtils.getDistance(reference.getLocation(), phys.getLocation());
		Motion waypoint = radar.get(key);
		if( waypoint == null ) {
			waypoint = new Motion( phys.getLocation(), angle, distance );
		}else {
			latitude = ( waypoint.getLocation().getLatitude() + phys.getLocation().getLatitude())/2; 
			longitude = ( waypoint.getLocation().getLongitude() + phys.getLocation().getLongitude())/2; 
			angle += ( waypoint.getBearing() + angle )/2;
			if( distance > waypoint.getDistance() )
				distance = waypoint.getDistance();
			waypoint = new Motion( new LatLng( latitude, longitude ), angle, distance );
		}
		return waypoint;
	}

	@Override
	protected void onDrawEnd(GC gc) {
		double centrex = super.getCentre().x;
		double centrey = super.getCentre().y;
		double length = (centrex < centrey )? centrex: centrey;
		length = (( length - RADIUS ) < 0)? 0: length - RADIUS;
		Color background = gc.getBackground();
		double distance = super.getRange() + 10;
		for( int i=0; i< this.leds; i++ ) {
			Motion waypoint = radar.get( i );
			if( waypoint != null )
				distance = waypoint.getDistance();
			double phi = i * 2 * Math.PI/this.leds;
			double x = length * Math.sin( phi );
			double y = length * Math.cos( phi );
			gc.setBackground( getColour( distance ));
			gc.fillOval((int)(centrex + x), (int)(centrey-y), RADIUS, RADIUS );			
		}
		gc.setBackground(background);
		super.onDrawEnd(gc);
	}
}