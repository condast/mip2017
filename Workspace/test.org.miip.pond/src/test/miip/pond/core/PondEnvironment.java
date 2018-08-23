package test.miip.pond.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.logging.Logger;

import org.condast.commons.autonomy.ca.AbstractCollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.env.IEnvironmentListener.EventTypes;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Vessel;
import org.miip.waterway.model.def.MapLocation;
import org.miip.waterway.model.eco.PondSituationalAwareness;

public class PondEnvironment implements IReferenceEnvironment<IVessel, IPhysical>{

	private Field field;
	private IVessel reference;
	private List<IPhysical> others;
	private boolean active;
	
	private Collection<IEnvironmentListener<IVessel>> listeners;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	private PondEnvironment pe;
	
	public PondEnvironment() {
		this.others = new ArrayList<IPhysical>();
		this.listeners = new ArrayList<>();
		pe = this; 
		this.clear();
	}

	public void clear() {
		field = new Field( MapLocation.Location.RIJNHAVEN.toLatLng(), 100, 100);
		LatLng latlng = field.transform(0, field.getWidth()/2);
		reference = new Vessel( "Reference", latlng, 90, 10);//bearing east, 10 km/h
		ICollisionAvoidance<IVessel, IPhysical> ca = new DefaultCollisionAvoidance( reference); 
		reference.init(ca);
		
		this.others.clear();
		latlng = field.transform( field.getWidth()/2 - 10,0);
		IVessel other = new Vessel( "Other", latlng, 180, 10 );//bearing south, 10 km/h
		ca = new DefaultCollisionAvoidance( other); 
		other.init(ca);
		this.others.add(other);
	}

	public IVessel getInhabitant() {
		return reference;
	}
	
	@Override
	public boolean isInitialsed() {
		return true;
	}

	@Override
	public boolean isActive() {
		return active;
	}

	@Override
	public void setActive(boolean choice) {
		this.active = choice;
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

	private class DefaultCollisionAvoidance extends AbstractCollisionAvoidance<IPhysical, IVessel>{

		public DefaultCollisionAvoidance( IVessel vessel ) {
			super( new PondSituationalAwareness( vessel ), true);
			PondSituationalAwareness psa = (PondSituationalAwareness) super.getSituationalAwareness();
			psa.setInput( pe);
			setActive(!( vessel.getName().toLowerCase().equals("other")));
		}		
	}

	public boolean execute( int time ) {
		reference.move(time);
		if( !pe.getField().isInField(reference.getLocation(), 1))
			notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe, EventTypes.OUT_OF_BOUNDS, reference));
		for(IPhysical other: others ) {
			IVessel vessel = (IVessel) other;
			vessel.move(time);
			if( !pe.getField().isInField(vessel.getLocation(), 1))
				notifyEnvironmentChanged( new EnvironmentEvent<IVessel>(pe, EventTypes.OUT_OF_BOUNDS, vessel));
			double distance = LatLngUtils.distance(reference.getLocation(), other.getLocation());
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
