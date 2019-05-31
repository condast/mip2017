package org.miip.waterway.model;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.model.AbstractModel;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;

public class Ship extends AbstractModel<Object> implements IVessel{
	
	private static final int DEFAULT_LENGTH = 20;//m

	public static final int TO_HOURS = 60*60;//3600 min.

	public enum Bearing{
		NORTH(0),
		EAST(90),
		SOUTH(180),
		WEST(270);
		
		private int degrees;
		
		private Bearing( int degress ){
			this.degrees = degress;
		}

		public int getAngle() {
			return degrees;
		}
	}
	
	private float length;
	private float speed;//-20 - 60 km/hour
	private int bearing; //0-360
	
	private ISituationalAwareness<IVessel, IPhysical> sa;
	private ICollisionAvoidance<IVessel, IPhysical> ca;
	
	private double rotation;
	private double rot; //Rate of turn (degress/minute

	public Ship( String id, LatLng position, float speed, Bearing bearing) {
		this( id, speed, DEFAULT_LENGTH, position, bearing );
	}

	public Ship( String id, float speed, LatLng position) {
		this( id, speed, DEFAULT_LENGTH, position, Bearing.EAST );
	}
	
	public Ship( String id, float speed, int length, LatLng position, Bearing bearing) {
		super( id, IPhysical.ModelTypes.VESSEL, position, null );
		this.speed = speed;
		this.bearing = bearing.getAngle();
		this.length = length;
		this.rotation = (( double )this.length + 5 * Math.random()) * this.speed; 
		this.rot = ( rotation * Math.PI)/30 ; //( v + 5 *rand ) 2 * PI/60)
	}

	public void init(ISituationalAwareness<IVessel, IPhysical> sa, ICollisionAvoidance<IVessel, IPhysical>  ca) {
		this.sa = sa;
		this.ca = ca;
	}

	@Override
	public boolean hasCollisionAvoidance() {
		return (this.ca != null ) &&( this.ca.isActive());
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
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.IVessel#getTurn(long)
	 */
	@Override
	public double getTurn( long timemsec ){
		return timemsec * this.rot * 60000;
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.IVessel#getMinTurnDistance()
	 */
	@Override
	public double getMinTurnDistance(){
		return this.rotation * Math.tan( Math.toRadians(1));//the distance of a one degree turn
	}

	protected void setBearing( int angle ){
		this.bearing = angle;
	}

	@Override
	public String getName() {
		return super.getIdentifier();
	}

	@Override
	public LatLng getLocation() {
		return super.getLocation();
	}

	@Override
	public double getSpeed() {
		return this.speed;
	}

	@Override
	public double getHeading() {
		return this.bearing;
	}

	@Override
	public LatLng plotNext(long interval) {
		double distance = ( this.speed * interval )/TO_HOURS;// (msec * km/h) = m/3600
		return LatLngUtilsDegrees.extrapolate( super.getLocation(), this.bearing, distance);
	}

	@Override
	public LatLng move(long interval ) {
		LatLng location = super.getLocation();
		if(( this.ca == null ) ||( !this.ca.isActive())) {
			location = plotNext(interval);
		}else {
			location = ca.move( this, interval ).getLocation();
		}
		super.setLocation(location);
		return location;
	}

	@Override
	public ISituationalAwareness<IVessel, IPhysical> getSituationalAwareness() {
		return sa;
	}

	/**
	 * Create a new ship with random values
	 * @param lnglat
	 * @param name
	 * @return
	 */
	public static Ship createShip( LatLng lnglat, String name ){
		double speed = 10 + ( 60 * Math.random());
		Bearing bearing = ( Math.random() < 0.5f)? Bearing.EAST: Bearing.WEST;
		return new Ship( name, lnglat, (float) speed, bearing);
	}
}