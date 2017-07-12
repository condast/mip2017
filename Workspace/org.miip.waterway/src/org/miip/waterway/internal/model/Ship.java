package org.miip.waterway.internal.model;

import java.util.Calendar;
import java.util.Date;

import org.condast.commons.lnglat.LatLng;
import org.condast.commons.lnglat.LngLatUtils;
import org.miip.waterway.model.def.IModel;

public class Ship extends AbstractModel{
	private static final int TO_HOURS = 60*60;
	
	public enum Bearing{
		NORTH(0),
		EAST(90),
		SOUTH(180),
		WEST(270);
		
		private int degrees;
		
		private Bearing( int degress ){
			this.degrees = degress;
		}

		public int getDegrees() {
			return degrees;
		}
	}
	
	private Date currentTime;
	
	private float speed;//-20 - 60 km/hour
	private Bearing bearing; //0-360

	public Ship( String id, LatLng position, float speed, Bearing bearing) {
		this( id, Calendar.getInstance().getTime(), speed, position, bearing );
	}

	public Ship( String id, Date currentTime, float speed, LatLng position) {
		this( id, currentTime, speed, position, Bearing.EAST );
	}
	
	public Ship( String id, Date currentTime, float speed, LatLng position, Bearing bearing) {
		super( id, IModel.ModelTypes.SHIP, position );
		this.currentTime = currentTime;
		this.speed = speed;
		this.bearing = bearing;
	}
	
	public float getSpeed() {
		return speed;
	}

	public Bearing getBearing() {
		return bearing;
	}

	/**
	 * Plot the next location, based on speed and bearing
	 * @param next
	 * @return
	 */
	public Location plotNext( Date next ){
		long interval = next.getTime() - this.currentTime.getTime();//msec
		double distance = this.speed * interval / TO_HOURS;
		double radian = Math.toRadians( this.bearing.getDegrees() );
		double x = distance * Math.sin( radian );
		double y = distance * Math.cos( radian );
		return new Location((float) x, (float)y );
	}

	public LatLng sail( Date newTime ){
		float interval = newTime.getTime() - currentTime.getTime();
		float distance = interval * speed/ TO_HOURS;
		LatLng position = LngLatUtils.extrapolate( super.getLnglat(), bearing.getDegrees(), distance);
		super.setLnglat(position);
		this.currentTime = newTime;
		return position;
	}
	
	/**
	 * Create a new ship with random values
	 * @param lnglat
	 * @param name
	 * @return
	 */
	public static Ship createShip( LatLng lnglat, String name ){
		double speed = 10 + ( 60 * Math.random());
		Bearing bearing = ( Math.random() < 0.5f)? Bearing.EAST: Bearing.WEST;
		return new Ship( name, lnglat, (float) speed, bearing);
	}
}
