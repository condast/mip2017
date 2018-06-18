package org.miip.waterway.radar;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Motion;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IRadar;

public class LedRadar<V extends IPhysical>{
	
	private Map<Integer, Motion> radarData;
	private IRadar<V> radar;
	
	public LedRadar( IRadar<V> radar ) {
		super(); 
		this.radar = radar;
		radarData = new HashMap<Integer, Motion>();
	}
	
	public Map<Integer, Motion> getRadarData() {
		return radarData;
	}

	public Motion getRadarData( int angle ) {
		return this.radarData.get( angle );
	}

	public void refresh() {
		ISituationalAwareness<V,?> sa = radar.getInput();
		this.radarData.clear();
		if( sa == null )
			return;
		IVessel reference = (IVessel) radar.getInput().getReference(); 
		for( V obj: sa.getRadar() ){
			double angle = LatLngUtils.getBearing(reference.getLocation(), obj.getLocation());
			int key = ( int )( radar.getSteps() * angle /( 2*Math.PI ));
			Motion waypoint = calculate(key, obj );
			this.radarData.put(key, waypoint);
		}
	}

	public Motion calculate( int key, IPhysical phys) {
		IVessel reference = (IVessel) radar.getInput().getReference(); 
		double latitude = 0; double longitude = 0;
		double angle = LatLngUtils.getBearing(reference.getLocation(), phys.getLocation());
		double distance = LatLngUtils.getDistance(reference.getLocation(), phys.getLocation());
		Motion waypoint = radarData.get(key);
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
}