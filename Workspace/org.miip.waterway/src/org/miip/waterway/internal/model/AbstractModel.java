package org.miip.waterway.internal.model;

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.data.latlng.LatLng;

public abstract class AbstractModel implements IPhysical{

	private String id;
	private LatLng latlng;
	private ModelTypes type;

	protected AbstractModel( ModelTypes type, LatLng lngLat ) {
		this( type.toString(), type, lngLat );
	}
	
	protected AbstractModel( String id, ModelTypes type, LatLng lnglat ) {
		this.id = id;
		this.type = type;
		this.latlng = lnglat;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public ModelTypes getType() {
		return type;
	}

	@Override
	public LatLng getLocation() {
		return latlng;
	}

	protected void setLnglat(LatLng lnglat) {
		this.latlng = lnglat;
	}
}