package org.miip.waterway.model.def;

import org.condast.commons.strings.StringStyler;
import org.miip.waterway.sa.ISituationalAwareness;

public interface IRadar<V extends Object> {

	public static final int DEFAULT_SENSITIVITY = 210;//0-1000
	public static final int DEFAULT_RANGE = 1200;//max 3000 meters

	public enum RadarSelect{
		WARP,
		HUMAN_ASSIST,
		AVERAGE,
		POND,
		LED_RING;

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
	
	/**
	 * set the input for this radar by adding the SA and the range (m)
	 * @param sa
	 */
	void setInput( ISituationalAwareness<V,?> sa);

	/**
	 * Get the sensitivity of the radar
	 * @return
	 */
	public int getSensitivity();

	/**
	 * get the range of the radar
	 * @return
	 */
	public long getRange();
	
	/**
	 * redraw the canvas
	 */
	public void refresh();

	void setSensitivity(int sensitivity);

	void setRange(int range);
}