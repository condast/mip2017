package org.miip.waterway.model;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.model.AbstractModel;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.MotionData;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.latlng.Motion;
import org.condast.commons.data.latlng.Waypoint;

public class Ship extends AbstractModel<Object> implements IVessel{
	
	private static final int DEFAULT_LENGTH = 20;//m

	public static final int TO_HOURS = 60*60;//3600 min.

	public enum Heading{
		NORTH(0),
		EAST(90),
		SOUTH(180),
		WEST(270);
		
		private int degrees;
		
		private Heading( int degress ){
			this.degrees = degress;
		}

		public int getAngle() {
			return degrees;
		}
	}
	
	private float length;
	private float speed;//-20 - 60 km/hour
	private int heading; //0-360
	
	private ISituationalAwareness<IPhysical,IVessel> sa;
	private ICollisionAvoidance<IPhysical,IVessel> ca;
	
	private double rotation;
	private double rot; //Rate of turn (degress/minute

	public Ship( long id, String name, LatLng position, float speed, Heading heading) {
		this( id, name, speed, DEFAULT_LENGTH, position, heading.getAngle() );
	}

	public Ship( long id, String name, float speed, LatLng position) {
		this( id, name, speed, DEFAULT_LENGTH, position, Heading.EAST.getAngle() );
	}
	
	public Ship( long id, String name, float speed, int length, LatLng position, int heading) {
		super( id, name , IPhysical.ModelTypes.VESSEL, position, ICollisionAvoidance.DEFAULT_CRITICAL_DISTANCE, null );
		this.speed = speed;
		this.heading = heading;
		this.length = length;
		this.rotation = (( double )this.length + 5 * Math.random()) * this.speed; 
		this.rot = ( rotation * Math.PI)/30 ; //( v + 5 *rand ) 2 * PI/60)
	}

	public void init(ISituationalAwareness<IPhysical,IVessel> sa, ICollisionAvoidance<IPhysical,IVessel>  ca) {
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
		this.heading = angle;
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
		return this.heading;
	}

	@Override
	public MotionData plotNext(long interval) {
		double distance = ( this.speed * interval )/TO_HOURS;// (msec * km/h) = m/3600
		LatLng next = LatLngUtilsDegrees.extrapolate( super.getLocation(), this.heading, distance);
		return new MotionData( next, this.heading);
	}

	@Override
	public Motion move(long interval ) {
		Motion motion = null;
		if(( this.ca == null ) ||( !this.ca.isActive())) {
			motion = move(interval);
		}else {
			MotionData md = ca.suggest(this, null, interval)[0];
			double speed = getSpeed();
			motion = new Motion( getID(), md.getLocation(), md.getHeading(), speed );
		}
		super.setLocation( motion.getLocation());
		return motion;
	}

	@Override
	public ISituationalAwareness<IPhysical, IVessel> getSituationalAwareness() {
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
		Heading bearing = ( Math.random() < 0.5f)? Heading.EAST: Heading.WEST;
		long id=  name.hashCode();
		return new Ship( id, name, lnglat, (float) speed, bearing);
	}

	@Override
	public String[] getSelectedStrategies() {
		return ca.getSelectedStrategies();
	}

	@Override
	public void init(ISituationalAwareness<IPhysical, IVessel> sa) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void clearStrategies() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean addStrategy(String strategyName) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeStrategy(String strategyName) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@Override
	public IPhysical clone() throws CloneNotSupportedException {
		return new Ship(super.getID(), super.getIdentifier(), speed, (int)length, super.getLocation(), heading );
	}

	@Override
	public void addWayPoint(Waypoint waypoint) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public MotionData getCurrent() {
		// TODO Auto-generated method stub
		return null;
	}
}