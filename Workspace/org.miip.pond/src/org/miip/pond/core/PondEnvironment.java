package org.miip.pond.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.env.IEnvironmentListener.EventTypes;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.latlng.Waypoint;
import org.condast.commons.data.plane.Field;
import org.condast.commons.data.plane.FieldData;
import org.condast.commons.data.plane.IField;
import org.condast.commons.thread.AbstractExecuteThread;
import org.miip.waterway.ca.DefaultCollisionAvoidance;
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
	
	private IField field;
	private Vessel reference;
	private List<IPhysical> others;
	private int time;
	private int iteration;
	private double angle;
	
	private Collection<IEnvironmentListener<IVessel>> listeners;
	
	private PondEnvironment pe;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public PondEnvironment() {
		this.others = new ArrayList<IPhysical>();
		this.listeners = new ArrayList<IEnvironmentListener<IVessel>>();
		pe = this; 
		this.time = DEFAULT_TIME_SECOND;
		this.iteration = 0;
		this.angle = 0;
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
		FieldData fieldData = field.toFieldData(FieldData.DEFAULT_ZOOM );
		this.iteration = 0;
		
		//Vessels have situational awareness and collision avoidance
		LatLng latlng = field.transform(0, field.getWidth()/2);
		String name = "Reference";
		reference = new Vessel( name.hashCode(), name, latlng, 90, true );//bearing east, 10 km/h
		ICollisionAvoidance<IVessel, VesselRadarData> ca =  new DefaultCollisionAvoidance( reference, fieldData );
		reference.setCollisionAvoidance(ca);
		ISituationalAwareness<VesselRadarData> sa = new PondSituationalAwareness( reference, field );
		sa.setRange(30);
		//ca.addSituationalAwareness(sa);
		LatLng destination = Field.clip( field, reference.getLocation(), 90 );
		reference.addWayPoint( new Waypoint( destination ));
		
		this.others.clear();
		latlng = field.transform( field.getLength()/2, 0);
		name = "Other";
		IVessel other = new Vessel( name.hashCode(), name , latlng, 180, false );//bearing south, 10 km/h
		ca =  new DefaultCollisionAvoidance( other, fieldData );
		other.setCollisionAvoidance(ca);
		sa = new PondSituationalAwareness( other, field );
		sa.setRange(30);
		//ca.addSituationalAwareness(sa);
		destination = Field.clip( field, other.getLocation(), 180 );
		other.addWayPoint( new Waypoint( destination ));
		
		this.others.add(other);
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(this, EventTypes.INITIALSED,  reference));
	}

	@Override
	public boolean onInitialise() {
		clear();
		return true;
	}

	protected void proceed() {
		this.others.clear();
		angle = LatLngUtilsDegrees.mod( this.iteration + 90);
		double heading =  (int) LatLngUtilsDegrees.opposite(angle);
		int half = (int) (Math.max( field.getLength(), field.getWidth() )/2);
		LatLng latlng = LatLngUtilsDegrees.extrapolate( field.getCentre(), heading, half );
		if( !field.isInField(latlng, 1))
			logger.info("out of bounds");
		String name = "Reference";
		reference = new Vessel( name.hashCode(), name, latlng, angle, true);//bearing east, 10 km/h
		FieldData fieldData = field.toFieldData(FieldData.DEFAULT_ZOOM );
		ICollisionAvoidance<IVessel, VesselRadarData> ca =  new DefaultCollisionAvoidance( reference, fieldData );
		reference.setCollisionAvoidance(ca);
		ISituationalAwareness<VesselRadarData> sa = new PondSituationalAwareness( reference, field );
		//ca.addSituationalAwareness(sa);
		LatLng destination = Field.clip( field, reference.getLocation(), angle );
		reference.addWayPoint( new Waypoint( destination ));

		angle = (int) LatLngUtilsDegrees.mod(angle - 90);
		heading = LatLngUtilsDegrees.opposite( angle );
		latlng = LatLngUtilsDegrees.extrapolate( field.getCentre(), angle, half);
		if( !field.isInField(latlng, 1))
			logger.info("out of bounds");
		name = "Other";
		IVessel other = new Vessel( name.hashCode(), name, latlng, heading, false);
		ca = reference.getCollisionAvoidance();
		other.setCollisionAvoidance(ca);
		sa = new PondSituationalAwareness( other, field );
		//ca.addSituationalAwareness(sa);
		destination = LatLngUtilsDegrees.extrapolate( other.getLocation(), 190, half);
		other.addWayPoint( new Waypoint( destination ));

		this.others.add(other);
		iteration++;
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(this, EventTypes.PROCEED,  reference));
	}

	
	@Override
	public int getIteration() {
		return iteration;
	}

	@Override
	public void setIteration(int iteration) {
		this.iteration = iteration;
	}

	@Override
	public double getAngle() {
		return angle;
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
		if( reference.destinationReached()) {
			proceed();
		}
		for(IPhysical other: others ) {
			Vessel vessel = (Vessel) other;
			vessel.move(time);
			if( reference.isInCriticalDistance(other))
				notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe, EventTypes.COLLISION_DETECT, vessel));
			if( vessel.destinationReached()) {
				proceed();
				break;
			}
		}
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe, EventTypes.CHANGED, reference));
		super.sleep(time);
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
	public int getBankWidth() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Bank[] getBanks() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ISituationalAwareness<VesselRadarData> getSituationalAwareness() {
		// TODO Auto-generated method stub
		return null;
	}
}
