package org.miip.waterway.model.def;

public interface IDesignFactory {

	public String getId();
	
	/**
	 * Create a new environment
	 * @return
	 */
	public IEnvironment createEnvironment();
}
