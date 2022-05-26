package org.miip.waterway.radar;

import java.util.ArrayList;
import java.util.Collection;

import org.condast.commons.strings.StringStyler;

public interface ILEDRadarData {

	public enum Choices {
		DISABLED,
		RADAR,
		COLOUR_WIPE_RED,
		COLOUR_WIPE_GREEN,
		COLOUR_WIPE_BLUE,
		THEATER_CHASE_RED,
		THEATER_CHASE_WHITE,
		THEATER_CHASE_BLUE,
		RAINBOW,
		RAINBOW_CYCLE,
		RAINBOW_THEATRE_CHASE,
		ALL;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}

		public static String[] getItems(){
			Collection<String> items = new ArrayList<>();
			for( Choices choice: values()){
				items.add( choice.toString());
			}
			return items.toArray( new String[items.size()]);
		}
	}

	int getIndex();
}