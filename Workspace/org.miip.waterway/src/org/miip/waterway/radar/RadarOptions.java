package org.miip.waterway.radar;

import org.condast.commons.preferences.IPreferenceStore;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.miip.waterway.model.def.IRadar;
import org.miip.waterway.radar.IRadarData.Choices;

public class RadarOptions{

	public static final int DEFAULT_NR_OF_LEDS = 24;
	
	public enum Options{
		ENABLE,
		TYPE,
		TOKEN,
		CHOICE,
		SENSITIVITY,
		RANGE,
		TRANSPARENCY,
		NR_OF_LEDS,
		OPTIONS,
		COUNTER;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	private IPreferenceStore<String, String> store;
	
	public RadarOptions( IPreferenceStore<String, String> store, String vesselName) {
		this.store = store;
		putSettings( Options.TOKEN, getToken(vesselName.getBytes()));
	}

	public boolean isEnabled() {
		return getBoolean(Options.ENABLE.name(), (byte)0);
	}
	
	public void setEnable( boolean choice ) {
		this.store.setBoolean(Options.ENABLE.name(), (byte)0, choice);
	}

	private final String getToken( byte[] vesselName ) {
		int token = 0;
		for( byte bt: vesselName ) {
			token+= bt;
		}
		return String.valueOf( token );
	}
	
	public Choices getChoice() {
		String str = getSettings( Options.CHOICE);
		Choices choice = StringUtils.isEmpty(str)?Choices.RADAR: Choices.valueOf(str);
		return choice;
	}

	public void setChoice( Choices choice ) {
		putSettings(Options.CHOICE, choice.name());
	}

	public IRadar.RadarSelect getRadarType() {
		String str = getSettings( Options.TYPE);
		IRadar.RadarSelect type = StringUtils.isEmpty(str)? IRadar.RadarSelect.LED_RING: 
			IRadar.RadarSelect.valueOf(str);
		return type;
	}

	public void setRadarType( IRadar.RadarSelect type ) {
		putSettings(Options.TYPE, type.name());
	}

	public int getSensitivity() {
		String str = getSettings( Options.SENSITIVITY);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public void setSensitivity( int sensitivity ) {
		putSettings(Options.SENSITIVITY, String.valueOf( sensitivity));
	}

	public int getRange() {
		String str = getSettings( Options.RANGE);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public void setRange( int range ) {
		putSettings(Options.RANGE, String.valueOf( range ));
	}

	public int getNrOfLeds() {
		String str = getSettings( Options.NR_OF_LEDS);
		return StringUtils.isEmpty(str)? DEFAULT_NR_OF_LEDS: Integer.parseInt(str);
	}

	public void setNrOfLeds( int range ) {
		putSettings(Options.RANGE, String.valueOf( range ));
	}

	public int getCounter() {
		String str = getSettings( Options.COUNTER);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public void setCounter( int count ) {
		putSettings(Options.COUNTER, String.valueOf( count ));
	}

	public int getTransparency() {
		String str = getSettings( Options.TRANSPARENCY);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public void setTransparency( int transp ) {
		putSettings(Options.TRANSPARENCY, String.valueOf( transp ));
	}

	public boolean isLogging() {
		return getBoolean(Options.OPTIONS.name(), (byte)0);
	}
	
	public void setLogging( boolean choice ) {
		this.store.setBoolean(Options.OPTIONS.name(), (byte)0, choice);
	}

	/**
	 * retrieve the boolean represented by a bit located on an int value
	 * @param name
	 * @param position
	 * @return
	 */
	protected boolean getBoolean( String name, int position ) {
		String str = this.store.getSettings( name);
		int options = StringUtils.isEmpty(str)?0: Integer.parseInt(str);
		int mask = 1<<position;
		return ((options&mask) > 0);
		
	}

	protected int getOptions() {
		String str = getSettings( Options.OPTIONS);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public String getSettings( Options key) {
		return this.store.getSettings(key.name());
	}

	protected void putSettings( Options key, String value) {
		this.store.putSettings(key.name(), value);
	}

	protected void putSettings( Options key, boolean value) {
		this.store.putSettings(key.name(), String.valueOf( value ));
	}

	protected boolean isChecked( Options option) {
		String str = getSettings( option );
		return StringUtils.isEmpty( str)?false: Boolean.parseBoolean(str );
	}

	public IRadarData toRadarData() {
		IRadarData data = new RadarData( getChoice(), getRange(), getSensitivity(), getOptions() );
		return data;
	}
}
