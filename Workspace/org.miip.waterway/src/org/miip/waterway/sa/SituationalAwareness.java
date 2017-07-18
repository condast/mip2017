package org.miip.waterway.sa;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.Stack;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

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
	private AngleData data;
	private Stack<Double> stack;

	public SituationalAwareness( Ship ship ) {
		this( ship, MAX_DEGREES);
	}
	
	public SituationalAwareness( Ship ship, int steps ) {
		this.ship = ship;
		this.steps = steps;
		lock = new ReentrantLock();
		radar = new TreeMap<Integer, Double>();
		stack = new Stack<>();
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
			for( int i=0; i< steps; i++ ){
				double bank =  getBankDistance(waterway, i); 
				double shipdist = vectors.containsKey(i)? vectors.get(i):(double)Integer.MAX_VALUE;
				Double distance = ( shipdist < bank)? shipdist: bank;
				radar.put( i, distance );
			}
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}finally{
			lock.unlock();
		}
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

	private void updateData( int steps, double distance ){
		stack.push(distance);
		if( stack.size() < 2 )
			return;
		AngleData adata = new AngleData(stack.pop(), stack.pop());
		if( data == null ){
			data = adata;
			return;
		}
		boolean start = steps%2==0;
		if( start == true)
			return;
		return;
	}
	
	private class AngleData{
		private AngleData[] children;
		private int depth;
		private double average;

		public AngleData( double distance1, double distance2 ){
			children = new AngleData[2];
			this.average = ( distance1 + distance2)/2;
		}

		public AngleData( AngleData first, AngleData second) {
			this( first.getAverage(), second.getAverage());
			children[0] = first;
			first.descend();
			children[1] = second;
			second.descend();
			this.depth = 0;
		}
		
		public double getAverage() {
			return average;
		}

		public int getDepth() {
			return depth;
		}

		private void descend(){
			this.depth++;
		}
		
		
		
		
		
	}
}
