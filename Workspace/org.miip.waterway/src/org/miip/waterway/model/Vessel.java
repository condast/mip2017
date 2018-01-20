package org.miip.waterway.model;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.miip.waterway.internal.model.AbstractModel;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.sa.ISituationalAwareness;

public class Vessel extends AbstractModel implements IVessel {

	private double speed;
	private double bearing;
	private float length;//mtr
	
	private ICollisionAvoidance ca;

	public Vessel(String name, double latitude, double longitude, double bearing, double speed) {
		this( new LatLng(name, latitude, longitude ), bearing, speed);
	}

	public Vessel( String name, LatLng location, double bearing, double speed) {
		this( name, location.getLatitude(), location.getLongitude(), bearing, speed );
	}
	
	public Vessel( LatLng location, double bearing, double speed) {
		super( IPhysical.ModelTypes.VESSEL, location );
		this.speed = speed;
		this.bearing = bearing;
		this.length = IVessel.DEFAULT_LENGTH;
	}

	@Override
	public String getName() {
		return super.getLocation().getId();
	}

	@Override
	public double getTurn(long timemsec) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public double getMinTurnDistance() {
		return 3*this.length;
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
	public ISituationalAwareness<IPhysical,?> getSituationalAwareness(){
		return ca.getSituationalAwareness();
	}
	
	public ICollisionAvoidance getCollisionAvoidance() {
		return ca;
	}

	@Override
	public void setCollisionAvoidance(ICollisionAvoidance ca) {
		this.ca = ca;
	}

	@Override
	public LatLng plotNext(long interval) {
		double distance = ( this.speed * interval )/3600;// (msec * km/h) = m/3600
		return LatLngUtils.extrapolate( super.getLocation(), this.bearing, distance);
	}

	@Override
	public LatLng sail(long interval ) {
		LatLng location = super.getLocation();
		if(( this.ca == null ) ||(!ca.isActive())){
			location= plotNext(interval);
		}else {
			location = ca.sail( this, interval );
		}
		super.setLnglat(location);
		return location;
	}
}
