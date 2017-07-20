package org.miip.waterway.ui.swt;

import org.condast.commons.strings.StringStyler;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.sa.SituationalAwareness;

public interface IRadarUI {

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
	public float getSensitivity();

	/**
	 * Set the sensitivity of the radar
	 * @return
	 */
	public void setSensitivity(int sensitivity);

	/**
	 * get the range of the radar
	 * @return
	 */
	public int getRange();

	/**
	 * Set the rangeo f the radar
	 * @param range
	 */
	public void setRange(int range);
	
	/**
	 * redraw the canvas
	 */
	public void refresh();
}