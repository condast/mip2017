package org.miip.waterway.model;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;

public class Vessel implements IVessel {

	private LatLng location;
	private double speed;
	private double bearing;

	public Vessel(String name, double latitude, double longitude, double bearing, double speed ) {
		this( new LatLng(name, latitude, longitude ), bearing, speed );
	}

	public Vessel( String name, LatLng location, double bearing, double speed) {
		this( name, location.getLatitude(), location.getLongitude(), bearing, speed );
	}
	
	public Vessel( LatLng location, double bearing, double speed) {
		this.location = location;
		this.speed = speed;
		this.bearing = bearing;
	}

	@Override
	public String getName() {
		return location.getId();
	}

	@Override
	public LatLng getLocation() {
		return location;
	}

	@Override
	public double getTurn(long timemsec) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMinTurnDistance() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getSpeed() {
		return speed;
	}

	@Override
	public double getBearing() {
		return bearing;
	}

	
	@Override
	public LatLng plotNext(long interval) {
		double distance = ( this.speed * interval )/3600;// (msec * km/h) = m/3600
		return LatLngUtils.extrapolate( this.location, this.bearing, distance);
	}

	@Override
	public LatLng sail(long timeinMillis) {
		this.location = plotNext( timeinMillis );
		return location;
	}
}
