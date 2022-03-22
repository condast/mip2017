package org.miip.waterway.model.def;

import org.condast.commons.autonomy.sa.radar.IRadar;
import org.condast.commons.strings.StringStyler;

public interface IMIIPRadar<V, O extends Object> extends IRadar<V,O>{

	public static final int DEFAULT_SENSITIVITY = 100;//0-100
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
}