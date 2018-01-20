package org.miip.waterway.model.def;

import org.miip.waterway.environment.IEnvironment;

public interface IDesignFactory<D extends Object> {

	public String getId();
	
	/**
	 * Create a new environment
	 * @return
	 */
	public IEnvironment<D> createEnvironment();
}
