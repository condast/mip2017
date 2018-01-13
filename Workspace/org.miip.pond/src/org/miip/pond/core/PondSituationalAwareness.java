package org.miip.pond.core;

import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IInhabitedEnvironment;
import org.miip.waterway.sa.AbstractSituationalAwareness;

public class PondSituationalAwareness extends AbstractSituationalAwareness<IInhabitedEnvironment<IVessel[]>> {

	private IVessel vessel;
	private IInhabitedEnvironment<IVessel[]> input;
	
	public PondSituationalAwareness() {
	}

	@Override
	protected long onSetInput(IInhabitedEnvironment<IVessel[]> input, int step) {
		this.vessel = input.getInhabitant()[0];
		this.input = input;
		return 0;
	}

}
