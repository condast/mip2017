package org.miip.waterway.model;

import org.condast.commons.autonomy.model.IAutonomous;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;

public interface IVessel extends IAutonomous<IPhysical>
{

	float DEFAULT_LENGTH = 4.00f;//4 mtr
		
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
	
	
}