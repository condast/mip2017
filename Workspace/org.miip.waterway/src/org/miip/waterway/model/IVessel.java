package org.miip.waterway.model;

import org.condast.commons.autonomy.model.IAutonomous;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.Waypoint;

public interface IVessel extends IAutonomous<IPhysical>
{
	float DEFAULT_LENGTH = 4.00f;//4 mtr

	void init(ISituationalAwareness<IPhysical,IVessel> sa);	

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
	
	double getTurn(long timemsec);

	double getMinTurnDistance();

	/**
	 * speed in km/h
	 * @return
	 */
	double getSpeed();

	ISituationalAwareness<IPhysical, IVessel> getSituationalAwareness();

	/**
	 * Returns true if the given physical object is too near
	 * @param physical
	 * @return
	 */
	boolean isInCriticalDistance(IPhysical physical);

	double getCriticalDistance();

	void clearStrategies();

	boolean addStrategy(String strategyName );

	boolean removeStrategy(String strategyName );
}