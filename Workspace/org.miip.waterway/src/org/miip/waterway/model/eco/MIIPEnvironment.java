package org.miip.waterway.model.eco;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.condast.commons.autonomy.ca.AbstractCollisionAvoidance;
import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.ca.SimpleCollisionAvoidanceStrategy;
import org.condast.commons.autonomy.env.EnvironmentEvent;
import org.condast.commons.autonomy.env.IEnvironmentListener;
import org.condast.commons.autonomy.env.IEnvironmentListener.EventTypes;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.LatLngUtilsDegrees;
import org.condast.commons.data.plane.Field;
import org.condast.commons.thread.AbstractExecuteThread;
import org.miip.waterway.model.CentreShip;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.Location;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.Ship.Bearing;
import org.miip.waterway.model.def.IMIIPEnvironment;
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
	private CentreShip reference;
	private Bank topBank;
	private Bank bottomBank;
	private Waterway waterway;
	private SituationalAwareness sa;

	private Collection<IEnvironmentListener<IVessel>> listeners;
	private int counter;
	private int bankWidth;
	private boolean manual;
	private Logger logger = Logger.getLogger(this.getClass().getName());
	
	public MIIPEnvironment() {
		this( DEFAULT_LENGTH, DEFAULT_WIDTH, BANK_WIDTH );
	}
	
	private MIIPEnvironment( int length, int width, int bankWidth ) {
		this.field = new Field( new LatLng( NAME, LATITUDE, LONGITUDE), length, width);
		this.sa = new SituationalAwareness(reference, field);
		this.bankWidth = bankWidth;
		this.timer = DEFAULT_TIME_OUT;
		this.manual = false;
		lock = new ReentrantLock();
		this.listeners = new ArrayList<IEnvironmentListener<IVessel>>();
	}

	@Override
	public String getName() {
		return NAME;
	}

	@Override
	public void setEnabled(boolean enabled) {
		if(!enabled )
			this.stop();
		super.setEnabled(enabled);
	}

	@Override
	public boolean isActive() {
		return super.isRunning();
	}

	@Override
	public boolean onInitialise() {
		currentTime = Calendar.getInstance().getTime();
		
		//The bank on the top
		Field section = new Field( this.field.getCoordinates(), this.field.getLength(), this.bankWidth );
		topBank = new Bank( section );
		
		//The actual waterway
		LatLng latlng = LatLngUtilsDegrees.extrapolate(this.field.getCoordinates(), Bearing.SOUTH.getAngle(), this.bankWidth); 
		long width = this.field.getWidth() - 2 * this.bankWidth;
		section = new Field( latlng, this.field.getLength(), width );
		this.waterway = new Waterway( latlng, section, 100);
		
		//Position of the ship
		latlng = this.field.getCentre();
		//This vessel consists of situational awareness and collision avoidance 
		logger.info(latlng.toLocation());
		reference = new CentreShip( NAME, latlng, 20 );
		sa.setInput(this);
		ICollisionAvoidance<IVessel, IPhysical> ca = new DefaultCollisionAvoidance( reference, sa); 
		reference.init( sa, ca);
		
		//The bank at the bottom
		latlng = LatLngUtilsDegrees.extrapolate(this.field.getCoordinates(), Bearing.SOUTH.getAngle(), this.field.getWidth() - this.bankWidth); 
		section = new Field( latlng, field.getLength(), this.bankWidth );
		bottomBank = new Bank( section, 0, (int) (this.field.getWidth() - this.bankWidth) );
		
		notifyChangeEvent( new EnvironmentEvent<IVessel>( this, EventTypes.INITIALSED, null ));
		counter = 0;
		return true;
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
		if( waterway != null)
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
	public IVessel getInhabitant() {
		return reference;
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
		return this.sa ;
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#addListener(org.condast.symbiotic.core.environment.IEnvironmentListener)
	 */
	@Override
	public void addListener( IEnvironmentListener<IVessel> listener ){
		this.listeners.add( listener );
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.model.eco.IMIIPEnvironment#removeListener(org.condast.symbiotic.core.environment.IEnvironmentListener)
	 */
	@Override
	public void removeListener( IEnvironmentListener<IVessel> listener ){
		this.listeners.remove( listener );
	}
	
	protected void notifyChangeEvent( EnvironmentEvent<IVessel> event ){
		for( IEnvironmentListener<IVessel> listener: listeners)
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
			Date newTime = Calendar.getInstance().getTime();
			long interval = newTime.getTime() - currentTime.getTime();
			this.currentTime = newTime;
			LatLng location = reference.move( interval );
			logger.info(location.toLocation());
			
			Location traverse = drawNext( reference, interval);
			topBank.update( traverse.getX());
			bottomBank.update( traverse.getX());
			
			LatLng course = LatLngUtilsDegrees.extrapolateEast( field.getCoordinates(), traverse.getX() );
			field = new Field( course, field.getLength(), field.getWidth() );
			
			//logger.info( "New Position " + this.position + ",\n\t\t   " + ship.getLnglat() );
			//logger.info( "Diff " + (this.position.getLongitude() - ship.getLnglat().getLongitude() ));
			//logger.info( "Diff " + LatLngUtils.distance(this.position, ship.getLnglat() ));
			waterway.update( interval, traverse.getX());

			SituationalAwareness sa = (SituationalAwareness) this.reference.getSituationalAwareness();
			sa.setInput(this);//after updating waterway
			float min_distance = manual?this.field.getLength(): 50;
			sa.controlShip( min_distance, this.manual );
			
			counter = ( counter + 1)%10;
			notifyChangeEvent( new EnvironmentEvent<IVessel>( this ));
		}
		catch( Exception ex ){
			ex.printStackTrace();
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
	public Location getLocation( IPhysical model ){
		double x = Math.abs( LatLngUtils.lngDistance( field.getCoordinates(), model.getLocation(), 0, 0));
		double y = 1.8* Math.abs( LatLngUtils.latDistance( field.getCoordinates(), model.getLocation(), 0, 0));
		logger.fine("Creating location for " + model.getLocation() + " = \n\t [" + x + ",  " + y  );
		return new Location( x, y );	
	}

	@Override
	public Collection<IPhysical> getOthers() {
		// TODO Auto-generated method stub
		return null;
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.model.IVessel#plotNext(java.util.Date)
	 */
	public static Location drawNext( CentreShip ship, long interval ){
		double distance = ship.getSpeed() * interval / CentreShip.TO_HOURS;
		double radian = Math.toRadians( ship.getHeading() );
		double x = distance * Math.sin( radian );
		double y = distance * Math.cos( radian );
		return new Location((float) x, (float)y );
	}

	private class DefaultCollisionAvoidance extends AbstractCollisionAvoidance<IPhysical, IVessel>{

		public DefaultCollisionAvoidance( IVessel vessel, ISituationalAwareness<IVessel, IPhysical> sa ) {
			super( field, sa ,false);
			super.addStrategy( new SimpleCollisionAvoidanceStrategy<IPhysical, IVessel>( vessel, this ));
		}
		
		/**
		 * Get the critical distance for passage 
		 */
		@Override
		public double getCriticalDistance() {
			IVessel vessel = (IVessel) getReference(); 
			return vessel.getMinTurnDistance();
		}
	}
}
