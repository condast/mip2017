package org.miip.waterway.internal.model;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.latlng.LatLng;
import org.miip.waterway.model.Ship;

/**
 * The pathway is the neighbourhood for a ship. A pathway is
 * a possible future heading for a ship. Every ship
 * is able to spawn a number of pathways, which can experience stress with other
 * 
 * @author Kees
 *
 */
public class PathWay {

	private Ship ship;
	
	private Map<LatLng, Float> paths; 
	
	public PathWay( Ship ship ) {
		this.ship = ship;
		paths = new HashMap<LatLng, Float>();
	}

	
}
