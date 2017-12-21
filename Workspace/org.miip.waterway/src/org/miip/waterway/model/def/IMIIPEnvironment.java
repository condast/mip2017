package org.miip.waterway.model.def;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.thread.IExecuteThread;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.miip.waterway.model.CentreShip;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.eco.Bank;
import org.miip.waterway.sa.SituationalAwareness;

public interface IMIIPEnvironment extends IExecuteThread {

	void setManual(boolean manual);

	void clear();

	int getTimer();

	void setTimer(int timer);

	CentreShip getShip();

	Waterway getWaterway();

	SituationalAwareness getSituationalAwareness();

	void addListener(IEnvironmentListener listener);

	void removeListener(IEnvironmentListener listener);

	int getBankWidth();

	boolean isInitialsed();

	Field getField();

	Bank[] getBanks();
}