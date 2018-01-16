package org.miip.pond.core;

import java.util.Collection;

import org.condast.commons.data.latlng.Field;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IReferenceEnvironment;
import org.miip.waterway.sa.AbstractSituationalAwareness;

public class PondSituationalAwareness extends AbstractSituationalAwareness<IReferenceEnvironment<IVessel>, IVessel> {

	private IVessel vessel;
	
	public PondSituationalAwareness() {
	}

	public IVessel getReference() {
		return vessel;
	}

	@Override
	public Field getField() {
		return getInput().getField();
	}
	
	@Override
	public Collection<IVessel> getRadar() {
		return super.getInput().getOthers();
	}

	@Override
	protected void onSetInput(IReferenceEnvironment<IVessel> input) {
		if( input == null )
			return;
		this.vessel = input.getInhabitant();
	}
}