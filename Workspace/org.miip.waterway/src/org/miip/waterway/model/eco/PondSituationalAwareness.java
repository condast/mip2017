package org.miip.waterway.model.eco;

import java.util.Collection;

import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.model.def.IReferenceEnvironment;
import org.miip.waterway.sa.AbstractSituationalAwareness;
import org.miip.waterway.sa.SituationEvent;

public class PondSituationalAwareness extends AbstractSituationalAwareness<IReferenceEnvironment<IPhysical>> {

	private IEnvironmentListener<IPhysical> listener = new IEnvironmentListener<IPhysical>() {

		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent<IPhysical> event) {
			notifylisteners( new SituationEvent<IPhysical>( getReference()));
		}
	};
		
	public PondSituationalAwareness( IVessel owner) {
		super( owner );
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
}