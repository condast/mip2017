package org.miip.waterway.internal.model;

import java.util.Date;

import org.condast.commons.lnglat.LngLat;
import org.condast.commons.lnglat.Motion;
import org.condast.wph.core.definition.IModel;

public class Ship extends AbstractModel<IModel.ModelTypes>{
	private static final int TO_HOURS = 60*60*1000;
	
	private Date currentTime;
	
	private float speed;//-20 - 60 km/hour
	private float bearing; //0-360
		
	public Ship( String id, Date currentTime, float speed, LngLat position) {
		super( id, IModel.ModelTypes.SHIP, position );
		this.currentTime = currentTime;
		this.speed = speed;
		this.bearing = 45;//due east
	}
	
	public float getSpeed() {
		return speed;
	}

	public float getBearing() {
		return bearing;
	}


	public void sail( Date newTime ){
		long interval = newTime.getTime() - currentTime.getTime();
		long distance = ( long )( interval * ( speed/ TO_HOURS ));
		LngLat position = Motion.extrapolate( super.getLnglat(), bearing, distance);
		super.setLnglat(position);
	}
}
