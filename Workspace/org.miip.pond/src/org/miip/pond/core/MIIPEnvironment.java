package org.miip.pond.core;

import org.condast.commons.data.latlng.Field;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.miip.waterway.model.CentreShip;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.model.eco.Bank;
import org.miip.waterway.sa.SituationalAwareness;

public class MIIPEnvironment implements IMIIPEnvironment {

	public MIIPEnvironment() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isRunning() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isPaused() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void start() {
		// TODO Auto-generated method stub

	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub

	}

	@Override
	public void step() {
		// TODO Auto-generated method stub

	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setManual(boolean manual) {
		// TODO Auto-generated method stub

	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub

	}

	@Override
	public int getTimer() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setTimer(int timer) {
		// TODO Auto-generated method stub

	}

	@Override
	public CentreShip getShip() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Waterway getWaterway() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public SituationalAwareness getSituationalAwareness() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBankWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isInitialsed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Bank[] getBanks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void addListener(IEnvironmentListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void removeListener(IEnvironmentListener listener) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Field getField() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return null;
	}

}
