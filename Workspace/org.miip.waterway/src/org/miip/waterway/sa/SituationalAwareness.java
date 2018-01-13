package org.miip.waterway.sa;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
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

public class SituationalAwareness extends AbstractSituationalAwareness<Waterway> {

	private CentreShip ship;
	
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
	
	public SituationalAwareness( CentreShip ship ) {
		this( ship, MAX_DEGREES);
	}
	
	public SituationalAwareness( CentreShip ship, int steps ) {
		this.ship = ship;
	}

	@Override
	protected long onSetInput(Waterway waterway, int step) {
		Map<Integer, Double> vectors = getVectors( waterway );
		double bank =  getBankDistance(waterway, step); 
		double shipdist = vectors.containsKey(step)? vectors.get(step):(double)Integer.MAX_VALUE;
		Double distance = ( shipdist < bank)? shipdist: bank;
		if( distance < this.getRange() ){
			getRadar().put( step, distance );
		}
		return 0;
	}
	
	/**
	 * Create a binary view of the situational awareness. This view creates
	 * subsequent averages of the radar images 
	 * @return
	 */
	@Override
	public SequentialBinaryTreeSet<Vector<Integer>> getBinaryView(){
		Iterator<Map.Entry<Integer, Double>> iterator = this.getRadar().entrySet().iterator();
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
		int quarter = this.getSteps() / 4;
		double radian = (i < quarter )|| (i>3*quarter)?toRadians(i): toRadians(2*quarter - i);
		return halfwidth/ Math.cos(radian);
	}
	
	protected Map<Integer, Double> getVectors( Waterway waterway ){
		LatLng latlng = ship.getLatLng();
		Map<Integer, Double> vectors = new TreeMap<Integer, Double>();
		for( Ship other: waterway.getShips() ){
			double hordistance = LatLngUtils.lngDistance(latlng, other.getLatLng(), 0, 0 );
			if( Math.abs( hordistance) > 2*this.getRange() )
				continue;
			double distance = LatLngUtils.distance(latlng, other.getLatLng() );
			Map.Entry<Integer, Double> vector = LatLngUtils.getVectorInSteps(latlng, other.getLatLng(), this.getSteps() );
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
