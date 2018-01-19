package org.miip.waterway.model.def;

import org.miip.waterway.environment.IEnvironment;

public interface IDesignFactory {

	public String getId();
	
	/**
	 * Create a new environment
	 * @return
	 */
	public IEnvironment createEnvironment();
}
