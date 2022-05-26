package org.miip.waterway.model;

import java.util.ArrayList;
import java.util.List;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.model.AbstractAutonomous;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.MotionData;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Motion;
import org.condast.commons.data.latlng.Waypoint;

public class Vessel extends AbstractAutonomous<IPhysical, IVessel, VesselRadarData> implements IVessel {

	private float length;//mtr
	private double maxSpeed;

	private List<Waypoint> waypoints;

	private boolean enableCA;

	public Vessel( long id, String name, double latitude, double longitude, double heading, double thrust, double maxSpeed, boolean enableCa) {
		this( id, name, new LatLng(name, latitude, longitude ), heading, thrust, maxSpeed, enableCa);
	}

	public Vessel( long id, String name, LatLng location, double heading, boolean enableCa) {
		this( id, name, location, heading, 100, 100, enableCa );
	}

	/**
	 * Create a vessel with the given name, heading (radians) and speed
	 * @param location
	 * @param heading
	 * @param thrust
	 */
	protected Vessel( long id, String name, LatLng location, double heading, double thrust, double maxSpeed, boolean enableCa) {
		super( id, name, IPhysical.ModelTypes.VESSEL, location, heading, thrust);
		this.length = IVessel.DEFAULT_LENGTH;
		this.maxSpeed = maxSpeed;
		this.enableCA = enableCa;
		this.waypoints = new ArrayList<>();
	}

	public void clear() {
		this.waypoints.clear();
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

	public boolean destinationReached() {
		for( Waypoint wp: this.waypoints) {
			if( !wp.isCompleted())
				return false;
		}
		return true;
	}

	@Override
	public boolean hasCollisionAvoidance() {
		ICollisionAvoidance<IVessel, VesselRadarData> ca = super.getCollisionAvoidance();
		return (ca != null ) &&( ca.isActive());
	}

	@Override
	public double getCriticalDistance() {
		ICollisionAvoidance<IVessel, VesselRadarData> ca = super.getCollisionAvoidance();
		return ( ca == null )? ICollisionAvoidance.DEFAULT_CRITICAL_DISTANCE: ca.getCriticalDistance();
	}

	public boolean isInCriticalDistance( IPhysical physical ) {
		ICollisionAvoidance<IVessel, VesselRadarData> ca = super.getCollisionAvoidance();
		double critical = ( ca == null )? ICollisionAvoidance.DEFAULT_CRITICAL_DISTANCE: ca.getCriticalDistance();
		double distance = LatLngUtils.getDistance(this.getLocation(), physical.getLocation());
		return distance <= critical;
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

		Motion motion = super.move(destination.getLocation(), interval);
		if( destination.destinationReached(getLocation())) {
			destination.setCompleted(true);
		}
		return motion;
	}

	@Override
	public IPhysical clone() throws CloneNotSupportedException {
		MotionData motionData = getCurrent();
		Vessel vessel = new Vessel(getID(), getName(), getLocation(), getHeading(), motionData.getThrust(), this.maxSpeed, this.enableCA);
		vessel.length = length;
		return vessel;
	}
}