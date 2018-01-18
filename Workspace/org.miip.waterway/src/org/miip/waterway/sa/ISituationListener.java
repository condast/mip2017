package org.miip.waterway.sa;

public interface ISituationListener<I extends Object> {

	public void notifyShipMoved( SituationEvent<I> event );
}
