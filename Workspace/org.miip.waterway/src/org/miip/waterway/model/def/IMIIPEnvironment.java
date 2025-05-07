package org.miip.waterway.model.def;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.data.plane.IField;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.eco.Bank;

public interface IMIIPEnvironment extends IReferenceEnvironment<IVessel, IPhysical> {

	@Override
	public IField getField();

	void setManual(boolean manual);

	Waterway getWaterway();

	int getBankWidth();

	Bank[] getBanks();

	int getIteration();

	public void setIteration(int selection);

	/**
	 * The current angle of the test
	 * @return
	 */
	double getAngle();
	
	public void update();
}