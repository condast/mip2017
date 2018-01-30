package org.miip.waterway.radar;

import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Vector;
import org.condast.commons.data.operations.AbstractOperator;
import org.miip.waterway.model.def.IRadar;
import org.miip.waterway.sa.ISituationalAwareness;

public class Radar<V extends Object> implements IRadar<V>{
	

	private AbstractOperator<Vector<Double>, Vector<Double>> operator = new AbstractOperator<Vector<Double>, Vector<Double>>(){

		@Override
		public Vector<Double> calculate(Collection<Vector<Double>> input) {
			float angle = 0;
			double distance = Integer.MAX_VALUE;
			for( Vector<Double> vector: input ){
				angle += vector.getKey();
				if( vector.getValue() < distance )
					distance = vector.getValue();
			}
			return new Vector<Double>( (double) (angle/input.size()), distance);
		}
	};

	private ISituationalAwareness<V,?> sa;
	
	private Map<Double, Double> vectors;
	
	private long range;
	private int sensitivity; //part of the range
	private int steps;

	private Logger logger = Logger.getLogger( this.getClass().getName() );

	public Radar() {
		this.sensitivity = DEFAULT_SENSITIVITY;
		this.range = DEFAULT_RANGE;
		vectors = new TreeMap<Double, Double>();
	}

	@Override
	public int getSensitivity() {
		return sensitivity;
	}

	@Override
	public void setSensitivity( int sensitivity) {
		this.sensitivity = sensitivity;
	}

	@Override
	public long getRange() {
		return range;
	}

	@Override
	public void setRange(int range) {
		this.range = range;
	}

	protected int getSteps() {
		return steps;
	}

	protected void setSteps(int steps) {
		this.steps = steps;
	}
	
	/**
	 * Get the radians for the given step size
	 * @param step
	 * @return
	 */
	protected double toRadians( int step ){
		double part = (double)step/steps;
		return 2*Math.PI*part;
	}

	protected ISituationalAwareness<V,?> getInput() {
		return sa;
	}

	@Override
	public void setInput( ISituationalAwareness<V,?> sa ){
		if( this.sa != null ) {
			if( this.sa.equals(sa))
				return;
		}
		this.sa = sa;
		if( sa != null ) {
			this.range = (long) sa.getField().getLength();
		}
		refresh();
	}

	@Override
	public void refresh() {
		// Default, nothing!
	}

	/**
	 * Create a binary view of the situational awareness. This view creates
	 * subsequent averages of the radar images. The example below gets the data at depth 5:
	 * 
	 * 		List<Vector<Double>> vectors = data.getValues(5);
	 * 		if( vectors.isEmpty() )
	 * 			return null;
	 * 		Collections.sort( vectors, new VectorComparator());
	 * 		for( Vector<Double> entry: vectors ){
	 * 			logger.fine("Angle: " + entry.getKey() + ", distance: " + entry.getValue() );
	 * 		}
	 * @return
	 */
	protected SequentialBinaryTreeSet<Vector<Double>> getBinaryView(){
		Iterator<Map.Entry<Double, Double>> iterator = this.vectors.entrySet().iterator();
		SequentialBinaryTreeSet<Vector<Double>> data = new SequentialBinaryTreeSet<Vector<Double>>( operator );
		while( iterator.hasNext() ){
			Map.Entry<Double, Double> entry = iterator.next();
			data.add( new Vector<Double>( entry.getKey(), entry.getValue()));
			logger.fine("Angle: " + entry.getKey() + ", distance: " + entry.getValue() );
		}
		return data;
	}
	
	protected class VectorComparator implements Comparator<Vector<Double>> {

		@Override
		public int compare(Vector<Double> arg0, Vector<Double> arg1) {
			return (int)( arg0.getValue() - arg1.getValue());
		}
		
	}
}
