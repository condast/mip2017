package org.miip.waterway.model.def;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.thread.IExecuteThread;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.miip.waterway.model.CentreShip;
import org.miip.waterway.sa.SituationalAwareness;

public interface ISimpleEnvironment extends IExecuteThread {

	//void setManual(boolean manual);

	void clear();
	
	public String getName();

	boolean isInitialsed();

	int getTimer();

	void setTimer(int timer);

	CentreShip getShip();

	SituationalAwareness getSituationalAwareness();

	void addListener(IEnvironmentListener listener);

	void removeListener(IEnvironmentListener listener);

	Field getField();
}