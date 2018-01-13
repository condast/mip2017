package org.miip.waterway.sa;

import java.util.Map;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Vector;

public interface ISituationalAwareness<I extends Object> {

	int MAX_DEGREES = 360;
	int STEPS_512 = 512;//a more refined alternative to degrees for quick mathematics

	int getRange();

	void setRange(int range);

	int getSensitivity();

	void setSensitivity(int sensitivity);

	int getSteps();

	void addlistener(IShipMovedListener listener);

	void removelistener(IShipMovedListener listener);
	
	public I getInput();
	
	public void setInput( I input );

	Map<Integer, Double> getRadar();

	SequentialBinaryTreeSet<Vector<Integer>> getBinaryView();

}