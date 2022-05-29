package org.miip.waterway.ca;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.autonomy.ca.AbstractVesselCollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidanceStrategy;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.data.plane.FieldData;
import org.condast.commons.data.plane.IField;
import org.miip.waterway.model.IVessel;

public class DefaultCollisionAvoidance extends AbstractVesselCollisionAvoidance<IPhysical, IVessel>{

	public DefaultCollisionAvoidance( IVessel vessel ){
		super( vessel,  true);
	}

	public DefaultCollisionAvoidance( IVessel vessel, FieldData field ){
		super( vessel, true);
		this.setField(field);
	}

	@Override
	public void clearStrategies() {
		super.clearStrategies();
	}

	public boolean addStrategy( String strategyName ) {
		ICollisionAvoidanceStrategy strategy = super.getDefaultStrategy(strategyName);
		if( strategy == null )
			return false;
		return super.addStrategy(strategy);
	}

	public boolean removeStrategy(String strategyName ) {
		Collection<ICollisionAvoidanceStrategy> temp =
				new ArrayList<>(super.getStrategies());
		boolean result = false;
		for( ICollisionAvoidanceStrategy strategy: temp) {
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
