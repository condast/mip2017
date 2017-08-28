package org.miip.waterway.model;

import java.util.Date;

import org.condast.commons.latlng.LatLng;
import org.condast.commons.latlng.Vector;

public class CentreShip extends Ship {

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
	public LatLng sail(Date newTime) {
		Bearing bearing = super.getBearing();
		int angle = bearing.getAngle() + (int)(1 + 10 * Math.random());
		int distance = ( int )( 5 * Math.random() );
		offset = new Vector<Integer>( angle, distance );
		return super.sail(newTime);
	}	
}
