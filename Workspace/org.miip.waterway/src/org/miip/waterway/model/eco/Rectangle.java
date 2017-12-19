package org.miip.waterway.model.eco;

public class Rectangle {

	private int x,y;
	private long length;
	private long width;
	
	public Rectangle( int x, int y, long length, long width ) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.length = length;
	}

	public int getXPos() {
		return x;
	}

	public int getYPos() {
		return y;
	}

	public long getLength() {
		return length;
	}

	public long getWidth() {
		return width;
	}

}
