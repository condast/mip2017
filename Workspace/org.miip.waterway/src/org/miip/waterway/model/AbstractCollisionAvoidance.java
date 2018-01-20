package org.miip.waterway.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.logging.Logger;

import org.condast.commons.Utils;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.miip.waterway.model.def.IPhysical;

import org.miip.waterway.sa.AbstractSituationalAwareness;
import org.miip.waterway.sa.ISituationalAwareness;

public class AbstractCollisionAvoidance implements ICollisionAvoidance {

	public static int DEFAULT_FUTURE_RANGE = 35000;//35 sec

	private IVessel vessel;
	private ISituationalAwareness<IPhysical,?> sa;
	private boolean activate;
	
	//The collision avoidance translates situational awareness into a collection of waypoints
	private LinkedList<LatLng> waypoints;
	
	private Logger logger = Logger.getLogger( this.getClass().getName());
	
	public AbstractCollisionAvoidance( IVessel vessel, ISituationalAwareness<IPhysical,?> sa, boolean activate ) {
		this.sa = sa;
		this.vessel = vessel;
		this.activate = activate;
		waypoints = new LinkedList<LatLng>();
	}

	@Override
	public boolean isActive() {
		return activate;
	}

	protected void setActive(boolean active) {
		this.activate = active;
	}

	protected IVessel getVessel() {
		return vessel;
	}

	@Override
	public ISituationalAwareness<IPhysical,?> getSituationalAwareness() {
		return sa;
	}

	/**
	 * Calculate the way points and return the next location for the vessel 
	 * @param vessel
	 * @param interval
	 * @return
	 */
	protected synchronized LatLng calculateTrajectory( IVessel vessel, long interval ) {
		LatLng next = vessel.plotNext(interval);
		Collection<AbstractSituationalAwareness<?>.RadarData> data = sa.getShortest();
		if( Utils.assertNull(data))
			return next;
		Collection<LatLng> results = new ArrayList<LatLng>();
		StringBuffer buffer = new StringBuffer();
		for( AbstractSituationalAwareness<?>.RadarData datum: data ) {
			double distance = vessel.getMinTurnDistance() - datum.getDistance();
			if( distance < 0 )
				continue;
			double angle = datum.getAngle() + Math.PI;//turn away from the pending conflict
			buffer.append("Angle is " + angle + "\t");
			//First move away from the obstruction
			LatLng waypoint = LatLngUtils.extrapolate(datum.getLatlng(), angle, distance);
			results.add(waypoint);
			
			//Then move back
			waypoint = LatLngUtils.extrapolate(datum.getLatlng(), datum.getAngle(), distance);
			results.add(waypoint);
		}
		logger.info( buffer.toString());
		
		this.waypoints.clear();
		if( results.isEmpty() ) {
			this.waypoints.add(vessel.plotNext(interval));
		}else {
			this.waypoints.addAll(results);
			
		}
		LatLng first = this.waypoints.getFirst();
		double bearing = LatLngUtils.getBearingInDegrees(vessel.getLocation(), first);
		next = plotNext( vessel.getLocation(), interval, bearing, vessel.getSpeed());
		return next;
	}
	
	public static LatLng plotNext( LatLng location, long interval, double bearing, double speed) {
		double distance = (speed * interval )/3600;// (msec * km/h) = m/3600
		return LatLngUtils.extrapolate( location, bearing, distance);
	}

	@Override
	public LatLng sail( IVessel vessel, long interval ) {
		if(!this.activate )
			return vessel.plotNext(interval);
		sa.update();
		if( !this.waypoints.isEmpty() ) {
			LatLng current = this.waypoints.getFirst();
			if( LatLngUtils.getDistance(current, vessel.getLocation()) < 3) {
				this.waypoints.removeFirst();
			}
		}
		return calculateTrajectory(vessel, interval);
	}
}
