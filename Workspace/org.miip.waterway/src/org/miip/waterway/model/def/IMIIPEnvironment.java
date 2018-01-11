package org.miip.waterway.model.def;

import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.eco.Bank;
import org.miip.waterway.sa.SituationalAwareness;

public interface IMIIPEnvironment extends ISimpleEnvironment {

	void setManual(boolean manual);

	Waterway getWaterway();

	SituationalAwareness getSituationalAwareness();

	int getBankWidth();

	Bank[] getBanks();
}