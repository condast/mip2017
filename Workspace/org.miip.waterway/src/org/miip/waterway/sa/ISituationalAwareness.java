package org.miip.waterway.sa;

import java.util.Collection;

import org.condast.commons.data.latlng.Field;
import org.miip.waterway.environment.IEnvironment;
import org.miip.waterway.model.IVessel;

public interface ISituationalAwareness<V extends Object, I extends IEnvironment<V>> {

	int MAX_DEGREES = 360;
	int STEPS_512 = 512;//a more refined alternative to degrees for quick mathematics

	/**
	 * Clear the situational awareness cache
	 */
	void clear();
	
	public Field getField();
	
	public V getReference();
	
	void addlistener(ISituationListener<V> listener);

	void removelistener(ISituationListener<V> listener);
	
	public I getInput();
	
	public void setInput( I input );

	public Collection<V> getRadar();

	/**
	 * Predicts future interactions with nearby vessels and returns the distance and bearing
	 * @param time
	 * @param reference
	 * @param other
	 * @return
	 */
	public Collection<AbstractSituationalAwareness<?>.RadarData> predictFuture(int time, IVessel reference, IVessel other);
	
	/**
	 * Get a collection of all the shortest distances predicted in the near future
	 * @return
	 */
	public Collection<AbstractSituationalAwareness<?>.RadarData> getShortest();

	/**
	 * Update the situation
	 */
	void update();

	double getCriticalDistance();

}