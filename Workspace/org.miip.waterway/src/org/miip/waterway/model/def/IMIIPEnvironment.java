package org.miip.waterway.model.def;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.eco.Bank;
import org.miip.waterway.sa.SituationalAwareness;

public interface IMIIPEnvironment extends IReferenceEnvironment<IVessel, IPhysical> {

	void setManual(boolean manual);

	Waterway getWaterway();

	SituationalAwareness getSituationalAwareness();

	int getBankWidth();

	Bank[] getBanks();
}