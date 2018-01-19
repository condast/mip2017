package org.miip.waterway.sa;

import java.util.Collection;

import org.condast.commons.data.latlng.Field;
import org.miip.waterway.environment.IEnvironment;
import org.miip.waterway.model.IVessel;

public interface ISituationalAwareness<V extends Object, I extends IEnvironment<V>> {

	int MAX_DEGREES = 360;
	int STEPS_512 = 512;//a more refined alternative to degrees for quick mathematics

	public Field getField();
	
	public V getReference();
	
	void addlistener(ISituationListener<V> listener);

	void removelistener(ISituationListener<V> listener);
	
	public I getInput();
	
	public void setInput( I input );

	public Collection<V> getRadar();

	Collection<AbstractSituationalAwareness<V, I>.RadarData> predictFuture(int time, IVessel reference, IVessel other);
}