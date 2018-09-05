package org.miip.waterway.model;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.model.IAutonomous;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;

public interface IVessel extends IAutonomous<IPhysical>
{
	float DEFAULT_LENGTH = 4.00f;//4 mtr

	void init(ISituationalAwareness<IVessel, IPhysical> sa, ICollisionAvoidance<IVessel, IPhysical> ca);
	
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

	/**
	 * in degrees (0-360) starting from North
	 * @return
	 */
	double getBearing();

	ISituationalAwareness<IVessel, IPhysical> getSituationalAwareness();

	/**
	 * Returns true if the given physical object is too near
	 * @param physical
	 * @return
	 */
	boolean isInCriticalDistance(IPhysical physical);

	double getCriticalDistance();	
}