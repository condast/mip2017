package test.miip.pond.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.env.IEnvironmentListener.EventTypes;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.plane.Field;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Vessel;
import org.miip.waterway.model.def.MapLocation;
import org.miip.waterway.model.eco.PondSituationalAwareness;

public class TestPondEnvironment implements IReferenceEnvironment<IVessel, IPhysical>{

	public static final int DEFAULT_START_ANGLE = 90;
	public static final int DEFAULT_OFFSET = 90;

	private int proceedCounter, countRef;
	private Field field;
	private Vessel reference;
	private List<IPhysical> others;
	private boolean active;
	
	private Collection<IEnvironmentListener<IVessel>> listeners;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private TestPondEnvironment pe;
	
	public TestPondEnvironment() {
		this.others = new ArrayList<IPhysical>();
		this.listeners = new ArrayList<>();
		pe = this; 
		this.clear();
	}

	@Override
	public void setEnabled(boolean enabled) {
		/* NOTHING */
	}

	@Override
	public void clear() {
		field = new Field( MapLocation.Location.RIJNHAVEN.toLatLng(), 100, 100);
	}


	public void clear( int refAngle, int otherAngle, int distance) {
		this.clear();
		double angle = Math.toRadians(refAngle);
		double bearing = Math.toRadians(( refAngle + 180  )%360);
		LatLng latlng = LatLngUtilsDegrees.extrapolate(field.getCentre(), angle, distance);
		String name = "Reference";
		reference = new Vessel( name.hashCode(), name, latlng, bearing, true);//bearing east, 10 km/h
		ISituationalAwareness<IPhysical, IVessel> sa = new PondSituationalAwareness( reference, field );
		sa.setInput(this);
		reference.init(sa);
		
		this.others.clear();
		angle = Math.toRadians( otherAngle);
		bearing = Math.toRadians(( otherAngle + 180  )%360);
		latlng = LatLngUtilsDegrees.extrapolate( field.getCentre(), angle, distance);
		name = "Other";
		IVessel other = new Vessel( name.hashCode(), name , latlng, bearing, false );//bearing south, 10 km/h
		sa = new PondSituationalAwareness( other, field );
		sa.setInput(this);
		other.init(sa);
		this.others.add(other);
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(this,  EventTypes.INITIALSED,  this.reference));
	}

	protected void proceed() {
		this.others.clear();
		this.proceedCounter++;
		int countOther = proceedCounter%360;
		if( countOther == 0)
			countRef++;
		double angle = field.getAngle() + countRef;
		double bearing = (180+angle)%360;
		int half = ( field.getLength() > field.getWidth() )?  (int)field.getWidth()/2:  (int)field.getLength()/2;
		LatLng latlng = LatLngUtilsDegrees.extrapolate( field.getCentre(), bearing, half );
		if( !field.isInField(latlng, 1))
			logger.info("out of bounds");
		String name = "Reference";
		reference = new Vessel( name.hashCode(), name, latlng, angle, true);//bearing east, 10 km/h
		ISituationalAwareness<IPhysical, IVessel> sa = new PondSituationalAwareness( reference, field );
		sa.setInput(this);
		reference.init(sa);

		angle = field.getAngle() + countOther + DEFAULT_OFFSET ;
		bearing = (180+angle)%360;
		half = (int) (field.getWidth()/2);
		latlng = LatLngUtilsDegrees.extrapolate( field.getCentre(), bearing, half);
		if( !field.isInField(latlng, 1))
			logger.info("out of bounds");
		name = "Other";
		IVessel other = new Vessel( name.hashCode(), name, latlng, angle, false );//bearing south, 10 km/h
		sa = new PondSituationalAwareness( other, field );
		sa.setInput(this);
		other.init(sa);
		this.others.add(other);
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(this, EventTypes.PROCEED,  reference));
	}

	public IVessel getInhabitant() {
		return reference;
	}
	
	@Override
	public boolean isInitialised() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return active;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	public Collection<IPhysical> getOthers() {
		return this.others;
	}

	public Collection<IPhysical> getAll() {
		Collection<IPhysical> vessels = new ArrayList<IPhysical>();
		vessels.add(reference);
		vessels.addAll(this.others);
		return vessels;
	}

	public void addListener(IEnvironmentListener<IVessel> listener) {
		this.listeners.add(listener);
	}

	public void removeListener(IEnvironmentListener<IVessel> listener) {
		this.listeners.remove(listener);
	}

	protected void notifyEnvironmentChanged( EnvironmentEvent<IVessel> event ) {
		for( IEnvironmentListener<IVessel> listener: listeners )
			listener.notifyEnvironmentChanged(event);
	}

	public Field getField() {
		return field;
	}

	public String getName() {
		return field.getName();
	}

	public boolean execute( int time ) {
		reference.move(time);
		if( !pe.getField().isInField(reference.getLocation(), 1))
			proceed();
			//notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe, EventTypes.OUT_OF_BOUNDS, reference));
		for(IPhysical other: others ) {
			IVessel vessel = (IVessel) other;
			vessel.move(time);
			if( reference.isInCriticalDistance(vessel))
				notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe, EventTypes.COLLISION_DETECT, vessel));
			if( !pe.getField().isInField(vessel.getLocation(), 1)) {
				proceed();
			}
			double distance = LatLngUtils.getDistance(reference.getLocation(), other.getLocation());
			logger.info( "Distance: " + distance);
			if( distance < 5 ) {
				logger.severe( "Distance: " + distance);
				active = false;
			}
		}
		notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe));
		return active;
	}
}
