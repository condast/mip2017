package org.miip.waterway.sa;

import java.util.Collection;

import org.condast.commons.data.latlng.Field;

public interface ISituationalAwareness<I,V extends Object> {

	int MAX_DEGREES = 360;
	int STEPS_512 = 512;//a more refined alternative to degrees for quick mathematics

	public Field getField();
	
	public V getReference();
	
	void addlistener(ISituationListener<V> listener);

	void removelistener(ISituationListener<V> listener);
	
	public I getInput();
	
	public void setInput( I input );

	public Collection<V> getRadar();
}