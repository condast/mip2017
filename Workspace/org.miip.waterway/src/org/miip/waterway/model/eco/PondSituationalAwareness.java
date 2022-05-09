package org.miip.waterway.model.eco;

import java.util.Collection;

import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.AbstractAutonomousSituationalAwareness;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.autonomy.sa.radar.RadarData;
import org.condast.commons.data.plane.IField;
import org.miip.waterway.model.IVessel;

public class PondSituationalAwareness extends AbstractAutonomousSituationalAwareness<IPhysical,IVessel> {

	public PondSituationalAwareness( IVessel owner, IField field) {
		super( owner, (int)( field.getDiameter()/3));
		//super.setCriticalDistance( vessel.getMinTurnDistance());
	}

	private void onNotifyEnvironmentChanged(EnvironmentEvent<IVessel> event) {
		notifylisteners( new SituationEvent<IPhysical>( this, getReference(), null));
	}

	@Override
	public Collection<RadarData> getScan() {
		return super.getSituation().values();
	}

	@Override
	protected void onSetInput(IReferenceEnvironment<IVessel, IPhysical> input) {
		if( super.getInput() != null ) {
			if ( super.getInput().equals(input ))
				return;
			super.getInput().removeListener( e-> onNotifyEnvironmentChanged(e));
		}
		input.addListener(e-> onNotifyEnvironmentChanged(e));
	}


	@Override
	public IField getView() {
		return this.getInput().getField();
	}
}