package org.miip.waterway.model.eco;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.AbstractSituationalAwareness;
import org.condast.commons.autonomy.sa.SituationEvent;
import org.condast.commons.autonomy.sa.radar.IDataEntry;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.data.plane.IField;
import org.miip.waterway.model.IVessel;

public class PondSituationalAwareness extends AbstractSituationalAwareness<VesselRadarData> {

	private IVessel owner;
	
	private IReferenceEnvironment<IVessel, IPhysical> input;
	
	public PondSituationalAwareness( IVessel owner, IField field) {
		super(ICollisionAvoidance.DefaultSituationalAwareness.VESSEL_RADAR.toString(), IDataEntry.DefaultDimensions.VESSEL_RADAR_DATA.getIndex(), field );
		this.owner = owner;
		//super.setCriticalDistance( vessel.getMinTurnDistance());
	}

	private void onNotifyEnvironmentChanged(EnvironmentEvent<IVessel> event) {
		notifylisteners( new SituationEvent<VesselRadarData>( this));
	}

	@Override
	public IPhysical[] getScan() {
		//Collection<VesselRadarData> results = super.getSituation().values();
		return null;//results.toArray( new IRadarData[ results.size()]);
	}

	protected void onSetInput(IReferenceEnvironment<IVessel, IPhysical> input) {
		if( input != null ) {
			if ( input.equals(input ))
				return;
			input.removeListener( e-> onNotifyEnvironmentChanged(e));
		}
		input.addListener(e-> onNotifyEnvironmentChanged(e));
	}
}