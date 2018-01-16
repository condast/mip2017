package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractSituationalAwareness<I,V extends Object> implements ISituationalAwareness<I,V> {

	private Lock lock;
	
	private I input;
			
	private Collection<IShipMovedListener> listeners;

	protected AbstractSituationalAwareness( ) {
		this( MAX_DEGREES);
	}
	
	protected AbstractSituationalAwareness( int steps ) {
		lock = new ReentrantLock();
		this.listeners = new ArrayList<IShipMovedListener>();
	}

	protected void clear() {
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#addlistener(org.miip.waterway.sa.IShipMovedListener)
	 */
	@Override
	public void addlistener( IShipMovedListener listener ){
		this.listeners.add( listener);
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#removelistener(org.miip.waterway.sa.IShipMovedListener)
	 */
	@Override
	public void removelistener( IShipMovedListener listener ){
		this.listeners.remove(listener);
	}

	protected void notifylisteners( ShipEvent<I> event ){
		for( IShipMovedListener listener: listeners )
			listener.notifyShipMoved(event);	
	}
	
	@Override
	public I getInput() {
		return input;
	}

	protected abstract void onSetInput( I input );
	
	@Override
	public void setInput( I input ){
		if( input == null )
			return;
		lock.lock();
		try{
			clear();
			this.input = input;
			this.onSetInput(input);
			notifylisteners( new ShipEvent<I>( input ));
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
}
