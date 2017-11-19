package org.miip.waterway.model.eco;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.thread.AbstractExecuteThread;
import org.condast.symbiotic.core.environment.EnvironmentEvent;
import org.condast.symbiotic.core.environment.IEnvironmentListener;
import org.condast.symbiotic.core.environment.IEnvironmentListener.EventTypes;
import org.miip.waterway.model.CentreShip;
import org.miip.waterway.model.Location;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.Ship.Bearing;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.model.def.IModel;
import org.miip.waterway.sa.SituationalAwareness;

public class MIIPEnvironment extends AbstractExecuteThread implements IMIIPEnvironment {

	private static final int DEFAULT_TIME_OUT =  1000;
	
	private static final String NAME = "HMS Rotterdam";
	private static final float LONGITUDE = 4.00f;
	private static final float LATITUDE  = 52.000f;
	private static final int DEFAULT_LENGTH  = 2000; //2 km
	private static final int DEFAULT_WIDTH  = 500; //500 m

	public static final int BANK_WIDTH = 60;	

	private Date currentTime;
	private Lock lock;
	private int timer;
	
	private LatLng position;//The left centre of the course
	private CentreShip ship;
	private Bank topBank;
	private Bank bottomBank;
	private Waterway waterway;
	private SituationalAwareness sa;
	
	private Collection<IEnvironmentListener> listeners;
	private int counter;
	private int length; //The length of the course in meters
	private int width; //The width of the course
	private int bankWidth;
	private boolean initialsed;
	private boolean manual;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public MIIPEnvironment() {
		this( DEFAULT_LENGTH, DEFAULT_WIDTH, BANK_WIDTH );
	}
	private MIIPEnvironment( int length, int width, int bankWidth ) {
		this.length = length;
		this.width = width;
		this.bankWidth = bankWidth;
		this.timer = DEFAULT_TIME_OUT;
		this.manual = false;
		lock = new ReentrantLock();
		this.listeners = new ArrayList<IEnvironmentListener>();
	}
	
	public boolean isInitialsed() {
		return initialsed;
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#setManual(boolean)
	 */
	@Override
	public void setManual(boolean manual) {
		this.manual = manual;
	}
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#clear()
	 */
	@Override
	public void clear(){
		waterway.clear();
		this.manual = false;
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
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#getTimer()
	 */
	@Override
	public int getTimer() {
		return timer;
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#setTimer(int)
	 */
	@Override
	public void setTimer(int timer) {
		this.timer = timer;
	}
	
	public int getBankWidth() {
		return bankWidth;
	}
	
	public void setBankWidth(int bankWidth) {
		this.bankWidth = bankWidth;
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#getShip()
	 */
	@Override
	public CentreShip getShip() {
		return ship;
	}
	
	public Bank[] getBanks(){
		Bank[] bank = new Bank[2];
		bank[0] = topBank;
		bank[1] = bottomBank;
		return bank;
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#getWaterway()
	 */
	@Override
	public Waterway getWaterway() {
		return waterway;
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#getSituationalAwareness()
	 */
	@Override
	public SituationalAwareness getSituationalAwareness() {
		return sa;
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#addListener(org.condast.symbiotic.core.environment.IEnvironmentListener)
	 */
	@Override
	public void addListener( IEnvironmentListener listener ){
		this.listeners.add( listener );
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#removeListener(org.condast.symbiotic.core.environment.IEnvironmentListener)
	 */
	@Override
	public void removeListener( IEnvironmentListener listener ){
		this.listeners.remove( listener );
	}
	
	protected void notifyChangeEvent( EnvironmentEvent event ){
		for( IEnvironmentListener listener: listeners)
			listener.notifyEnvironmentChanged(event);
	}

	@Override
	public boolean onInitialise() {
		currentTime = Calendar.getInstance().getTime();
		
		//The left course associates a lnglat coordinate with a position on the course.
		//In this case we use the left centre
		float halfWidth = width/2;
		this.position = new LatLng( LATITUDE, LONGITUDE );
		
		Rectangle rect = new Rectangle(0, 0, length, this.bankWidth );
		topBank = new Bank( Bank.Banks.UPPER, LatLngUtils.extrapolate( this.position, 0, halfWidth), rect );
		
		LatLng centre = LatLngUtils.extrapolate( this.position, Bearing.EAST.getAngle(), length/2);
		ship = new CentreShip( NAME, Calendar.getInstance().getTime(), 20, centre );

		this.waterway = new Waterway(this.position, length, width, 100);

		sa = new SituationalAwareness(ship, SituationalAwareness.STEPS_512);
		sa.setRange(length/2);
		
		counter = 0;
		rect = new Rectangle(0, this.bankWidth + width, length, this.bankWidth );//also account for the upper bank
		bottomBank =  new Bank( Bank.Banks.LOWER,LatLngUtils.extrapolate(this.position, 0, halfWidth), rect );
		
		this.initialsed = true;
		notifyChangeEvent( new EnvironmentEvent( this, EventTypes.INITIALSED ));
		return true;
	}
	
	public Integer[] getXCoordinates(){
		Collection<Integer> coords = new ArrayList<Integer>();
		int ref = (int)this.position.getLongitude();
		float interval = 0.001f;
		float posx = ref;
		LatLng end = LatLngUtils.extrapolate(this.position, Bearing.EAST.getAngle(), length);
		while( posx < end.getLongitude() ){
			if( posx >= this.position.getLongitude()){
				LatLng coord = new LatLng( this.position.getLatitude(), posx );
				coords.add( (int) LatLngUtils.distance(this.position, coord) );
			}
			posx += interval;
		}
		return coords.toArray( new Integer[ coords.size()]);
	}

	public Integer[] getYCoordinates(){
		Collection<Integer> coords = new ArrayList<Integer>();
		int ref = (int)this.position.getLongitude();
		float interval = 0.001f;
		float posy = ref;
		LatLng end = LatLngUtils.extrapolate(this.position, Bearing.NORTH.getAngle(), width);
		while( posy < end.getLatitude() ){
			if( posy >= this.position.getLatitude()){
				LatLng coord = new LatLng( posy, this.position.getLongitude() );
				coords.add( (int) LatLngUtils.distance(this.position, coord) );
			}
			posy += interval;
		}
		return coords.toArray( new Integer[ coords.size()]);
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#onExecute()
	 */
	@Override
	public synchronized void onExecute() {
		lock.lock();
		try{
			logger.fine("\n\nEXECUTE:");
			currentTime = Calendar.getInstance().getTime();
			Location traverse = ship.plotNext(currentTime);

			LatLng course = LatLngUtils.extrapolateEast(this.position, traverse.getX() );
			this.position = course;

			ship.sail( currentTime );	
			//logger.info( "New Position " + this.position + ",\n\t\t   " + ship.getLnglat() );
			//logger.info( "Diff " + (this.position.getLongitude() - ship.getLnglat().getLongitude() ));
			//logger.info( "Diff " + LatLngUtils.distance(this.position, ship.getLnglat() ));
			waterway.update( course, currentTime, traverse.getX());

			sa.update(waterway);//after updating waterway
			float min_distance = manual?this.getLength(): 50;
			sa.controlShip( min_distance, this.manual );
			
			counter = ( counter + 1)%10;
			topBank.update( traverse.getX());
			bottomBank.update( traverse.getX());
			
			notifyChangeEvent( new EnvironmentEvent( this, EventTypes.CHANGED ));
		}
		finally{
			lock.unlock();
		}
		sleep( timer );
	}
	
	/**
	 * Get the location with respect to the reference
	 * TODO NOTE: for some reason we need to add a correction of 1.8 in the latitude
	 * @param model
	 * @return
	 */
	public Location getLocation( IModel model ){
		double x = Math.abs( LatLngUtils.lngDistance( this.position, model.getLatLng(), 0, 0));
		double y = 1.8* Math.abs( LatLngUtils.latDistance( this.position, model.getLatLng(), 0, 0));
		logger.fine("Creating location for " + model.getLatLng() + " = \n\t [" + x + ",  " + y  );
		return new Location( x, y );	
	}
}
