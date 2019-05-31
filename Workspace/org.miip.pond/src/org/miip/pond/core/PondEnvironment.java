package org.miip.pond.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.condast.commons.autonomy.ca.AbstractCollisionAvoidance;
import org.condast.commons.autonomy.ca.FlankCAStrategy;
import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.env.IEnvironmentListener.EventTypes;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.IField;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.thread.AbstractExecuteThread;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Vessel;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.model.def.MapLocation;
import org.miip.waterway.model.eco.Bank;
import org.miip.waterway.model.eco.PondSituationalAwareness;

public class PondEnvironment extends AbstractExecuteThread implements IMIIPEnvironment {

	public static final int DEFAULT_START_ANGLE = 90;
	public static final int DEFAULT_OFFSET = 90;
	public static final int DEFAULT_TIME_SECOND = 1000;
	
	private int proceedCounter, countRef;
	private IField field;
	private Vessel reference;
	private List<IPhysical> others;
	private int time;
	
	private Collection<IEnvironmentListener<IVessel>> listeners;
	
	private PondEnvironment pe;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public PondEnvironment() {
		this.others = new ArrayList<IPhysical>();
		this.listeners = new ArrayList<IEnvironmentListener<IVessel>>();
		pe = this; 
		this.time = DEFAULT_TIME_SECOND;
		this.proceedCounter = 0;
		this.clear();
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(!enabled )
			this.stop();
		super.setEnabled(enabled);
	}

	@Override
	public void clear() {
		field = new Field( MapLocation.Location.RIJNHAVEN.toLatLng(), 100, 100, 0);
		this.proceedCounter = 0;
		this.countRef = 0;
		
		//Vessels have situational awareness and collision avoidance
		LatLng latlng = field.transform(0, field.getWidth()/2);
		reference = new Vessel( "Reference", latlng, 90, 10);//bearing east, 10 km/h
		ISituationalAwareness<IVessel, IPhysical> sa = new PondSituationalAwareness( reference, field );
		sa.setInput(this);
		ICollisionAvoidance<IVessel, IPhysical> ca = new DefaultCollisionAvoidance( reference, sa); 
		reference.init(sa, ca);
		
		this.others.clear();
		latlng = field.transform( field.getLength()/2,0);
		IVessel other = new Vessel( "Other", latlng, 0, 10 );//bearing south, 10 km/h
		sa = new PondSituationalAwareness( other, field );
		sa.setInput(this);
		ca = new DefaultCollisionAvoidance( other, sa); 
		other.init(sa, ca);
		this.others.add(other);
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(this, EventTypes.INITIALSED,  reference));
	}

	@Override
	public boolean onInitialise() {
		clear();
		return true;
	}

	protected void proceed() {
		this.proceedCounter++;
		int countOther = (int) LatLngUtilsDegrees.mod( proceedCounter );
		if( countOther == 0)
			countRef++;
		double angle = field.getAngle() + countRef;
		double heading = LatLngUtilsDegrees.opposite( angle );
		int half = ( field.getLength() > field.getWidth() )?  (int)field.getWidth()/2:  (int)field.getLength()/2;
		LatLng latlng = LatLngUtilsDegrees.extrapolate( field.getCentre(), heading, half );
		if( !field.isInField(latlng, 1))
			logger.info("out of bounds");

		angle = field.getAngle() + countOther;
		heading = LatLngUtilsDegrees.opposite(angle);
		half = (int) (field.getWidth()/2);
		latlng = LatLngUtilsDegrees.extrapolate( field.getCentre(), heading, half);
		if( !field.isInField(latlng, 1))
			logger.info("out of bounds");
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(this, EventTypes.PROCEED,  reference));
	}

	@Override
	public IVessel getInhabitant() {
		return reference;
	}
	
	@Override
	public Collection<IPhysical> getOthers() {
		return this.others;
	}

	public Collection<IPhysical> getAll() {
		Collection<IPhysical> vessels = new ArrayList<IPhysical>();
		vessels.add(reference);
		vessels.addAll(this.others);
		return vessels;
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
	public boolean isInitialised() {
		return true;
	}

	@Override
	public boolean isActive() {
		return super.isRunning();
	}

	@Override
	public void addListener(IEnvironmentListener<IVessel> listener) {
		this.listeners.add(listener);
	}

	@Override
	public void removeListener(IEnvironmentListener<IVessel> listener) {
		this.listeners.remove(listener);
	}
	
	protected void notifyEnvironmentChanged( EnvironmentEvent<IVessel> event ) {
		for( IEnvironmentListener<IVessel> listener: listeners )
			listener.notifyEnvironmentChanged(event);
	}

	public IField getField() {
		return field;
	}

	@Override
	public String getName() {
		return field.getName();
	}

	@Override
	public void onExecute() {
		reference.move(time);
		if( !pe.getField().isInField(reference.getLocation(), 1)) {
			proceed();
		}
		for(IPhysical other: others ) {
			IVessel vessel = (IVessel) other;
			vessel.move(time);
			if( reference.isInCriticalDistance(other))
				notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe, EventTypes.COLLISION_DETECT, vessel));
			if( !pe.getField().isInField(vessel.getLocation(), 1)) {
				proceed();
			}
		}
		super.sleep(time);
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe));
	}

	private class DefaultCollisionAvoidance extends AbstractCollisionAvoidance<IPhysical, IVessel>{

		public DefaultCollisionAvoidance( IVessel vessel, ISituationalAwareness<IVessel, IPhysical> sa ){
			super( field, sa, true);
			if( StringUtils.isEmpty( vessel.getName()))
				System.out.println("STOP!!!!");
			super.addStrategy( new FlankCAStrategy<IPhysical, IVessel>( vessel, this ));
			setActive(!( vessel.getName().toLowerCase().equals("other")));
		}
		
		/**
		 * Get the critical distance for passage 
		 */
		@Override
		public double getCriticalDistance() {
			IVessel vessel = (IVessel) getReference(); 
			return vessel.getMinTurnDistance();
		}
	}

	@Override
	public void setManual(boolean manual) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Waterway getWaterway() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISituationalAwareness<IVessel, IPhysical> getSituationalAwareness() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getBankWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Bank[] getBanks() {
		// TODO Auto-generated method stub
		return null;
	}
}
