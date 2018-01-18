package org.miip.waterway.model;

import java.util.Date;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.Vector;

public class CentreShip extends Ship {

	public enum Controls{
		UP,
		DOWN,
		LEFT,
		RIGHT
	}
	
	private Vector<Integer> offset;

	public CentreShip(String id, LatLng position, float speed, Bearing bearing) {
		super(id, position, speed, bearing);
		offset = new Vector<Integer>(Bearing.EAST.getAngle(), 0 );
	}

	public CentreShip(String id, Date currentTime, float speed, LatLng position) {
		super(id, currentTime, speed, position);
	}

	public CentreShip(String id, Date currentTime, float speed, int length, LatLng position, Bearing bearing) {
		super(id, currentTime, speed, length, position, bearing);
	}

	
	public Vector<Integer> getOffset() {
		return offset;
	}

	@Override
	public LatLng getLocation() {
		if(( offset == null ) || ( Math.abs( offset.getValue() ) < Double.MIN_VALUE ))
			return super.getLocation();
		if( offset.getKey() == 0 )
			offset = null;
		LatLng correction = super.getLocation();//LatLngUtils.extrapolate( super.getLatLng(), 0, 200);
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

		offset = new Vector<Integer>(newBearing, newDistance);						
	}
	
	@Override
	public LatLng sail(Date newTime) {
		if( offset != null ){
			int bearing = offset.getKey();
			int angle = ( bearing < 0 )? offset.getKey()+1: ( bearing > 0 )? offset.getKey()-1: bearing; 

			int distance = (int)(( offset.getValue() > 0 )? offset.getValue() - 1: ( offset.getValue() < 0 )? offset.getValue() + 1:0 );
			offset = new Vector<Integer>( angle, distance );
		}
		
		return super.sail(newTime);
	}	
}
