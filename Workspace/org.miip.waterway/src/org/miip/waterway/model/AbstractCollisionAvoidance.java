package org.miip.waterway.model;

import org.condast.commons.data.latlng.LatLng;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.sa.ISituationalAwareness;

public class AbstractCollisionAvoidance implements ICollisionAvoidance {

	private IVessel vessel;
	private ISituationalAwareness<?,IPhysical> sa;
	
	public AbstractCollisionAvoidance( IVessel vessel, ISituationalAwareness<?,IPhysical> sa ) {
		this.sa = sa;
		this.vessel = vessel;
	}

	protected IVessel getVessel() {
		return vessel;
	}

	@Override
	public ISituationalAwareness<?, IPhysical> getSituationalAwareness() {
		return sa;
	}

	@Override
	public LatLng sail(long interval) {
		LatLng location = vessel.plotNext( interval );
		return location;
	}

}
