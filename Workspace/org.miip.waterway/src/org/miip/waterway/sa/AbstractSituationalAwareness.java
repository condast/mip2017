package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public abstract class AbstractSituationalAwareness<I,V extends Object> implements ISituationalAwareness<I,V> {

	private Lock lock;
	
	private I input;
	
	private V owner;
			
	private Collection<ISituationListener<V>> listeners;

	protected AbstractSituationalAwareness( V owner) {
		this( owner, MAX_DEGREES);
	}
	
	protected AbstractSituationalAwareness( V owner, int steps ) {
		lock = new ReentrantLock();
		this.owner = owner;
		this.listeners = new ArrayList<ISituationListener<V>>();
	}

	protected void clear() {
	}
	
	protected V getOwner() {
		return owner;
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#addlistener(org.miip.waterway.sa.IShipMovedListener)
	 */
	@Override
	public void addlistener( ISituationListener<V> listener ){
		this.listeners.add( listener);
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#removelistener(org.miip.waterway.sa.IShipMovedListener)
	 */
	@Override
	public void removelistener( ISituationListener<V> listener ){
		this.listeners.remove(listener);
	}

	protected void notifylisteners( SituationEvent<V> event ){
		for( ISituationListener<V> listener: listeners )
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
			this.onSetInput(input);
			this.input = input;
			notifylisteners( new SituationEvent<V>( this.owner ));
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
}
