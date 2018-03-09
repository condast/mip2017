package org.miip.waterway.model;

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

	//private IVessel vessel;
	private ISituationalAwareness<IPhysical,?> sa;
	private boolean activate;
	
	//The collision avoidance translates situational awareness into a collection of waypoints
	private LinkedList<LatLng> waypoints;
	
	private Logger logger = Logger.getLogger( this.getClass().getName());
	
	public AbstractCollisionAvoidance( ISituationalAwareness<IPhysical,?> sa, boolean activate ) {
		this.sa = sa;
		//this.vessel = vessel;
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
		StringBuffer buffer = new StringBuffer();
		//Remember the last waypoint
		LatLng last = this.waypoints.isEmpty()?null: this.waypoints.getLast();
		this.waypoints.clear();
		int furthestInteraction = 0;
		for( AbstractSituationalAwareness<?>.RadarData datum: data ) {
			if( datum.getDistance() > furthestInteraction )
				furthestInteraction = (int) datum.getDistance();
			if( !datum.isPast() || ( datum.getDistance() > sa.getCriticalDistance()))
				continue;
			double distance = sa.getCriticalDistance() - datum.getDistance();
			if( distance < 0 )
				continue;
			double angle = 0;//Always move datum.getAngle() + 180;//turn away from the pending conflict
			buffer.append("Angle is " + angle + "\t");
			//First move away from the obstruction
			LatLng waypoint = LatLngUtils.extrapolate( datum.getLatlng(), angle, sa.getCriticalDistance());
			waypoints.add(waypoint);
		}
		//Try to move back to the original course
		if( last == null ) 
			last = ( furthestInteraction == 0 )? next: LatLngUtils.extrapolate(next, vessel.getBearing(), furthestInteraction + sa.getCriticalDistance());
		
		waypoints.add(last);

		LatLng first = this.waypoints.getFirst();
		double bearing = LatLngUtils.getBearingInDegrees(vessel.getLocation(), first);
		next = plotNext( vessel.getLocation(), interval, bearing, vessel.getSpeed());
		buffer.append(" bearing: " + bearing);
		logger.info( buffer.toString());
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
