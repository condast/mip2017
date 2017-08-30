package org.miip.waterway.sa;

import java.util.EventObject;

import org.miip.waterway.model.Ship;

public class ShipEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	private boolean hit;
	public ShipEvent( Ship ship ) {
		this( ship, false); 
	}
	
	public ShipEvent( Ship ship, boolean hit) {
		super(ship );
		this.hit = hit;
	}

	public boolean isHit() {
		return hit;
	}
}
