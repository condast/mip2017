package org.miip.waterway.model;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.autonomy.ca.AbstractCollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidanceStrategy;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.latlng.LatLngVector;
import org.condast.commons.data.plane.IField;
import org.condast.commons.strings.StringUtils;

public class CentreShip extends Ship {

	public enum Controls{
		UP,
		DOWN,
		LEFT,
		RIGHT
	}
	
	private LatLngVector<Integer> offset;
	
	private IField field;

	public CentreShip(String id, LatLng position, float speed, Heading bearing) {
		super(id, position, speed, bearing);
		offset = new LatLngVector<Integer>(Heading.EAST.getAngle(), 0 );
	}

	public CentreShip(String id, LatLng position, float speed) {
		super(id, speed, position);
	}

	public CentreShip(String id, LatLng position, float speed, int length, Heading bearing) {
		super(id, speed, length, position, bearing.getAngle());
	}

	
	public LatLngVector<Integer> getOffset() {
		return offset;
	}

	@Override
	public LatLng getLocation() {
		if(( offset == null ) || ( Math.abs( offset.getValue() ) < Double.MIN_VALUE ))
			return super.getLocation();
		if( offset.getKey() == 0 )
			offset = null;
		LatLng correction = LatLngUtilsDegrees.extrapolate( super.getLocation(), offset.getKey(), offset.getValue());
		//logger.fine( "New position: " + correction + " from [" + offset.getKey() + ", " + offset.getValue() + "]");
		return correction;
	}

	public void setControl( Controls control ){
		if( control == null )
			return;
		int bearing = 0;
		int distance = 0;
		switch( control ){
		case UP:
			bearing = - 3;
			break;
		case DOWN:
			bearing = + 3 ;
			break;
		case LEFT:
			distance = -3;
			break;
		case RIGHT:
			distance = +3;
			break;
		default: 
			break;
		}
		int newBearing = ( offset == null )? bearing: (int) (offset.getKey() + bearing);
		if( newBearing > 40 )
			newBearing = 40;
		if( newBearing <= -40 )
			newBearing = -40;

		int newDistance = ( offset == null )? distance: (int) (offset.getValue() + distance);
		if( newDistance > 40 )
			newDistance = 40;
		if( newDistance <= -40 )
			newDistance = -40;

		offset = new LatLngVector<Integer>(newBearing, newDistance);						
	}

	@Override
	public LatLng move( long interval) {
		if( offset != null ){
			int bearing = offset.getKey();
			int angle = ( bearing < 0 )? offset.getKey()+1: ( bearing > 0 )? offset.getKey()-1: bearing; 

			int distance = (int)(( offset.getValue() > 0 )? offset.getValue() - 1: ( offset.getValue() < 0 )? offset.getValue() + 1:0 );
			offset = new LatLngVector<Integer>( angle, distance );
		}
		
		return super.move( interval);
	}
	
	private class DefaultCollisionAvoidance extends AbstractCollisionAvoidance<IPhysical, IVessel>{

		public DefaultCollisionAvoidance( IVessel vessel, ISituationalAwareness<IVessel, IPhysical> sa ){
			super( field, sa, true);
			if( StringUtils.isEmpty( vessel.getName()))
				System.out.println("STOP!!!!");
			addStrategy( ICollisionAvoidanceStrategy.DefaultStrategies.FLANK_STRATEGY.name());
			setActive(!( vessel.getName().toLowerCase().equals("other")));
		}
		
		@Override
		protected void clearStrategies() {
			super.clearStrategies();
		}

		protected boolean addStrategy( String strategyName ) {
			ICollisionAvoidanceStrategy<IPhysical, IVessel> strategy = super.getDefaultStrategy(strategyName);
			if( strategy == null )
				return false;
			return super.addStrategy(strategy);
		}

		protected boolean removeStrategy(String strategyName ) {
			Collection<ICollisionAvoidanceStrategy<IPhysical, IVessel>> temp =
					new ArrayList<ICollisionAvoidanceStrategy<IPhysical, IVessel>>(super.getStrategies());
			boolean result = false;
			for( ICollisionAvoidanceStrategy<IPhysical, IVessel> strategy: temp) {
				if( strategy.getName().equals(strategyName))
					result = super.removeStrategy(strategy);
			}
			return result;
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
}
