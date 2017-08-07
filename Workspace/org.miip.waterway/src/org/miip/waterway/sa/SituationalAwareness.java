package org.miip.waterway.sa;

import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.condast.commons.data.binary.IBinaryTreeSet;
import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.operations.AbstractOperator;
import org.condast.commons.data.operations.IOperator;
import org.condast.commons.latlng.LatLng;
import org.condast.commons.latlng.LatLngUtils;
import org.miip.waterway.model.Ship;
import org.miip.waterway.model.Waterway;

public class SituationalAwareness {

	public static final int MAX_DEGREES = 360;
	public static final int STEPS_512 = 512;//a more refined alternative to degrees for quick mathematics
	
	private Ship ship;
	
	private Map<Integer, Double> radar;
	private Lock lock;
	private int range;
	private int steps;
	private IBinaryTreeSet<Double> data;

	public SituationalAwareness( Ship ship ) {
		this( ship, MAX_DEGREES);
	}
	
	public SituationalAwareness( Ship ship, int steps ) {
		this.ship = ship;
		this.steps = steps;
		lock = new ReentrantLock();
		radar = new TreeMap<Integer, Double>();
		IOperator<Double, Double> average = new AbstractOperator<Double, Double>(){

			@Override
			public Double calculate(Collection<Double> input) {
				Double sum = 0d;
				Iterator<Double> iterator = input.iterator();
				while( iterator.hasNext() ){
					Double value = iterator.next();
					if( value == null )
						value = 0d;
					sum+=value;
				}
				return (sum/input.size());
			}
			
		};
		data = new SequentialBinaryTreeSet<Double>( average);
	}
	
	public int getSteps() {
		return steps;
	}

	public Ship.Bearing getBearing(){
		return ship.getBearing();
	}
	
	public int getRange() {
		return range;
	}

	public void update( Waterway waterway ){
		this.range = waterway.getLength()/2;
		Map<Integer, Double> vectors = getVectors( waterway );
		lock.lock();
		try{
			data.clear();
			for( int i=0; i< steps; i++ ){
				double bank =  getBankDistance(waterway, i); 
				double shipdist = vectors.containsKey(i)? vectors.get(i):(double)Integer.MAX_VALUE;
				Double distance = ( shipdist < bank)? shipdist: bank;
				data.add( distance);
				radar.put( i, distance );
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}finally{
			lock.unlock();
		}
	}

	public IBinaryTreeSet<Double> getTreeSet(){
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

	private double getBankDistance( Waterway waterway, int i ){
		double halfwidth = waterway.getWidth()/2;
		int quarter = this.steps / 4;
		double radian = (i < quarter )|| (i>3*quarter)?toRadians(i): toRadians(2*quarter - i);
		return halfwidth/ Math.cos(radian);
	}

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
	
	public Map<Integer, Double> getVectors( Waterway waterway ){
		LatLng latlng = ship.getLnglat();
		Map<Integer, Double> vectors = new TreeMap<Integer, Double>();
		for( Ship other: waterway.getShips() ){
			Map.Entry<Integer, Double> vector = LatLngUtils.getVectorInSteps(latlng, other.getLnglat(), this.steps );
			vectors.put( vector.getKey(), vector.getValue());
		}
		return vectors;
	}
}
