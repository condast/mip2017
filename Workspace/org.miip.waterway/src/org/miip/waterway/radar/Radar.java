package org.miip.waterway.radar;


import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.miip.waterway.model.def.IRadar;

public class Radar<V,O extends Object> implements IRadar<V,O>{
	
	private ISituationalAwareness<V,O> sa;
	
	private double range;
	private int sensitivity; //part of the range
	private int steps;

	public Radar() {
		this( DEFAULT_STEPS ); 
	}

	public Radar( int steps ) {
		this( steps, DEFAULT_SENSITIVITY, DEFAULT_RANGE );
	}
	
	public Radar( int steps, int sensitivity, double range ) {
		this.sensitivity = sensitivity;
		this.range = range;
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
	public double getRange() {
		return this.range;
	}

	@Override
	public void setRange( double range) {
		this.range = range;
		if( this.sa != null )
			this.sa.setRange(range);
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
	public ISituationalAwareness<V,O> getInput() {
		return sa;
	}

	@Override
	public void setInput( ISituationalAwareness<V,O> sa, boolean setRange ){
		if( this.sa != null ) {
			if( this.sa.equals(sa))
				return;
		}
		this.sa = sa;
		if( setRange)
			setRange( range );
		refresh();
	}

	public void setInput( ISituationalAwareness<V,O> sa){
		setInput( sa, false );
	}
	
	@Override
	public void refresh() {
	}
}
