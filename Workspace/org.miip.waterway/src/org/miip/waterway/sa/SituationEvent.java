package org.miip.waterway.sa;

import java.util.EventObject;

public class SituationEvent<V extends Object> extends EventObject {
	private static final long serialVersionUID = 1L;
	private boolean hit;
	private int angle;
	private long distance;

	public SituationEvent( V vessel ) {
		this( vessel, 0, 0, false); 
	}

	public SituationEvent( V vessel, int angle, long distance ) {
		this( vessel, angle, distance, false); 
	}
	
	public SituationEvent( V vessel, int angle, long distance, boolean hit) {
		super(vessel );
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
