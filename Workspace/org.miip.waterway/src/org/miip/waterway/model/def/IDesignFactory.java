package org.miip.waterway.model.def;

import org.condast.commons.autonomy.env.IEnvironment;

public interface IDesignFactory<D extends Object> {

	public String getId();
	
	/**
	 * Create a new environment
	 * @return
	 */
	public IEnvironment<D> createEnvironment();
}
