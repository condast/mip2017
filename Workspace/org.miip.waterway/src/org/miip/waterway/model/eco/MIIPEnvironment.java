package org.miip.waterway.model.eco;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.condast.commons.data.latlng.Field;
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
import org.miip.waterway.sa.ISituationalAwareness;
import org.miip.waterway.sa.SituationalAwareness;

public class MIIPEnvironment extends AbstractExecuteThread implements IMIIPEnvironment {

	private static final int DEFAULT_TIME_OUT =  1000;
	
	private static final String NAME = "HMS Rotterdam";
	private static final double LONGITUDE = 4.00f;
	private static final double LATITUDE  = 52.000f;
	private static final int DEFAULT_LENGTH  = 2000; //2 km
	private static final int DEFAULT_WIDTH  = 500; //500 m

	public static final int BANK_WIDTH = 60;	

	private Date currentTime;
	private Lock lock;
	private int timer;
	
	private Field field;//A field is represented by LatLng coordinates for the top-left corner
	private CentreShip ship;
	private Bank topBank;
	private Bank bottomBank;
	private Waterway waterway;
	private SituationalAwareness sa;
	
	private Collection<IEnvironmentListener> listeners;
	private int counter;
	private int bankWidth;
	private boolean initialsed;
	private boolean manual;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public MIIPEnvironment() {
		this( DEFAULT_LENGTH, DEFAULT_WIDTH, BANK_WIDTH );
	}
	private MIIPEnvironment( int length, int width, int bankWidth ) {
		this.field = new Field( new LatLng( NAME, LATITUDE, LONGITUDE), length, width);
		this.bankWidth = bankWidth;
		this.timer = DEFAULT_TIME_OUT;
		this.manual = false;
		lock = new ReentrantLock();
		this.listeners = new ArrayList<IEnvironmentListener>();
	}

	@Override
	public boolean onInitialise() {
		currentTime = Calendar.getInstance().getTime();
		
		//The bank on the top
		Rectangle rectangle = new Rectangle( 0, 0, field.getLength(), this.bankWidth );
		topBank = new Bank( rectangle );
		
		//The actual waterway
		LatLng latlng = LatLngUtils.extrapolate(this.field.getCoordinates(), Bearing.SOUTH.getAngle(), this.bankWidth); 
		long width = this.field.getWidth() - 2 * this.bankWidth;
		rectangle = new Rectangle( 0, this.bankWidth, field.getLength(), width );
		this.waterway = new Waterway( latlng, rectangle, 100);
		
		//Position of the ship
		latlng = this.field.getCentre();
		ship = new CentreShip( NAME, Calendar.getInstance().getTime(), 20, latlng );

		sa = new SituationalAwareness(ship, ISituationalAwareness.STEPS_512);
		sa.setRange( (int) (field.getLength()/2));
		
		//The bank at the bottom
		rectangle = new Rectangle( 0, (int) (field.getWidth()-this.bankWidth), field.getLength(), this.bankWidth );
		bottomBank =  new Bank( rectangle );
		
		this.initialsed = true;
		notifyChangeEvent( new EnvironmentEvent( this, EventTypes.INITIALSED ));
		counter = 0;
		return true;
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
	
	public Field getField() {
		return field;
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
			topBank.update( traverse.getX());
			bottomBank.update( traverse.getX());
			
			LatLng course = LatLngUtils.extrapolateEast( field.getCoordinates(), traverse.getX() );
			field = new Field( course, field.getLength(), field.getWidth() );
			
			ship.sail( currentTime );	
			//logger.info( "New Position " + this.position + ",\n\t\t   " + ship.getLnglat() );
			//logger.info( "Diff " + (this.position.getLongitude() - ship.getLnglat().getLongitude() ));
			//logger.info( "Diff " + LatLngUtils.distance(this.position, ship.getLnglat() ));
			waterway.update( course, currentTime, traverse.getX());

			sa.update(waterway);//after updating waterway
			float min_distance = manual?this.field.getLength(): 50;
			sa.controlShip( min_distance, this.manual );
			
			counter = ( counter + 1)%10;
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
		double x = Math.abs( LatLngUtils.lngDistance( field.getCoordinates(), model.getLatLng(), 0, 0));
		double y = 1.8* Math.abs( LatLngUtils.latDistance( field.getCoordinates(), model.getLatLng(), 0, 0));
		logger.fine("Creating location for " + model.getLatLng() + " = \n\t [" + x + ",  " + y  );
		return new Location( x, y );	
	}
}
