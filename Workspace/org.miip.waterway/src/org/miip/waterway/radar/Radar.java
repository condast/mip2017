package org.miip.waterway.radar;


import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.miip.waterway.model.def.IRadar;

public class Radar<V,O extends Object> implements IRadar<V,O>{
	
	private ISituationalAwareness<V,O> sa;
	
	private int sensitivity; //part of the range
	private int steps;

	public Radar() {
		this( DEFAULT_STEPS ); 
	}
	
	public Radar( int steps ) {
		this.sensitivity = DEFAULT_SENSITIVITY;
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
		return ( this.sa == null )?0: (long) this.sa.getRange();
	}

	@Override
	public void setRange(int range) {
		if( this.sa != null )
			this.sa.setRange( range );
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
	public void setInput( ISituationalAwareness<V,O> sa ){
		if( this.sa != null ) {
			if( this.sa.equals(sa))
				return;
		}
		this.sa = sa;
		refresh();
	}

	@Override
	public void refresh() {
	}
}
