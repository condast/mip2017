package org.miip.waterway.model;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.miip.waterway.internal.model.AbstractModel;

/**
 * With a point, one can choose to either keep the location fixed and change the
 * lnglat coordinates, or to change the location. 
 * @author Kees
 *
 */
public class Point extends AbstractModel {

	private Location location;
	
	public Point(LatLng lngLat, Location location) {
		super( ModelTypes.COURSE, lngLat);
		this.location = location;
	}

	public Location getLocation() {
		return location;
	}

	@Override
	public void setLnglat(LatLng lnglat) {
		super.setLnglat(lnglat);
	}
	
	/**
	 * Set new LngLat coordinates based on the new location
	 * and keep the location in tact
	 * @param newloc
	 */
	public static LatLng getLngLat( Point current, Location newloc ){
		double displx = newloc.getX() - current.getLocation().getX();
		double disply = newloc.getY() - current.getLocation().getY();
		double distance = displx*displx + disply*disply;
		
		double rad = Math.asin(disply/Math.sqrt( distance));
		return LatLngUtils.extrapolate( current.getLatLng(), rad, distance);
	}
	
	/**
	 * Set a new location, based on the lnglat coordinates
	 * @param lnglat
	 */
	public static Location getLocation( Point current, LatLng lnglat ){
		double newX = LatLngUtils.lngDistance(lnglat, current.getLatLng(), 0, 0);
		double newY = LatLngUtils.latDistance(lnglat, current.getLatLng(), 0, 0);
		Location location = new Location( current.getLocation().getX() + newX, current.getLocation().getY() + newY );
		return location;
	}
}
