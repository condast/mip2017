package org.miip.waterway.internal.model;

import org.condast.commons.lnglat.LngLat;

public class Ship {

	private float speed;//-20 - 60 km/hour
	private float brearing; //0-360
	
	private LngLat position;
	
	public Ship( LngLat position) {
		this.position = position;
	}

}
