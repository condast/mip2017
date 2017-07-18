package org.miip.waterway.ui.swt;

import org.condast.commons.strings.StringStyler;
import org.eclipse.swt.widgets.Composite;
import org.miip.waterway.sa.SituationalAwareness;

public interface IRadarUI {

	public enum RadarSelect{
		WARP,
		HUMAN_ASSIST;

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
	}
	
	public Composite getParent();

	void setInput(SituationalAwareness sa);
}