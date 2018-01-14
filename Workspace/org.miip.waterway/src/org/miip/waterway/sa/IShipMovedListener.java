package org.miip.waterway.sa;

public interface IShipMovedListener<I extends Object> {

	public void notifyShipMoved( ShipEvent<I> event );
}
