package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.TreeSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.condast.commons.data.latlng.Field;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.model.def.IReferenceEnvironment;

public abstract class AbstractSituationalAwareness<I extends IReferenceEnvironment<IPhysical>> implements ISituationalAwareness<IPhysical,I> {

	public static int DEFAULT_FUTURE_RANGE = 35000;//35 sec

	private int forecast;
	
	private I input;
	
	private IVessel owner;
	
	private Collection<IPhysical>others;
	private Collection<RadarData> shortestList;
	
	private Collection<ISituationListener<IPhysical>> listeners;
	private Lock lock;
	
	private Logger logger = Logger.getLogger(this.getClass().getName());

	protected AbstractSituationalAwareness( IVessel owner) {
		this( owner, MAX_DEGREES);
	}
	
	protected AbstractSituationalAwareness( IVessel owner, int steps ) {
		lock = new ReentrantLock();
		this.forecast = DEFAULT_FUTURE_RANGE;
		this.owner = owner;
		this.others = new ArrayList<IPhysical>();
		shortestList = new TreeSet<RadarData>( new DataComparator());
		this.listeners = new ArrayList<ISituationListener<IPhysical>>();
	}

	@Override
	public void clear() {
		this.others.clear();
		this.shortestList.clear();
	}

	@Override
	public Field getField() {
		if( getInput() == null )
			return null;
		return getInput().getField();
	}

	@Override
	public IPhysical getReference() {
		return this.owner;
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#addlistener(org.miip.waterway.sa.IShipMovedListener)
	 */
	@Override
	public void addlistener( ISituationListener<IPhysical> listener ){
		this.listeners.add( listener);
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#removelistener(org.miip.waterway.sa.IShipMovedListener)
	 */
	@Override
	public void removelistener( ISituationListener<IPhysical> listener ){
		this.listeners.remove(listener);
	}

	protected void notifylisteners( SituationEvent<IPhysical> event ){
		for( ISituationListener<IPhysical> listener: listeners )
			listener.notifySituationChanged(event);	
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
			notifylisteners( new SituationEvent<IPhysical>( this.owner ));
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * Get the critical distance for passage 
	 */
	@Override
	public double getCriticalDistance() {
		IVessel vessel = (IVessel) getReference(); 
		return vessel.getMinTurnDistance();
	}
	
	@Override
	public void update() {
		this.clear();
		for( IPhysical other: getInput().getOthers()) {
			this.others.add(other);
			predictFuture(this.forecast, (IVessel)getInput().getInhabitant(), (IVessel)other);
		}
	}
	/**
	 * Predict the future in the given time (in seconds)
	 * @param interval
	 * @param router
	 * @param bearing
	 * @param speed
	 */
	@Override
	public synchronized Collection<RadarData> predictFuture( int time, IVessel reference, IVessel other ){
		//logger.info("START POSITION: " + position.toString());
		StringBuffer buffer = new StringBuffer();
		LatLng newpos = reference.getLocation();
		LatLng newotherpos = other.getLocation();
		//logger.fine("Start Time: " + current.getTime());
		TreeSet<RadarData> timemap = new TreeSet<RadarData>( new DataComparator());
		RadarData shortest = null; 
		for( int i=0; i < time; i++ ){
			try {
				Field field = getInput().getField();
				long interval = 1000*i;//seconds
				//buffer.append("Interval: " + ( interval/1000) + ":\t" + field.printCoordinates(newpos, false ));
				//buffer.append( "\t" + field.printCoordinates(newotherpos, false ));
				newpos = reference.plotNext( interval );
				if(!field.isInField( newpos, 10 ))
					continue;
				
				//logger.fine(newpos.toString());
				newotherpos = other.plotNext(interval);
				if(!field.isInField( newotherpos, 10 ))
					continue;
				double diff = LatLngUtils.distance(newpos, newotherpos);		
				double bearing = LatLngUtils.getBearingInDegrees(newpos,newotherpos);		
				//buffer.append( "\t bearing and distance at time: " + interval + " is {" + bearing + ", " + diff + "}\n");
				RadarData data = new RadarData(newpos, interval, bearing, diff); 
				if( shortest == null ) {
					shortest = data;
				}else{
					if( shortest.isShorter(diff))
						shortest = data;
				}
				timemap.add( data );
			}
			catch( Exception ex ) {
				buffer.append( ex.getMessage() + "\n");
				break;
			}
		}
		if( !timemap.isEmpty() ) {
			shortest.setShortest(true);
			shortest.past = timemap.first().equals(shortest);
			this.shortestList.add(shortest);
			for( RadarData data: timemap ) {
				buffer.append( data.toString() +"\n");
			}
			if( shortest.distance > getCriticalDistance()) {
				Collection<RadarData> temp = new ArrayList<RadarData>();
				for( RadarData data: temp ) {
					if( shortest.isEarlier(data) && ( data.distance > getCriticalDistance())) {
						temp.add(data );
					}
				}
				timemap.removeAll(temp);
			}
		}
		logger.fine( buffer.toString());
		return timemap;
	}

	@Override
	public Collection<AbstractSituationalAwareness<?>.RadarData> getShortest() {
		return this.shortestList;
	}

	private class DataComparator implements Comparator<RadarData>{

		@Override
		public int compare(RadarData o1, RadarData o2) {
			double cmp =  (o1.distance - o2.distance);
			if( cmp > Double.MIN_VALUE )
				return 1;
			else if( cmp < Double.MIN_VALUE )
				return -1;
			return (int) (o1.time - o2.time );
		}
	}
	
	public class RadarData{
		private LatLng latlng;
		private long time;
		private double angle;
		private double distance;
		private boolean shortest;
		private boolean past;
		protected RadarData(LatLng latlng, long time, double angle, double distance) {
			super();
			this.latlng = latlng;
			this.time = time;
			this.angle = angle;
			this.distance = distance;
			this.shortest = false;
		}
		
		public LatLng getLatlng() {
			return latlng;
		}

		public long getTime() {
			return time;
		}

		public double getAngle() {
			return angle;
		}

		public double getDistance() {
			return distance;
		}
		
		public boolean isPast() {
			return past;
		}

		public boolean isShortest() {
			return shortest;
		}

		public void setShortest(boolean shortest) {
			this.shortest = shortest;
		}

		public boolean isShorter( double distance ) {
			return ( this.distance > distance);
		}
		
		public boolean isLater( RadarData data ) {
			return data.time > this.time;
		}
		
		public boolean isEarlier( RadarData data ) {
			return data.time < this.time;
		}

		@Override
		public String toString() {
			return "{" + this.distance + ",\t"+ angle + ",\t" + this.time + "}\t" + this.shortest;
		}
	}
}
