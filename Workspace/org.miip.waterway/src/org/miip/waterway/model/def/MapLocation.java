package org.miip.waterway.model.def;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;

public class MapLocation {

	//Ijssel
	public static final float DEF_LONGTITUDE = 6.15388f;
	public static final float DEF_LATITUDE = 52.24967f;

	//Heyplaat
	public static final float HEY_LONGTITUDE = 4.421760f;
	public static final float HEY_LATITUDE = 51.8984489440918f;

	//Rijnhaven
	public static final float RIJN_LONGTITUDE = 4.487553f;
	public static final float RIJN_LATITUDE = 51.903933f;

	public static final float DORDT_LONGTITUDE = 4.75f;
	public static final float DORDT_LATITUDE = 51.8158f;
	public static final String DORDT_NAAM = "Derde MerwedeHaven (Dordrecht)";

	public static final float UTRECHT_LONGTITUDE = 5.1072f;
	public static final float UTRECHT_LATITUDE = 52.0717f;
	public static final String UTRECHT_NAAM = "de Stadstuin, Europalaan";

	public enum Location{
		IJSSEL,
		HEIJPLAAT,
		RIJNHAVEN,
		DORDRECHT,
		UTRECHT;

		@Override
		public String toString() {
			String str = StringStyler.prettyString( super.toString() );
			switch( this ){
			case DORDRECHT:
				str = DORDT_NAAM;
				break;
			case UTRECHT:
				str = UTRECHT_NAAM;
				break;
			default:
				break;
			}
			return str;
		}

		public LatLng toLatLng() {
			LatLng latlng = null;
			switch( this ){
			case HEIJPLAAT:
				latlng = new LatLng( this.toString(), HEY_LATITUDE, HEY_LONGTITUDE );
				break;
			case IJSSEL:
				latlng = new LatLng( this.toString(), DEF_LATITUDE, DEF_LONGTITUDE );
				break;
			case RIJNHAVEN:
				latlng = new LatLng( this.toString(), RIJN_LATITUDE, RIJN_LONGTITUDE );
				break;
			case DORDRECHT:
				latlng = new LatLng( DORDT_NAAM, DORDT_LATITUDE, DORDT_LONGTITUDE );
				break;
			case UTRECHT:
				latlng = new LatLng( UTRECHT_NAAM, UTRECHT_LATITUDE, UTRECHT_LONGTITUDE );
				break;
			}
			return latlng;
		}

		public int getZoom() {
			int zoom = 17;
			switch( this ){
			case IJSSEL:
				zoom = 18;
				break;
			default:
				break;
			}
			return zoom;
		}

		public static String[] getNames(){
			String[] results = new String[ values().length ];
			int index = 0;
			for( Location location: values()){
				results[index++] = location.toString();
			}
			return results;
		}
	}
}
