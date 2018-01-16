package org.miip.waterway.model.def;

import java.util.Collection;

public interface IReferenceEnvironment<I extends Object> extends IInhabitedEnvironment<I> {

	/**
	 * The inhabitant of the environment
	 * @return
	 */
	public I getInhabitant();
	
	
	/**
	 * The other participants
	 * @return
	 */
	public Collection<I> getOthers();
	
	/**
	 * Convenience method to get all the inhabitants
	 * @return
	 */
	public Collection<I> getAll();
}
