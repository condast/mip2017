package org.miip.waterway.model;

import org.condast.commons.data.latlng.LatLng;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.sa.ISituationalAwareness;

public interface ICollisionAvoidance {

	/**
	 * Sail the vessel
	 * @param interval
	 * @return
	 */
	public LatLng sail( long interval);

	public ISituationalAwareness<IPhysical,?> getSituationalAwareness();
}
