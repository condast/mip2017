package org.miip.waterway.model.def;

import org.condast.commons.strings.StringStyler;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.sa.SituationalAwareness;

public interface IRadar {

	public static final int DEFAULT_SENSITIVITY = 210;//0-1000
	public static final int DEFAULT_RANGE = 1200;//max 3000 meters

	public enum RadarSelect{
		WARP,
		HUMAN_ASSIST,
		AVERAGE;

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
	
	public Composite getParent();

	/**
	 * set the input for this radar by adding the SA and the range (m)
	 * @param sa
	 */
	void setInput(SituationalAwareness sa);

	/**
	 * Get the sensitivity of the radar
	 * @return
	 */
	public int getSensitivity();

	/**
	 * get the range of the radar
	 * @return
	 */
	public int getRange();
	
	/**
	 * redraw the canvas
	 */
	public void refresh();
}