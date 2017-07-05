package org.miip.waterway.ui.eco;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.condast.commons.lnglat.LngLat;
import org.condast.commons.lnglat.LngLatUtils;
import org.condast.commons.thread.AbstractExecuteThread;
import org.condast.symbiotic.core.environment.Environment;
import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.condast.symbiotic.core.environment.IEnvironmentListener.EventTypes;
import org.eclipse.swt.graphics.Rectangle;
import org.miip.waterway.internal.model.Location;
import org.miip.waterway.internal.model.Ship;
import org.miip.waterway.internal.model.Ship.Bearing;
import org.miip.waterway.internal.model.Waterway;
import org.miip.waterway.model.def.IModel;

public class MIIPEnvironment extends AbstractExecuteThread {

	private static final int DEFAULT_TIME_OUT =  1000;
	
	private static final String NAME = "HMS Rotterdam";
	private static final float LONGITUDE = 51.936914f;
	private static final float LATITUDE  = 4.055972f;
	private static final int DEFAULT_LENGTH  = 2000; //2 km
	private static final int DEFAULT_WIDTH  = 500; //500 m

	public static final int BANK_WIDTH = 60;	

	private Date currentTime;
	private Environment environment;
	private Lock lock;
	private int timer;
	
	private LngLat position;//The left centre of the course
	private Ship ship;
	private Bank topBank;
	private Bank bottomBank;
	private Waterway waterway;
	
	private Collection<IEnvironmentListener> listeners;
	private int counter;
	private int length; //The length of the course in meters
	private int width; //The width of the course
	private int bankWidth;
	private boolean initialsed;
	
	private static MIIPEnvironment miipenvironment = new MIIPEnvironment();
	
	private MIIPEnvironment() {
		this( DEFAULT_LENGTH, DEFAULT_WIDTH, BANK_WIDTH );
	}
	private MIIPEnvironment( int length, int width, int bankWidth ) {
		this.environment = new Environment();
		this.length = length;
		this.width = width;
		this.bankWidth = bankWidth;
		this.timer = DEFAULT_TIME_OUT;
		lock = new ReentrantLock();
		this.listeners = new ArrayList<IEnvironmentListener>();
	}
	public static MIIPEnvironment getInstance(){
		return miipenvironment;
	}
	
	public boolean isInitialsed() {
		return initialsed;
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
	
	
	public int getTimer() {
		return timer;
	}
	public void setTimer(int timer) {
		this.timer = timer;
	}
	public int getBankWidth() {
		return bankWidth;
	}
	public void setBankWidth(int bankWidth) {
		this.bankWidth = bankWidth;
	}
	public Ship getShip() {
		return ship;
	}
	
	public Bank[] getBanks(){
		Bank[] bank = new Bank[2];
		bank[0] = topBank;
		bank[1] = bottomBank;
		return bank;
	}
	
	public Waterway getWaterway() {
		return waterway;
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
		currentTime = Calendar.getInstance().getTime();
		
		//The left course associates a lnglat coordinate with a position on the course.
		//In this case we use the left centre
		float halfWidth = width/2;
		this.position = new LngLat( LONGITUDE, LATITUDE );
		
		Rectangle rect = new Rectangle(0, 0, length, this.bankWidth );
		topBank =  new Bank( Bank.Banks.UPPER, LngLatUtils.extrapolate( this.position, 0, halfWidth), rect );
		
		LngLat centre = LngLatUtils.extrapolate( this.position, Bearing.EAST.getDegrees(), length/2);
		ship = new Ship( NAME, Calendar.getInstance().getTime(), 20, centre );
		this.position = LngLatUtils.extrapolate( ship.getLnglat(), Bearing.EAST.getDegrees(), (int)( -length/2));
		counter = 0;

		rect = new Rectangle(0, this.bankWidth + width, length, this.bankWidth );//also account for the upper bank
		bottomBank =  new Bank( Bank.Banks.LOWER,LngLatUtils.extrapolate(this.position, 0, halfWidth), rect );
		
		this.waterway = new Waterway(this.position, length, width);
		this.initialsed = true;
		notifyChangeEvent( new EnvironmentEvent( this, EventTypes.INITIALSED ));
	}

	@Override
	public void onExecute() {
		while( super.isRunning()){
			if( !super.isPaused() ){
				lock.lock();
				try{
					currentTime = Calendar.getInstance().getTime();
					Location traverse = ship.plotNext(currentTime);
					
					LngLat course = LngLatUtils.extrapolateEast(this.position, traverse.getX() );
					this.position = course;
					
					ship.sail( currentTime );	
					waterway.update(currentTime, (float) traverse.getX());
					counter = ( counter + 1)%10;
					topBank.update( traverse.getY());
					bottomBank.update( traverse.getY());
					notifyChangeEvent( new EnvironmentEvent( this, EventTypes.CHANGED ));
				}
				finally{
					lock.unlock();
				}
				try{
					Thread.sleep(timer);
				}
				catch( InterruptedException ex ){
					ex.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Get the location with respect to the reference
	 * @param model
	 * @return
	 */
	public Location getLocation( IModel model ){
		double x = LngLatUtils.lngDistance( this.position, model.getLnglat(), 0, 0);
		double y = LngLatUtils.latDistance( this.position, model.getLnglat(), 0, 0);
		return new Location( x, y );
		
	}
}
