package org.miip.waterway.model;

import java.util.Calendar;
import java.util.Date;

import org.condast.commons.latlng.LatLng;
import org.condast.commons.latlng.LatLngUtils;
import org.miip.waterway.internal.model.AbstractModel;
import org.miip.waterway.model.def.IModel;

public class Ship extends AbstractModel{
	
	private static final int DEFAULT_LENGTH = 20;//m
	
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

		public int getAngle() {
			return degrees;
		}
	}
	
	private Date currentTime;
	
	private int length;
	private float speed;//-20 - 60 km/hour
	private int bearing; //0-360
	
	private double rotation;
	private double rot; //Rate of turn (degress/minute

	//private Logger logger = Logger.getLogger( this.getClass().getName() );

	public Ship( String id, LatLng position, float speed, Bearing bearing) {
		this( id, Calendar.getInstance().getTime(), speed, DEFAULT_LENGTH, position, bearing );
	}

	public Ship( String id, Date currentTime, float speed, LatLng position) {
		this( id, currentTime, speed, DEFAULT_LENGTH, position, Bearing.EAST );
	}
	
	public Ship( String id, Date currentTime, float speed, int length, LatLng position, Bearing bearing) {
		super( id, IModel.ModelTypes.SHIP, position );
		this.currentTime = currentTime;
		this.speed = speed;
		this.bearing = bearing.getAngle();
		this.length = length;
		this.rotation = (( double )this.length + 5 * Math.random()) * this.speed; 
		this.rot = ( rotation * Math.PI)/30 ; //( v + 5 *rand ) 2 * PI/60)
	}
	
	public double getTurn( long timemsec ){
		return timemsec * this.rot * 60000;
	}
	
	public double getMinTurnDistance(){
		return this.rotation * Math.tan( Math.toRadians(1));//the distance of a one degree turn
	}
	public float getSpeed() {
		return speed;
	}

	public int getBearing() {
		return bearing;
	}

	protected void setBearing( int angle ){
		this.bearing = angle;
	}

	/**
	 * Plot the next location, based on speed and bearing
	 * @param next
	 * @return
	 */
	public Location plotNext( Date next ){
		long interval = next.getTime() - this.currentTime.getTime();//msec
		double distance = this.speed * interval / TO_HOURS;
		double radian = Math.toRadians( this.bearing );
		double x = distance * Math.sin( radian );
		double y = distance * Math.cos( radian );
		return new Location((float) x, (float)y );
	}

	public LatLng sail( Date newTime ){
		float interval = newTime.getTime() - currentTime.getTime();
		float distance = interval * speed/ TO_HOURS;
		LatLng position = LatLngUtils.extrapolate( super.getLatLng(), bearing, distance);
		//logger.info( "New Position for spped:" + getSpeed() + "\n\t" + super.getLnglat() + ", to\n\t" + position );
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
