package org.miip.waterway.model;

import org.condast.commons.data.latlng.LatLng;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.sa.ISituationalAwareness;

public interface ICollisionAvoidance {

	/**
	 * Returns true if the algorithm is activated
	 * @return
	 */
	boolean isActive();

	/**
	 * Needed for radars
	 * @return
	 */
	public ISituationalAwareness<IPhysical,?> getSituationalAwareness();

	/**
	 * Tell the vessel to which way point its bearing should be
	 * @param vessel
	 * @param interval
	 * @return
	 */
	public LatLng sail( IVessel vessel, long interval );
}
