package org.miip.waterway.sa;

import java.util.Map;
import java.util.TreeMap;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.condast.commons.latlng.LatLng;
import org.condast.commons.latlng.LatLngUtils;
import org.miip.waterway.model.Ship;
import org.miip.waterway.model.Waterway;

public class SituationalAwareness {

	private Ship ship;
	
	private Map<Integer, Double> radar;
	private Lock lock;
	private int range;
	
	public SituationalAwareness( Ship ship) {
		this.ship = ship;
		lock = new ReentrantLock();
		radar = new TreeMap<Integer, Double>();
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
			for( int i=0; i<360; i++ ){
				Double distance = vectors.get(i);
				if( distance == null ){
					radar.remove(i);
				}else{
					Double current = radar.get(i);
					if(( current == null  ) || ( distance < current ))
						radar.put( i, distance );
				}
			}
		}finally{
			lock.unlock();
		}
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
			Map.Entry<Integer, Double> vector = LatLngUtils.getVectorInDegrees(latlng, other.getLnglat());
			vectors.put( vector.getKey(), vector.getValue());
		}
		return vectors;
	}
}
