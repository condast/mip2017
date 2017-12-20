package org.miip.waterway.sa;

import java.util.EventObject;

import org.miip.waterway.model.Ship;

public class ShipEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private boolean hit;
	private int angle;
	private long distance;
	
	public ShipEvent( Ship ship, int angle, long distance ) {
		this( ship, angle, distance, false); 
	}
	
	public ShipEvent( Ship ship, int angle, long distance, boolean hit) {
		super(ship );
		this.angle = angle;
		this.distance = distance;
		this.hit = hit;
	}

	public int getAngle() {
		return angle;
	}

	public long getDistance() {
		return distance;
	}

	public boolean isHit() {
		return hit;
	}
}
