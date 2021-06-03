package org.miip.waterway.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.condast.commons.autonomy.ca.AbstractCollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidanceStrategy;
import org.condast.commons.autonomy.model.AbstractAutonomous;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.MotionData;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Motion;
import org.condast.commons.data.latlng.Waypoint;
import org.condast.commons.strings.StringUtils;

public class Vessel extends AbstractAutonomous<IPhysical, IVessel,Object> implements IVessel {

	private float length;//mtr
	private double maxSpeed;

	private List<Waypoint> waypoints;
	
	/**
	 * Needed for awareness of its environment
	 * @return
	 */
	private ISituationalAwareness<IPhysical, IVessel> sa;

	public Vessel( long id, String name, double latitude, double longitude, double heading, double thrust, double maxSpeed) {
		this( id, name, new LatLng(name, latitude, longitude ), heading, thrust, maxSpeed);
	}

	/**
	 * Create a vessel with the given name, heading (radians) and speed
	 * @param location
	 * @param heading
	 * @param thrust
	 */
	public Vessel( long id, String name, LatLng location, double heading, double thrust, double maxSpeed) {
		super( id, name, IPhysical.ModelTypes.VESSEL, location, heading, thrust);
		this.length = IVessel.DEFAULT_LENGTH;
		this.maxSpeed = maxSpeed;
		this.waypoints = new ArrayList<>();
	}

	public void clear() {
		this.waypoints.clear();
	}
	
	@Override
	public void init( ISituationalAwareness<IPhysical, IVessel> sa ) {
		this.sa = sa;
		super.setCollisionAvoidance( new DefaultCollisionAvoidance(this, sa));
	}

	@Override
	public void addWayPoint( Waypoint waypoint ) {
		this.waypoints.add(waypoint);
	}

	@Override
	public void setLocation(LatLng lnglat) {
		super.setLocation(lnglat);
	}
	
	@Override
	public double getTurn(long timemsec) {
		return 0;
	}

	@Override
	public double getMinTurnDistance() {
		return 2*this.length;
	}
	
	@Override
	public ISituationalAwareness<IPhysical, IVessel> getSituationalAwareness() {
		return sa;
	}

	@Override
	public void clearStrategies() {
		ICollisionAvoidance<IPhysical, IVessel> ca = super.getCollisionAvoidance();
		ca.clearStrategies();
	}

	@Override
	public boolean addStrategy(String strategyName) {
		DefaultCollisionAvoidance ca = (DefaultCollisionAvoidance) super.getCollisionAvoidance();
		return ca.addStrategy(strategyName);
	}

	@Override
	public boolean removeStrategy(String strategyName ) {
		DefaultCollisionAvoidance ca = (DefaultCollisionAvoidance) super.getCollisionAvoidance();
		return ca.removeStrategy(strategyName);
	}

	/**
	 * Get the selected strategies for collision avoidance
	 * @return
	 */
	@Override
	public String[] getSelectedStrategies() {
		ICollisionAvoidance<IPhysical, IVessel> ca = super.getCollisionAvoidance();
		return ca.getSelectedStrategies();
	}

	@Override
	public double getCriticalDistance() {
		ICollisionAvoidance<IPhysical, IVessel> ca = super.getCollisionAvoidance();
		return ( ca == null )? ICollisionAvoidance.DEFAULT_CRITICAL_DISTANCE: ca.getCriticalDistance();		
	}

	@Override
	public boolean isInCriticalDistance( IPhysical physical ) {
		ICollisionAvoidance<IPhysical, IVessel> ca = super.getCollisionAvoidance();
		double critical = ( ca == null )? ICollisionAvoidance.DEFAULT_CRITICAL_DISTANCE: ca.getCriticalDistance();
		return LatLngUtils.getDistance(this.getLocation(), physical.getLocation()) <= critical;
	}

	@Override
	public boolean hasCollisionAvoidance() {
		ICollisionAvoidance<IPhysical, IVessel> ca = super.getCollisionAvoidance();
		return (ca != null ) &&( ca.isActive());
	}
	
	@Override
	protected double calculateSpeed(long interval, double thrust) {
		return maxSpeed * thrust/100;
	}

	@Override
	public Motion move(long interval ) {
		Waypoint destination = ( this.waypoints.isEmpty())?null: waypoints.iterator().next();
		if(( destination == null ) || destination.isCompleted())
			return new Motion(super.getID(), this.getLocation());

		double heading = Math.toDegrees( LatLngUtils.getHeading(getLocation(), destination.getLocation()));
		super.getCurrent().setHeading(heading);
		Motion motion = super.move(interval);
		if( destination.destinationReached(getLocation())) {
			destination.setCompleted(true);
		}
		return motion;
	}
	
	@Override
	public IPhysical clone() throws CloneNotSupportedException {
		MotionData motionData = getCurrent();
		Vessel vessel = new Vessel(getID(), getName(), getLocation(), getHeading(), motionData.getThrust(), this.maxSpeed);
		vessel.length = length;
		return vessel;
	}

	private class DefaultCollisionAvoidance extends AbstractCollisionAvoidance<IPhysical, IVessel>{

		public DefaultCollisionAvoidance( IVessel vessel, ISituationalAwareness<IPhysical, IVessel> sa ){
			super( sa, true);
			if( StringUtils.isEmpty( vessel.getName()))
				System.out.println("STOP!!!!");
			setActive(!( vessel.getName().toLowerCase().equals("other")));
		}
		
		@Override
		public void clearStrategies() {
			super.clearStrategies();
		}

		protected boolean addStrategy( String strategyName ) {
			ICollisionAvoidanceStrategy<IPhysical, IVessel> strategy = super.getDefaultStrategy(strategyName);
			if( strategy == null )
				return false;
			return super.addStrategy(strategy);
		}

		protected boolean removeStrategy(String strategyName ) {
			Collection<ICollisionAvoidanceStrategy<IPhysical, IVessel>> temp =
					new ArrayList<ICollisionAvoidanceStrategy<IPhysical, IVessel>>(super.getStrategies());
			boolean result = false;
			for( ICollisionAvoidanceStrategy<IPhysical, IVessel> strategy: temp) {
				if( strategy.getName().equals(strategyName))
					result = super.removeStrategy(strategy);
			}
			return result;
		}

		/**
		 * Get the critical distance for passage 
		 */
		@Override
		public double getCriticalDistance() {
			IVessel vessel = (IVessel) getReference(); 
			return vessel.getMinTurnDistance();
		}
	}
}