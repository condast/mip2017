package org.miip.waterway.model.def;

import org.miip.waterway.environment.IEnvironment;

public interface IInhabitedEnvironment<I extends Object> extends IEnvironment<I> {

	/**
	 * The inhabitant of the environment
	 * @return
	 */
	public I getInhabitant();
}
