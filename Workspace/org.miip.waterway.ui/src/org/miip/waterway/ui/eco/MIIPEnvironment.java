package org.miip.waterway.ui.eco;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.condast.commons.lnglat.LngLat;
import org.condast.commons.thread.AbstractExecuteThread;
import org.condast.symbiotic.core.environment.Environment;
import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.condast.symbiotic.core.environment.IEnvironmentListener.EventTypes;
import org.miip.waterway.internal.model.Ship;

public class MIIPEnvironment extends AbstractExecuteThread {

	private static final String NAME = "HMS Rotterdam";
	private static final float LONGITUDE = 51.936914f;
	private static final float LATITUDE  = 4.055972f;
	
	private Environment environment;
	private Lock lock;
	private Ship ship;
	private Collection<IEnvironmentListener> listeners;
	private int counter;
	
	private static MIIPEnvironment miipenvironment = new MIIPEnvironment();
	
	private MIIPEnvironment() {
		this.environment = new Environment();
		lock = new ReentrantLock();
		this.listeners = new ArrayList<IEnvironmentListener>();
	}
	public static MIIPEnvironment getInstance(){
		return miipenvironment;
	}
	
	public Ship getShip() {
		return ship;
	}
	public void addListener( IEnvironmentListener listener ){
		this.listeners.add( listener );
	}

	public void removeListener( IEnvironmentListener listener ){
		this.listeners.remove( listener );
	}
	
	protected void notifyChangeEvent( EnvironmentEvent event ){
		for( IEnvironmentListener listener: listeners)
			listener.notifyEnvironmentChanged(event);
	}

	@Override
	public void onInitialise() {
		
		ship = new Ship( NAME, Calendar.getInstance().getTime(), 20, new LngLat( LONGITUDE, LATITUDE));
		counter = 0;
		notifyChangeEvent( new EnvironmentEvent( this, EventTypes.INITIALSED ));
	}

	@Override
	public void onExecute() {
		while( super.isRunning() ){
			lock.lock();
			try{
				ship.sail( Calendar.getInstance().getTime() );	
				counter = ( counter + 1)%10;
				notifyChangeEvent( new EnvironmentEvent( this, EventTypes.CHANGED ));
			}
			finally{
				lock.unlock();
			}
			try{
				Thread.sleep(1000);
			}
			catch( InterruptedException ex ){
				ex.printStackTrace();
			}
		}
	}
}
