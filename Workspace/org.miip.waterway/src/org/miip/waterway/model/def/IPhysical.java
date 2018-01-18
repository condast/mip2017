package org.miip.waterway.model.def;

import org.condast.commons.data.latlng.LatLng;
import org.condast.commons.strings.StringStyler;

public interface IPhysical {

	public enum ModelTypes{
		POINT,
		COURSE,
		BANK,
		WATERWAY,
		VESSEL;
		
		public String getImage(){
			/*
			IGoogleMapsImages.MarkerImages image = IGoogleMapsImages.MarkerImages.RED;
			switch( this ){
			case TERMINAL:
				image = IGoogleMapsImages.MarkerImages.BLUE;
				break;
			case PILOT:
				image = IGoogleMapsImages.MarkerImages.BROWN;
				break;
			case TUG_BOAT:
				image = IGoogleMapsImages.MarkerImages.YELLOW;
				break;
			default:
				break;
			}
			char id = this.name().charAt(0);
			return image.getImage(id);
			*/
			return null;
		}

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString());
		}	
	}

	String getId();

	LatLng getLocation();

	ModelTypes getType();
}