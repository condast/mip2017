package org.miip.waterway.radar;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.IRadarData;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Motion;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IMIIPRadar;

public class LedRadar<O extends IPhysical,V extends IPhysical>{

	private Map<Integer, Motion> radarData;
	private IMIIPRadar<V> radar;

	public LedRadar( IMIIPRadar<V> radar ) {
		super();
		this.radar = radar;
		radarData = new HashMap<>();
	}

	public Map<Integer, Motion> getRadarData() {
		return radarData;
	}

	public Motion getRadarData( int angle ) {
		return this.radarData.get( angle );
	}

	public void refresh() {
		ISituationalAwareness<VesselRadarData> sa = null;//radar.getInput();
		this.radarData.clear();
		if( sa == null )
			return;
		IVessel reference = null;//(IVessel) radar.getInput().getReference();
		for( IRadarData<VesselRadarData> radarData: sa.getRadarData() ){
			VesselRadarData data = radarData.getData();
			double angle = LatLngUtils.getHeading(reference.getLocation(), data.getLocation());
			int key = ( int )( radar.getSteps() * angle /( 2*Math.PI ));
			Motion waypoint = calculate(key, data );
			this.radarData.put(key, waypoint);
		}
	}

	public Motion calculate( int key, VesselRadarData phys) {
		IVessel reference = null;//(IVessel) radar.getInput().getReference();
		double latitude = 0; double longitude = 0;
		double angle = LatLngUtils.getHeading(reference.getLocation(), phys.getLocation());
		double distance = LatLngUtils.getDistance(reference.getLocation(), phys.getLocation());
		Motion waypoint = radarData.get(key);
		if( waypoint == null ) {
			waypoint = new Motion(reference.getID(), phys.getLocation(), angle, distance );
		}else {
			latitude = ( waypoint.getLocation().getLatitude() + phys.getLocation().getLatitude())/2;
			longitude = ( waypoint.getLocation().getLongitude() + phys.getLocation().getLongitude())/2;
			angle += ( waypoint.getHeading() + angle )/2;
			if( distance > waypoint.getDistance() )
				distance = waypoint.getDistance();
			waypoint = new Motion( reference.getID(), new LatLng( latitude, longitude ), angle, distance );
		}
		return waypoint;
	}
}