package org.miip.waterway.model.eco;

import java.util.Collection;

import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.AbstractSituationalAwareness;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.miip.waterway.model.IVessel;

public class PondSituationalAwareness extends AbstractSituationalAwareness<IReferenceEnvironment<IPhysical>> {

	private IEnvironmentListener<IPhysical> listener = new IEnvironmentListener<IPhysical>() {

		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent<IPhysical> event) {
			notifylisteners( new SituationEvent<IPhysical>( this, getReference()));
		}
	};
		
	public PondSituationalAwareness( IVessel owner) {
		super( owner );
	}

	/**
	 * Get the critical distance for passage 
	 */
	@Override
	public double getCriticalDistance() {
		IVessel vessel = (IVessel) getReference(); 
		return vessel.getMinTurnDistance();
	}

	@Override
	public Collection<IPhysical> getRadar() {
		return super.getInput().getOthers();
	}

	@Override
	protected void onSetInput(IReferenceEnvironment<IPhysical> input) {
		if( super.getInput() != null ) {
			if ( super.getInput().equals(input ))
				return;
			super.getInput().removeListener(listener);
		}
		input.addListener(listener);
	}

	@Override
	public double getNearestDistance() {
		// TODO Auto-generated method stub
		return 0;
	}
}