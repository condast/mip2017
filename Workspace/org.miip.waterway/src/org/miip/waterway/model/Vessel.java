package org.miip.waterway.model;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.model.AbstractModel;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;

public class Vessel extends AbstractModel implements IVessel {

	private double speed;
	private double bearing;//rad
	private float length;//mtr
	
	/**
	 * Needed for awareness of its environment
	 * @return
	 */
	private ISituationalAwareness<IVessel, IPhysical> sa;
	private ICollisionAvoidance<IVessel, IPhysical> ca;

	public Vessel(String name, double latitude, double longitude, double bearing, double speed) {
		this( new LatLng(name, latitude, longitude ), bearing, speed);
	}

	public Vessel( String name, LatLng location, double bearing, double speed) {
		this( name, location.getLatitude(), location.getLongitude(), bearing, speed );
	}
	
	/**
	 * Create a vessel with the given name, bearing (radians) and speed
	 * @param location
	 * @param bearing
	 * @param speed
	 */
	public Vessel( LatLng location, double bearing, double speed) {
		super( IPhysical.ModelTypes.VESSEL, location );
		this.speed = speed;
		this.bearing = bearing;
		this.length = IVessel.DEFAULT_LENGTH;
	}
	
	@Override
	public void init( ISituationalAwareness<IVessel, IPhysical> sa, ICollisionAvoidance<IVessel, IPhysical> ca ) {
		this.sa = sa;
		this.ca = ca;
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

	//@Override
	public ISituationalAwareness<IVessel, IPhysical> getSituationalAwareness(){
		return sa;
	}
	
	public ICollisionAvoidance<IVessel, IPhysical> getCollisionAvoidance() {
		return ca;
	}

	@Override
	public double getCriticalDistance() {
		return ( ca == null )? ICollisionAvoidance.DEFAULT_CRITICAL_DISTANCE: ca.getCriticalDistance();		
	}

	@Override
	public boolean isInCriticalDistance( IPhysical physical ) {
		double critical = ( ca == null )? ICollisionAvoidance.DEFAULT_CRITICAL_DISTANCE: ca.getCriticalDistance();
		return LatLngUtils.getDistance(this.getLocation(), physical.getLocation()) <= critical;
	}

	@Override
	public boolean hasCollisionAvoidance() {
		return (this.ca != null ) &&( this.ca.isActive());
	}

	//@Override
	public void setCollisionAvoidance(ICollisionAvoidance<IVessel, IPhysical> ca) {
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
			location = ca.move( this, interval ).getLocation();
		}
		super.setLnglat(location);
		return location;
	}
}
