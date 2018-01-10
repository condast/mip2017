package org.miip.waterway.model;

import java.util.Date;

import org.condast.commons.data.latlng.LatLng;

public interface IVessel {

	double getTurn(long timemsec);

	double getMinTurnDistance();

	float getSpeed();

	int getBearing();

	/**
	 * Plot the next location, based on speed and bearing
	 * @param next
	 * @return
	 */
	Location plotNext(Date next);

	LatLng sail(Date newTime);

}