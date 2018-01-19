package org.miip.waterway.environment;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.thread.IExecuteThread;
import org.condast.symbiotic.core.environment.IEnvironmentListener;

public interface IEnvironment<D extends Object> extends IExecuteThread {

	//void setManual(boolean manual);

	void clear();
	
	public String getName();

	boolean isInitialsed();

	int getTimer();

	void setTimer(int timer);

	void addListener(IEnvironmentListener<D> listener);

	void removeListener(IEnvironmentListener<D> listener);

	Field getField();
}