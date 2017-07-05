package org.miip.waterway.internal.model;

import org.condast.commons.lnglat.LngLat;
import org.miip.waterway.model.def.IModel;

public abstract class AbstractModel implements IModel{

	private String id;
	private LngLat lnglat;
	private ModelTypes type;

	protected AbstractModel( ModelTypes type, LngLat lngLat ) {
		this( type.toString(), type, lngLat );
	}
	
	protected AbstractModel( String id, ModelTypes type, LngLat lnglat ) {
		this.id = id;
		this.type = type;
		this.lnglat = lnglat;
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
	public LngLat getLnglat() {
		return lnglat;
	}

	protected void setLnglat(LngLat lnglat) {
		this.lnglat = lnglat;
	}
}