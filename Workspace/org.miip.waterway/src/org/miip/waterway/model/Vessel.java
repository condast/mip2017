package org.miip.waterway.model;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.autonomy.ca.AbstractCollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidanceStrategy;
import org.condast.commons.autonomy.model.AbstractModel;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.plane.IField;
import org.condast.commons.strings.StringUtils;

public class Vessel extends AbstractModel<Object> implements IVessel {

	private String name;
	private double speed;
	private double heading;//rad
	private float length;//mtr
	private IField field;
	
	/**
	 * Needed for awareness of its environment
	 * @return
	 */
	private ISituationalAwareness<IVessel, IPhysical> sa;
	private DefaultCollisionAvoidance ca;

	public Vessel(String name, double latitude, double longitude, double bearing, double speed) {
		this( new LatLng(name, latitude, longitude ), bearing, speed);
		this.name = name;
	}

	public Vessel( String name, LatLng location, double bearing, double speed) {
		this( name, location.getLatitude(), location.getLongitude(), bearing, speed );
		this.name = name;
	}
	
	/**
	 * Create a vessel with the given name, bearing (radians) and speed
	 * @param location
	 * @param bearing
	 * @param speed
	 */
	public Vessel( LatLng location, double bearing, double speed) {
		super( IPhysical.ModelTypes.VESSEL, location );
		this.name = location.getId();
		this.speed = speed;
		this.heading = bearing;
		this.length = IVessel.DEFAULT_LENGTH;
	}
	
	@Override
	public void init( ISituationalAwareness<IVessel, IPhysical> sa, IField field ) {
		this.sa = sa;
		this.field = field;
		this.ca = new DefaultCollisionAvoidance(this, sa);
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setLocation(LatLng lnglat) {
		super.setLocation(lnglat);
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
	public double getHeading() {
		return heading;
	}

	public void setHeading(double bearing) {
		this.heading = bearing;
	}

	//@Override
	public ISituationalAwareness<IVessel, IPhysical> getSituationalAwareness(){
		return sa;
	}

	@Override
	public void clearStrategies() {
		ca.clearStrategies();
	}

	@Override
	public boolean addStrategy(String strategyName) {
		return ca.addStrategy(strategyName);
	}


	@Override
	public boolean removeStrategy(String strategyName ) {
		return ca.removeStrategy(strategyName);
	}

	/**
	 * Get the selected strategies for collision avoidance
	 * @return
	 */
	@Override
	public String[] getSelectedStrategies() {
		return this.ca.getSelectedStrategies();
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

	@Override
	public LatLng plotNext(long interval) {
		double distance = ( this.speed * interval )/3600;// (msec * km/h) = m/3600
		return LatLngUtilsDegrees.extrapolate( super.getLocation(), this.heading, distance);
	}

	@Override
	public LatLng move(long interval ) {
		LatLng location = super.getLocation();
		if(( this.ca == null ) ||(!ca.isActive())){
			location= plotNext(interval);
		}else {
			location = ca.move( this, interval ).getLocation();
		}
		super.setLocation(location);
		return location;
	}
	
	private class DefaultCollisionAvoidance extends AbstractCollisionAvoidance<IPhysical, IVessel>{

		public DefaultCollisionAvoidance( IVessel vessel, ISituationalAwareness<IVessel, IPhysical> sa ){
			super( field, sa, true);
			if( StringUtils.isEmpty( vessel.getName()))
				System.out.println("STOP!!!!");
			addStrategy( ICollisionAvoidanceStrategy.DefaultStrategies.FLANK_STRATEGY.name());
			setActive(!( vessel.getName().toLowerCase().equals("other")));
		}
		
		@Override
		protected void clearStrategies() {
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
