package org.miip.waterway.internal.model;

import org.condast.commons.latlng.LatLng;
import org.miip.waterway.model.def.IModel;

public abstract class AbstractModel implements IModel{

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
	public LatLng getLatLbg() {
		return latlng;
	}

	protected void setLnglat(LatLng lnglat) {
		this.latlng = lnglat;
	}
}