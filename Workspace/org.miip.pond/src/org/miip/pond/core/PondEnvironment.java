package org.miip.pond.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.condast.commons.autonomy.ca.AbstractCollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.env.IEnvironmentListener.EventTypes;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.thread.AbstractExecuteThread;
import org.condast.commons.thread.IExecuteThread;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Vessel;
import org.miip.waterway.model.def.MapLocation;
import org.miip.waterway.model.eco.PondSituationalAwareness;

public class PondEnvironment implements IReferenceEnvironment<IPhysical> {

	private Field field;
	private IVessel reference;
	private List<IPhysical> others;
	
	private Collection<IEnvironmentListener<IPhysical>> listeners;
	
	private IExecuteThread thread = new ExecuteThread();
	private PondEnvironment pe;
	
	public PondEnvironment() {
		this.others = new ArrayList<IPhysical>();
		this.listeners = new ArrayList<IEnvironmentListener<IPhysical>>();
		pe = this; 
		this.clear();
	}

	@Override
	public void clear() {
		field = new Field( MapLocation.Location.RIJNHAVEN.toLatLng(), 100, 100);
		LatLng latlng = field.transform(0, field.getWidth()/2);
		reference = new Vessel( "Reference", latlng, 90, 10);//bearing east, 10 km/h
		ICollisionAvoidance ca = new DefaultCollisionAvoidance( reference); 
		reference.setCollisionAvoidance(ca);
		
		this.others.clear();
		latlng = field.transform( field.getLength()/2 - 11,0);
		IVessel other = new Vessel( "Other", latlng, 180, 10 );//bearing south, 10 km/h
		ca = new DefaultCollisionAvoidance( other); 
		other.setCollisionAvoidance(ca);
		this.others.add(other);
	}

	@Override
	public IVessel getInhabitant() {
		return reference;
	}
	
	@Override
	public Collection<IPhysical> getOthers() {
		return this.others;
	}

	@Override
	public Collection<IPhysical> getAll() {
		Collection<IPhysical> vessels = new ArrayList<IPhysical>();
		vessels.add(reference);
		vessels.addAll(this.others);
		return vessels;
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
	public void addListener(IEnvironmentListener<IPhysical> listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(IEnvironmentListener<IPhysical> listener) {
		this.listeners.remove(listener);
	}
	
	protected void notifyEnvironmentChanged( EnvironmentEvent<IPhysical> event ) {
		for( IEnvironmentListener<IPhysical> listener: listeners )
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
			reference.move(time);
			if( !pe.getField().isInField(reference.getLocation(), 1))
				notifyEnvironmentChanged( new EnvironmentEvent<IPhysical>(pe, EventTypes.OUT_OF_BOUNDS, reference));
				
			for(IPhysical other: others ) {
				IVessel vessel = (IVessel) other;
				vessel.move(time);
				if( !pe.getField().isInField(vessel.getLocation(), 1))
					notifyEnvironmentChanged( new EnvironmentEvent<IPhysical>(pe, EventTypes.OUT_OF_BOUNDS, vessel));
			}
			super.sleep(time);
			notifyEnvironmentChanged( new EnvironmentEvent<IPhysical>(pe));
		}	
	}
	
	private class DefaultCollisionAvoidance extends AbstractCollisionAvoidance{

		public DefaultCollisionAvoidance( IVessel vessel ) {
			super( new PondSituationalAwareness( vessel ), true);
			PondSituationalAwareness psa = (PondSituationalAwareness) super.getSituationalAwareness();
			psa.setInput( pe);
			setActive(!( vessel.getName().toLowerCase().equals("other")));
		}
		
		
		
	}

}
