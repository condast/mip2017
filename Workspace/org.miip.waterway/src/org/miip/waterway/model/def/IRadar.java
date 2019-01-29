package org.miip.waterway.model.def;

import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.strings.StringStyler;

public interface IRadar<V, O extends Object> {

	public static final int DEFAULT_SENSITIVITY = 210;//0-1000
	public static final int DEFAULT_RANGE = 30;//max 30 meters
	public static final int DEFAULT_STEPS = 360;//max 3000 meters

	public enum RadarSelect{
		WARP,
		HUMAN_ASSIST,
		AVERAGE,
		POND,
		LED_RING,
		LED_RING_REST;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		} 
		
		public static String[] getItems(){
			String[] items = new String[ values().length];
			for( int i=0; i<items.length; i++ ){
				items[i] = values()[i].toString();
			}
			return items;
		}
		
		public static RadarSelect getRadar( int index ){
			return RadarSelect.values()[index];
		}
	}
	
	ISituationalAwareness<V, O> getInput();

	/**
	 * Set the input for this radar by adding the SA and the range (m)
	 * If setRange is true, then the range of the radar overwrites te
	 * range of situational awareness
	 * @param sa
	 */
	void setInput( ISituationalAwareness<V,O> sa, boolean setRange );
	void setInput( ISituationalAwareness<V,O> sa );

	/**
	 * Get the sensitivity of the radar
	 * @return
	 */
	public int getSensitivity();

	/**
	 * get the range of the radar
	 * @return
	 */
	public double getRange();

	void setRange(double range);

	/**
	 * redraw the canvas
	 */
	public void refresh();

	void setSensitivity(int sensitivity);

	int getSteps();

	void setSteps(int steps);

	double toRadians(int step);
}