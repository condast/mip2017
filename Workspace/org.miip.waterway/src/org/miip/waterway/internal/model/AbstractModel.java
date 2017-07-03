package org.miip.waterway.internal.model;

import org.condast.commons.lnglat.LngLat;
import org.condast.wph.core.definition.IModel;

public abstract class AbstractModel<E extends Enum<E>> implements IModel<E>{

	private String id;
	private LngLat lnglat;
	private E type;
	
	protected AbstractModel( String id, E type, LngLat lnglat ) {
		this.id = id;
		this.type = type;
		this.lnglat = lnglat;
	}

	@Override
	public String getId() {
		return id;
	}

	@Override
	public E getType() {
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