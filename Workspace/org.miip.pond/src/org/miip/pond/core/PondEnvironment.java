package org.miip.pond.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.thread.AbstractExecuteThread;
import org.condast.commons.thread.IExecuteThread;
import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Vessel;
import org.miip.waterway.model.def.IInhabitedEnvironment;
import org.miip.waterway.model.def.MapLocation;
import org.miip.waterway.sa.SituationalAwareness;

public class PondEnvironment implements IInhabitedEnvironment<IVessel[]> {

	private Field field;
	private IVessel vessel;
	private List<IVessel> others;
	
	private Collection<IEnvironmentListener> listeners;
	
	private IExecuteThread thread = new ExecuteThread();
	private PondEnvironment pe;
	
	public PondEnvironment() {
		field = new Field( MapLocation.Location.RIJNHAVEN.toLatLng(), 100, 100);
		vessel = new Vessel( field.transform(0, field.getWidth()/2), 90, 10);//bearing east, 10 km/h
		IVessel other = new Vessel( field.transform(field.getLength()/2, 0), 180, 10);//bearing south, 10 km/h
		this.others = new ArrayList<IVessel>();
		this.others.add( other );
		this.listeners = new ArrayList<IEnvironmentListener>();
		pe = this; 
	}

	@Override
	public IVessel[] getInhabitant() {
		Collection<IVessel> vessels = new ArrayList<IVessel>();
		vessels.add( vessel );
		vessels.addAll( this.others);
		return vessels.toArray( new IVessel[vessels.size()]);
	}

	@Override
	public boolean isRunning() {
		return thread.isRunning();
	}

	@Override
	public boolean isPaused() {
		return isPaused();
	}

	@Override
	public void start() {
		thread.start();
	}

	@Override
	public void pause() {
		thread.pause();
	}

	@Override
	public void step() {
		thread.step();
	}

	@Override
	public void stop() {
		thread.stop();
	}

	@Override
	public void clear() {
		field = new Field( MapLocation.Location.RIJNHAVEN.toLatLng(), 100, 100);
		vessel = new Vessel( field.transform(0, field.getWidth()/2), 90, 10);//bearing east, 10 km/h
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
	public boolean isInitialsed() {
		return true;
	}

	@Override
	public void addListener(IEnvironmentListener listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(IEnvironmentListener listener) {
		this.listeners.remove(listener);
	}
	
	protected void notifyEnvironmentChanged( EnvironmentEvent event ) {
		for( IEnvironmentListener listener: listeners )
			listener.notifyEnvironmentChanged(event);
	}

	@Override
	public Field getField() {
		return field;
	}

	@Override
	public String getName() {
		return field.getName();
	}

	private class ExecuteThread extends AbstractExecuteThread{

		private int time = 1000;//1 sec
		
		@Override
		public boolean onInitialise() {
			clear();
			return true;
		}

		@Override
		public void onExecute() {
			vessel.sail(time);
			for(IVessel other: others )
				other.sail(time);
			super.sleep(time);
			notifyEnvironmentChanged( new EnvironmentEvent(pe));
		}
		
	}
}
