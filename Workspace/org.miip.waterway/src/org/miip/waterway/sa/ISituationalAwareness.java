package org.miip.waterway.sa;

import java.util.Map;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Vector;

public interface ISituationalAwareness {

	int MAX_DEGREES = 360;
	int STEPS_512 = 512;//a more refined alternative to degrees for quick mathematics

	void setRange(int range);

	int getSensitivity();

	void setSensitivity(int sensitivity);

	int getSteps();

	void addlistener(IShipMovedListener listener);

	void removelistener(IShipMovedListener listener);

	Map<Integer, Double> getRadar();

	int getRange();

	SequentialBinaryTreeSet<Vector<Integer>> getBinaryView();

}