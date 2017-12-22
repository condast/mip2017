package org.miip.waterway.rest.service;

import org.miip.waterway.radar.IRadarData.Choices;

public class CompositeSettings {

	private static CompositeSettings settings = new CompositeSettings();

	private Choices choice;
	private int range;
	private int sensitivity;
	
	private CompositeSettings() {
		this.choice = Choices.RADAR;
	}

	public static CompositeSettings getInstance(){
		return settings;
	}

	public Choices getChoice() {
		return choice;
	}

	public void setChoice(Choices choice) {
		this.choice = choice;
	}

	public int getRange() {
		return range;
	}

	public void setRange(int range) {
		this.range = range;
	}

	public int getSensitivity() {
		return sensitivity;
	}

	public void setSensitivity(int sensitivity) {
		this.sensitivity = sensitivity;
	}
}