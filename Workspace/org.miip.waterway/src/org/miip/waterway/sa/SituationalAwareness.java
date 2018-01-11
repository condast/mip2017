package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Logger;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.data.latlng.LatLngUtils;
import org.condast.commons.data.latlng.Vector;
import org.condast.commons.data.operations.AbstractOperator;
import org.miip.waterway.model.CentreShip;
import org.miip.waterway.model.Ship;
import org.miip.waterway.model.Waterway;
import org.miip.waterway.model.CentreShip.Controls;

public class SituationalAwareness implements ISituationalAwareness {

	private CentreShip ship;
	
	private Map<Integer, Double> radar;
	private Lock lock;
	private int range;
	private int sensitivity;
	
	private int steps;
		
	private AbstractOperator<Vector<Integer>, Vector<Integer>> operator = new AbstractOperator<Vector<Integer>, Vector<Integer>>(){

		@Override
		public Vector<Integer> calculate(Collection<Vector<Integer>> input) {
			float angle = 0;
			double distance = Integer.MAX_VALUE;
			for( Vector<Integer> vector: input ){
				angle += vector.getKey();
				if( vector.getValue() < distance )
					distance = vector.getValue();
			}
			return new Vector<Integer>( (int) (angle/input.size()), distance);
		}
	};
	
	private Logger logger = Logger.getLogger( this.getClass().getName() );
	
	private Collection<IShipMovedListener> listeners;

	public SituationalAwareness( CentreShip ship ) {
		this( ship, MAX_DEGREES);
	}
	
	public SituationalAwareness( CentreShip ship, int steps ) {
		this.ship = ship;
		this.steps = steps;
		lock = new ReentrantLock();
		this.listeners = new ArrayList<IShipMovedListener>();
		radar = new TreeMap<Integer, Double>();
	}

	@Override
	public int getRange() {
		return range;
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#setRange(int)
	 */
	@Override
	public void setRange(int range) {
		this.range = range;
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#getSensitivity()
	 */
	@Override
	public int getSensitivity() {
		return sensitivity;
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#setSensitivity(int)
	 */
	@Override
	public void setSensitivity(int sensitivity) {
		this.sensitivity = sensitivity;
	}

	/* (non-Javadoc)
	 * @see org.miip.waterway.sa.ISituationalAwareness#getSteps()
	 */
	@Override
	public int getSteps() {
		return steps;
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

	protected void notifylisteners( ShipEvent event ){
		for( IShipMovedListener listener: listeners )
			listener.notifyShipMoved(event);	
	}
	
	public void update( Waterway waterway ){
		Map<Integer, Double> vectors = getVectors( waterway );
		lock.lock();
		try{
			radar.clear();
			for( int i=0; i< steps; i++ ){
				double bank =  getBankDistance(waterway, i); 
				double shipdist = vectors.containsKey(i)? vectors.get(i):(double)Integer.MAX_VALUE;
				Double distance = ( shipdist < bank)? shipdist: bank;
				if( distance < this.range ){
					radar.put( i, distance );
				}
				notifylisteners( new ShipEvent( ship, i, distance.longValue(), true ));
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}finally{
			lock.unlock();
		}
	}
	
	/**
	 * Create a binary view of the situational awareness. This view creates
	 * subsequent averages of the radar images 
	 * @return
	 */
	@Override
	public SequentialBinaryTreeSet<Vector<Integer>> getBinaryView(){
		Iterator<Map.Entry<Integer, Double>> iterator = this.radar.entrySet().iterator();
		SequentialBinaryTreeSet<Vector<Integer>> data = new SequentialBinaryTreeSet<Vector<Integer>>( operator );
		while( iterator.hasNext() ){
			Map.Entry<Integer, Double> entry = iterator.next();
			data.add( new Vector<Integer>( entry.getKey(), entry.getValue()));
			logger.fine("Angle: " + entry.getKey() + ", distance: " + entry.getValue() );
		}
		List<Vector<Integer>> vectors = data.getValues(5);
		if( vectors.isEmpty() )
			return null;
		Collections.sort( vectors, new VectorComparator());
		for( Vector<Integer> entry: vectors ){
			logger.fine("Angle: " + entry.getKey() + ", distance: " + entry.getValue() );
		}
		return data;
	}


	public void controlShip( float min_distance, boolean max ){
		SequentialBinaryTreeSet<Vector<Integer>> data = this.getBinaryView();
		if( data == null )
			return;
		List<Vector<Integer>> vectors = data.getValues(5);
		if( vectors.isEmpty() )
			return;
		Collections.sort( vectors, new VectorComparator());
		for( Vector<Integer> entry: vectors ){
			logger.fine("Angle: " + entry.getKey() + ", distance: " + entry.getValue() );
		}
		int angle = vectors.get(0).getKey();
		double distance = vectors.get(0).getValue();
		if(max && distance > min_distance )
			return;
		CentreShip.Controls control = null;
		if(( angle < 325) || ( angle > 448 ))
			control = Controls.UP;
		else if( angle < 192)
			control = Controls.RIGHT;
		else if( angle < 320)
			control = Controls.DOWN;
		else if( angle < 448)
			control = Controls.LEFT;
		ship.setControl(control);
		logger.fine("Angle: " + angle + ", Distance " + distance + " Control: " + control.name() );
	}
	
	/**
	 * Get the radians for the given step size
	 * @param step
	 * @return
	 */
	protected double toRadians( int step ){
		double part = (double)step/getSteps();
		return 2*Math.PI*part;
	}

	private double getBankDistance( Waterway waterway, int i ){
		double halfwidth = waterway.getField().getWidth()/2;
		int quarter = this.steps / 4;
		double radian = (i < quarter )|| (i>3*quarter)?toRadians(i): toRadians(2*quarter - i);
		return halfwidth/ Math.cos(radian);
	}

	@Override
	public Map<Integer, Double> getRadar(){
		Map<Integer, Double> results = new TreeMap<Integer, Double>();
		lock.lock();
		try{
			results.putAll(radar);
		}
		finally{
			lock.unlock();
		}
		return results;
	}
	
	protected Map<Integer, Double> getVectors( Waterway waterway ){
		LatLng latlng = ship.getLatLng();
		Map<Integer, Double> vectors = new TreeMap<Integer, Double>();
		for( Ship other: waterway.getShips() ){
			double hordistance = LatLngUtils.lngDistance(latlng, other.getLatLng(), 0, 0 );
			if( Math.abs( hordistance) > 2*this.range )
				continue;
			double distance = LatLngUtils.distance(latlng, other.getLatLng() );
			Map.Entry<Integer, Double> vector = LatLngUtils.getVectorInSteps(latlng, other.getLatLng(), this.steps );
			logger.fine( "Mutual distance:\t" + latlng + "\n\t\t\t" + other.getLatLng() );
			//logger.info( "Diff " + (latlng.getLongitude() - other.getLatLng().getLongitude() ));
			if( distance < 300 )
				logger.info( "Diff " + distance + "[" + vector.getKey() + ", "+ vector.getValue() + "]");
			vectors.put( vector.getKey(), vector.getValue());
			//if( vector.getValue() < 100 )
				//logger.info("Vector found for" + ship.getLnglat() + " and\n\t " + 
			//other.getLnglat() );
		}
		return vectors;
	}
	
	private class VectorComparator implements Comparator<Vector<Integer>> {

		@Override
		public int compare(Vector<Integer> arg0, Vector<Integer> arg1) {
			return (int)( arg0.getValue() - arg1.getValue());
		}
		
	}
}
