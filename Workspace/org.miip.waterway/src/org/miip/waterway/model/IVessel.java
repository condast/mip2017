package org.miip.waterway.model;

import org.condast.commons.data.latlng.LatLng;

public interface IVessel {

	/**
	 * Get the name of the vessel
	 * @return
	 */
	public String getName();
	
	/**
	 * Get the location of the vessel
	 * @return
	 */
	public LatLng getLocation();
	
	double getTurn(long timemsec);

	double getMinTurnDistance();

	/**
	 * speed in km/h
	 * @return
	 */
	double getSpeed();

	/**
	 * in degrees (0-360) starting from North
	 * @return
	 */
	double getBearing();

	/**
	 * Plot the next location, based on speed and bearing
	 * @param next
	 * @return
	 */
	LatLng plotNext( long timeinMillis);

	/**
	 * Plot the next position and update the current location.
	 * @param newTime
	 * @return
	 */
	LatLng sail(long timeinMillis);

}