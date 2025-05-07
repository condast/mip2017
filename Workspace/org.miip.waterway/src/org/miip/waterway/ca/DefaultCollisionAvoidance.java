package org.miip.waterway.ca;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.autonomy.ca.AbstractVesselCollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidanceStrategy;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.data.plane.FieldData;
import org.miip.waterway.model.IVessel;

public class DefaultCollisionAvoidance extends AbstractVesselCollisionAvoidance<IVessel>{

	public DefaultCollisionAvoidance( IVessel vessel, FieldData field ){
		super( vessel, field, true);
	}

	@Override
	public void clearStrategies() {
		super.clearStrategies();
	}

	public boolean addStrategy( String strategyName ) {
		ICollisionAvoidanceStrategy<VesselRadarData> strategy = super.getDefaultStrategy(strategyName);
		if( strategy == null )
			return false;
		return super.addStrategy(strategy);
	}

	public boolean removeStrategy(String strategyName ) {
		Map<ICollisionAvoidanceStrategy<VesselRadarData>, Boolean> temp =
				new HashMap<>(super.getStrategies());
		boolean result = false;
		for( ICollisionAvoidanceStrategy<VesselRadarData> strategy: temp.keySet()) {
			if( strategy.getName().equals(strategyName))
				result = super.removeStrategy(strategy);
		}
		return result;
	}

	/**
	 * Get the critical distance for passage
	 */
	@Override
	public double getCriticalDistance() {
		IVessel vessel = getReference();
		return vessel.getMinTurnDistance();
	}
}
