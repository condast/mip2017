package org.miip.waterway.model;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.model.IAutonomous;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.data.latlng.Waypoint;

public interface IVessel extends IAutonomous
{
	float DEFAULT_LENGTH = 4.00f;//4 mtr

	/**
	 * A vessel always has at least one destination
	 * @param waypoint
	 */
	void addWayPoint(Waypoint waypoint);

	/**
	 * Returns true if the vessel has collision avoidance
	 * @return
	 */
	public boolean hasCollisionAvoidance();
	
	public ICollisionAvoidance<IVessel, VesselRadarData> getCollisionAvoidance();

	public void setCollisionAvoidance(ICollisionAvoidance<IVessel, VesselRadarData> ca);

	double getTurn(long timemsec);

	double getMinTurnDistance();

	/**
	 * speed in km/h
	 * @return
	 */
	@Override
	double getSpeed();

	@Override
	double getCriticalDistance();
}