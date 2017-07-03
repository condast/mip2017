package org.miip.waterway.ui.eco;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.condast.commons.lnglat.LngLat;
import org.condast.commons.lnglat.Motion;
import org.condast.commons.thread.AbstractExecuteThread;
import org.condast.symbiotic.core.environment.Environment;
import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.condast.symbiotic.core.environment.IEnvironmentListener.EventTypes;
import org.eclipse.swt.graphics.Point;
import org.miip.waterway.internal.model.Ship;

public class MIIPEnvironment extends AbstractExecuteThread {

	private static final String NAME = "HMS Rotterdam";
	private static final float LONGITUDE = 51.936914f;
	private static final float LATITUDE  = 4.055972f;
	private static final int DEFAULT_LENGTH  = 2000; //2 km
	private static final int DEFAULT_WIDTH  = 500; //500 m
	
	private Environment environment;
	private Lock lock;
	private Ship ship;
	private Collection<IEnvironmentListener> listeners;
	private int counter;
	private LngLat position;
	private int length; //The length of the course in meters
	private int width; //The width of the course
	
	private Collection<Point> shore;
	
	private static MIIPEnvironment miipenvironment = new MIIPEnvironment();
	
	private MIIPEnvironment() {
		this( DEFAULT_LENGTH, DEFAULT_WIDTH );
	}
	private MIIPEnvironment( int length, int width ) {
		this.environment = new Environment();
		this.length = length;
		this.width = width;
		lock = new ReentrantLock();
		this.listeners = new ArrayList<IEnvironmentListener>();
		shore = new ArrayList<Point>();
	}
	public static MIIPEnvironment getInstance(){
		return miipenvironment;
	}
	
	public int getLength() {
		return length;
	}
	
	protected void setLength(int length) {
		this.length = length;
	}
	
	public int getWidth() {
		return width;
	}
	protected void setWidth(int width) {
		this.width = width;
	}
	public Ship getShip() {
		return ship;
	}
	
	public Point[] getShoreObjects(){
		return shore.toArray( new Point[ shore.size() ]);
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
		position = Motion.extrapolate( ship.getLnglat(), 45, (int)( -length/2));
		int offset = (int)( length/6);
		for( int i=0; i<6; i++){
			int top = (int)( 100* Math.random() + ( width / 2));
			int length = ( int)((float) offset * i * ( 1 + Math.random() ));
			shore.add( new Point( top, length ));
			top = (int)( 100* Math.random() - ( width / 2));
			length = ( int)((float) offset * i * ( 1 + Math.random() ));
			shore.add( new Point( top, length ));
		}
		counter = 0;
		notifyChangeEvent( new EnvironmentEvent( this, EventTypes.INITIALSED ));
	}

	@Override
	public void onExecute() {
		while( super.isRunning()){
			if( !super.isPaused() ){
				lock.lock();
				try{
					int distance = ship.sail( Calendar.getInstance().getTime() );	
					LngLat begin = Motion.extrapolate( ship.getLnglat(), 45, (int)( -length/2));
					position.setLongitude( begin.getLongitude());
					counter = ( counter + 1)%10;
					int offset = (int)( length/6);
					for( Point point: shore.toArray( new Point[shore.size()]) ){
						point.x -= distance;
						if( point.x >= 0 )
							continue;
						shore.remove( point );
						int x = ( int )( length + ( Math.random() - 1 )*offset); 
						point = new Point( x, point.y );
					}
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
}
