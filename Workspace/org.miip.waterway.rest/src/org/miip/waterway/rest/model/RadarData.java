package org.miip.waterway.rest.model;

import org.condast.commons.data.latlng.BaseData;

public class RadarData extends BaseData {

	public enum Choices {
		  COLOUR_WIPE_RED,
		  COLOUR_WIPE_GREEN,
		  COLOUR_WIPE_BLUE,
		  THEATER_CHASE_RED,
		  THEATER_CHASE_WHITE,
		  THEATER_CHASE_BLUE,
		  RAINBOW,
		  RAINBOW_CYCLE,
		  RAINBOW_THEATRE_CHASE,
		  ALL
		};	

	private int ch;//choice

	public RadarData( Choices choice) {
		ch = choice.ordinal();
	}

	public RadarData( Choices choice, String remarks) {
		super(remarks);
		ch = choice.ordinal();
	}
	
	public int getIndex(){
		return ch;
	}
}
