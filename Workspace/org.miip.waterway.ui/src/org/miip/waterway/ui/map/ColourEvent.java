package org.miip.waterway.ui.map;

import java.util.EventObject;

import org.condast.commons.data.colours.RGBA;

public class ColourEvent extends EventObject {
	private static final long serialVersionUID = 1L;
	
	public enum Types{
		POINT,
		LINE,
		AREA;
	}
	
	private int index;
	private Types type;
	private RGBA[] colours;
	
	public ColourEvent(Object source, RGBA[] colours, int index ) {
		this( source, Types.LINE, colours, index );
	}
	
	public ColourEvent(Object source, Types type, RGBA[] colours, int index ) {
		super(source);
		this.type = type;
		this.colours = colours;
		this.index = index;
	}

	public int getIndex() {
		return index;
	}

	public Types getType() {
		return type;
	}

	public RGBA[] getColours() {
		return colours;
	}

}
