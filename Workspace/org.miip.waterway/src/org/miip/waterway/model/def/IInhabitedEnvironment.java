package org.miip.waterway.model.def;

public interface IInhabitedEnvironment<I extends Object> extends IEnvironment {

	/**
	 * The inhabitant of the environment
	 * @return
	 */
	public I getInhabitant();
}
