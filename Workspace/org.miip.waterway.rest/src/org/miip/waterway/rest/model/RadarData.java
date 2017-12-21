package org.miip.waterway.rest.model;

import org.condast.commons.data.latlng.BaseData;
import org.miip.waterway.radar.IRadarData;

public class RadarData extends BaseData implements IRadarData {

	private int ch;//choice
	private int r;//range
	private int s;//sensitivity

	public RadarData( Choices choice, int range, int sensitivity) {
		ch = choice.ordinal();
		this.r = range;
		this.s = sensitivity;
	}

	public RadarData( Choices choice, String remarks) {
		super(remarks);
		ch = choice.ordinal();
	}
	
	/* (non-Javadoc)
	 * @see org.miip.waterway.rest.model.IRadarData#getIndex()
	 */
	@Override
	public int getIndex(){
		return ch;
	}

	public int getR() {
		return r;
	}

	public int getS() {
		return s;
	}
}
