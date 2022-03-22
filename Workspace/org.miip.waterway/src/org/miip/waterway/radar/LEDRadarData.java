package org.miip.waterway.radar;

import org.condast.commons.data.latlng.BaseData;

public class LEDRadarData extends BaseData implements IRadarData {

	private int ch;//choice
	private int r;//range
	private int s;//sensitivity

	private int o;//(boolean)options: bit 0: log
	private boolean enb;

	public LEDRadarData( Choices choice, boolean enable, int range, int sensitivity, int options) {
		ch = choice.ordinal();
		this.enb = enable;
		this.r = range;
		this.s = sensitivity;
		this.o = options;
	}

	public LEDRadarData( Choices choice, String remarks) {
		super(remarks);
		ch = choice.ordinal();
	}

	public boolean isEnbaled() {
		return enb;
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

	public int getOptions() {
		return o;
	}

}
