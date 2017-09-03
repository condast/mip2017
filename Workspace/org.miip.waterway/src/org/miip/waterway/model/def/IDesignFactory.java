package org.miip.waterway.model.def;

public interface IDesignFactory {

	/**
	 * Create a new environment
	 * @return
	 */
	public IMIIPEnvironment createEnvironment();
}
