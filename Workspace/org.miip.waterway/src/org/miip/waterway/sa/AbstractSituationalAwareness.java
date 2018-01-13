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
import org.condast.commons.data.latlng.Vector;
import org.condast.commons.data.operations.AbstractOperator;

public abstract class AbstractSituationalAwareness<I extends Object> implements ISituationalAwareness<I> {

	private Map<Integer, Double> radar;
	private Lock lock;
	private int range;
	private int sensitivity;
	
	private int steps;
	
	private I input;
		
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

	protected AbstractSituationalAwareness( ) {
		this( MAX_DEGREES);
	}
	
	protected AbstractSituationalAwareness( int steps ) {
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

	protected void notifylisteners( ShipEvent<I> event ){
		for( IShipMovedListener listener: listeners )
			listener.notifyShipMoved(event);	
	}
	
	@Override
	public I getInput() {
		return input;
	}

	protected abstract long onSetInput( I input, int step );
	
	@Override
	public void setInput( I input ){
		lock.lock();
		try{
			radar.clear();
			for( int i=0; i< steps; i++ ){
				long distance = onSetInput(input, i );
				notifylisteners( new ShipEvent<I>( input, i, distance, true ));
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


	
	/**
	 * Get the radians for the given step size
	 * @param step
	 * @return
	 */
	protected double toRadians( int step ){
		double part = (double)step/getSteps();
		return 2*Math.PI*part;
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
	
	
	private class VectorComparator implements Comparator<Vector<Integer>> {

		@Override
		public int compare(Vector<Integer> arg0, Vector<Integer> arg1) {
			return (int)( arg0.getValue() - arg1.getValue());
		}
		
	}
}
