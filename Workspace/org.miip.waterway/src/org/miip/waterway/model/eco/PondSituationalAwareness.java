package org.miip.waterway.model.eco;

import java.util.Collection;

import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.AbstractAutonomousSituationalAwareness;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.data.latlng.IField;
import org.miip.waterway.model.IVessel;

public class PondSituationalAwareness extends AbstractAutonomousSituationalAwareness<IPhysical,IVessel> {

	private IEnvironmentListener<IVessel> listener = new IEnvironmentListener<IVessel>() {

		@Override
		public void notifyEnvironmentChanged(EnvironmentEvent<IVessel> event) {
			notifylisteners( new SituationEvent<IPhysical>( this, getReference()));
		}
	};
		
	public PondSituationalAwareness( IVessel owner, IField field) {
		super( owner, (int)( field.getDiameter()/3));
		IVessel vessel = (IVessel) getReference(); 
		super.setCriticalDistance( vessel.getMinTurnDistance());
	}


	@Override
	public Collection<IPhysical> getScan() {
		return super.getInput().getOthers();
	}

	@Override
	protected void onSetInput(IReferenceEnvironment<IVessel, IPhysical> input) {
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