package org.miip.waterway.ui.swt.pond;

import org.condast.commons.data.latlng.LatLng;
import org.miip.waterway.model.IVessel;

public class VesselDrawData {

	public enum LineColours{
		BLACK(0),
		WHITE(1),
		BLUE(2),
		RED(3),
		YELLOW(4),
		GREEN(5),
		CYAN(6);

		private int index;
		
		private LineColours( int index ) {
			this.index = index;
		}
		
		
		public int getIndex() {
			return index;
		}


		@Override
		public String toString() {
			return this.name().toLowerCase();
		}	
		
		public static LineColours getColour( int index ) {
			for( LineColours colour: values() ) {
				if( colour.getIndex() == index )
					return colour;
			}
			return LineColours.BLACK;
		}
	}

	private IVessel vessel;
	
	//Is needed to sraw lines
	private LatLng current;
	
	private LineColours lineColour;
	
	public VesselDrawData( IVessel manager ) {
		this( manager, LineColours.BLUE);
	}
	
	public VesselDrawData( IVessel manager, LineColours lineColour ) {
		this.vessel = manager;
		this.current = manager.getLocation();
		this.lineColour = lineColour;
	}

	public IVessel getManager() {
		return vessel;
	}

	public LineColours getLineColour() {
		return lineColour;
	}

	public LatLng getCurrent() {
		return current;
	}

	public void setCurrent(LatLng current) {
		this.current = current;
	}	
}
