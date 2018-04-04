package org.miip.waterway.rest.store;

import org.condast.commons.preferences.AbstractPreferenceStore;
import org.condast.commons.preferences.IPreferenceStore;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.miip.waterway.radar.IRadarData;
import org.miip.waterway.radar.IRadarData.Choices;
import org.miip.waterway.rest.Activator;
import org.miip.waterway.rest.model.RadarData;
import org.osgi.service.prefs.Preferences;

public class RadarOptions extends AbstractPreferenceStore{

	public static final int DEFAULT_NR_OF_LEDS = 24;
	
	public enum Options{
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
	
	public RadarOptions( String vesselName) {
		super( Activator.BUNDLE_ID);
		super.addChild( vesselName);
		putSettings( Options.TOKEN, getToken(vesselName.getBytes()));
	}

	protected RadarOptions(Preferences preferences) {
		super(preferences);
	}
	
	private final String getToken( byte[] vesselName ) {
		int token = 0;
		for( byte bt: vesselName ) {
			token+= bt;
		}
		return String.valueOf( token );
	}
	
	public Choices getChoice() {
		String str = super.getSettings( Options.CHOICE);
		Choices choice = StringUtils.isEmpty(str)?Choices.RADAR: Choices.valueOf(str);
		return choice;
	}

	public void setChoice( Choices choice ) {
		super.putSettings(Options.CHOICE, choice.name());
	}
	
	public int getSensitivity() {
		String str = super.getSettings( Options.SENSITIVITY);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public void setSensitivity( int sensitivity ) {
		super.putSettings(Options.SENSITIVITY, String.valueOf( sensitivity));
	}

	public int getRange() {
		String str = super.getSettings( Options.RANGE);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public void setRange( int range ) {
		super.putSettings(Options.RANGE, String.valueOf( range ));
	}

	public int getNrOfLeds() {
		String str = super.getSettings( Options.NR_OF_LEDS);
		return StringUtils.isEmpty(str)? DEFAULT_NR_OF_LEDS: Integer.parseInt(str);
	}

	public void setNrOfLeds( int range ) {
		super.putSettings(Options.RANGE, String.valueOf( range ));
	}

	public int getCounter() {
		String str = super.getSettings( Options.COUNTER);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public void setCounter( int count ) {
		super.putSettings(Options.COUNTER, String.valueOf( count ));
	}

	public int getTransparency() {
		String str = super.getSettings( Options.TRANSPARENCY);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public void setTransparency( int transp ) {
		super.putSettings(Options.TRANSPARENCY, String.valueOf( transp ));
	}

	public boolean isLogging() {
		return getBoolean(Options.OPTIONS.name(), (byte)0);
	}
	
	public void setLogging( boolean choice ) {
		super.setBoolean(Options.OPTIONS.name(), (byte)0, choice);
	}

	protected int getOptions() {
		String str = super.getSettings( Options.OPTIONS);
		return StringUtils.isEmpty(str)?0: Integer.parseInt(str);
	}

	public String getSettings( Options key) {
		return super.getSettings(key);
	}

	protected void putSettings( Options key, String value) {
		super.putSettings(key, value);
	}

	protected void putSettings( Options key, boolean value) {
		super.putSettings(key, String.valueOf( value ));
	}

	protected boolean isChecked( Options option) {
		String str = getSettings( option );
		return StringUtils.isEmpty( str)?false: Boolean.parseBoolean(str );
	}

	public IRadarData toRadarData() {
		IRadarData data = new RadarData( getChoice(), getRange(), getSensitivity(), getOptions() ); 
		return data;
	}
	
	@Override
	protected IPreferenceStore<String, String> onAddChild(Preferences preferences) {
		return new RadarOptions( preferences );
	}	
	
	public static RadarOptions create( String vesselName ) {
		return new RadarOptions( vesselName ); 
	}
}
