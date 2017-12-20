package org.miip.rwt.service;

import java.util.Map;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Vector;
import org.miip.waterway.sa.IShipMovedListener;
import org.miip.waterway.sa.ISituationalAwareness;
import org.osgi.service.component.annotations.Component;

@Component(
		name = "org.miip.rwt.service.service.sa.provider",
		immediate=true
)
public class SituationalAwarenesssProvider implements ISituationalAwareness{

	private Dispatcher dispatcher = Dispatcher.getInstance();


	@Override
	public void setRange(int range) {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		sa.setRange(range);
	}

	@Override
	public int getSensitivity() {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		return sa.getSensitivity();
	}

	@Override
	public void setSensitivity(int sensitivity) {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		sa.setSensitivity(sensitivity);
	}

	@Override
	public int getSteps() {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		return sa.getSteps();
	}

	@Override
	public void addlistener(IShipMovedListener listener) {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		sa.addlistener(listener);
	}

	@Override
	public void removelistener(IShipMovedListener listener) {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		sa.removelistener(listener);
	}

	@Override
	public Map<Integer, Double> getRadar() {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		return sa.getRadar();
	}

	@Override
	public int getRange() {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		return sa.getRange();
	}

	@Override
	public SequentialBinaryTreeSet<Vector<Integer>> getBinaryView() {
		ISituationalAwareness sa = dispatcher.getSituationalAwareness();
		return sa.getBinaryView();
	}
}
