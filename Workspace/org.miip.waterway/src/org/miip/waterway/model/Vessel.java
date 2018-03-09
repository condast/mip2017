package org.miip.waterway.model;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.model.AbstractModel;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;

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
		return 2*this.length;
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
	public LatLng move(long interval ) {
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
