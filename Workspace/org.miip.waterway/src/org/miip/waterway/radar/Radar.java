package org.miip.waterway.radar;


import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.miip.waterway.model.def.IRadar;

public class Radar<V extends Object> implements IRadar<V>{
	
	private ISituationalAwareness<V,?> sa;
	
	private long range;
	private int sensitivity; //part of the range
	private int steps;

	public Radar() {
		this( DEFAULT_STEPS ); 
	}
	
	public Radar( int steps ) {
		this.sensitivity = DEFAULT_SENSITIVITY;
		this.range = DEFAULT_RANGE;
		this.steps = steps;
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

	@Override
	public int getSteps() {
		return steps;
	}

	@Override
	public void setSteps(int steps) {
		this.steps = steps;
	}
	
	/**
	 * Get the radians for the given step size
	 * @param step
	 * @return
	 */
	@Override
	public double toRadians( int step ){
		double part = (double)step/steps;
		return 2*Math.PI*part;
	}

	@Override
	public ISituationalAwareness<V,?> getInput() {
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
	}
}
