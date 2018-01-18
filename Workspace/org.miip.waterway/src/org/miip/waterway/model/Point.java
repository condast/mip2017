package org.miip.waterway.model;

import org.condast.commons.data.latlng.LatLng;
import org.miip.waterway.internal.model.AbstractModel;

/**
 * With a point, one can choose to either keep the location fixed and change the
 * lnglat coordinates, or to change the location. 
 * @author Kees
 *
 */
public class Point extends AbstractModel {

	public Point(LatLng lngLat) {
		super( ModelTypes.COURSE, lngLat);
	}

	@Override
	public void setLnglat(LatLng lnglat) {
		super.setLnglat(lnglat);
	}
}
