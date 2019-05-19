package org.miip.waterway.model;

import org.condast.commons.autonomy.model.AbstractModel;
import org.condast.commons.data.latlng.LatLng;

/**
 * With a point, one can choose to either keep the location fixed and change the
 * lnglat coordinates, or to change the location. 
 * @author Kees
 *
 */
public class Point extends AbstractModel<Object> {

	public Point(LatLng lngLat) {
		super( ModelTypes.COURSE, lngLat);
	}

	@Override
	public void setLocation(LatLng lnglat) {
		super.setLocation(lnglat);
	}
}
