package org.miip.waterway.ui.lang;

import org.condast.commons.i18n.Language;

public class MIIPLanguage extends Language {

	private static final String S_VG_LANGUAGE = "VGLanguage";
	
	private static MIIPLanguage language = new MIIPLanguage();
	
	public enum SupportedText{
		GUEST_MEMBERS;
	}
	
	private MIIPLanguage() {
		super( S_VG_LANGUAGE, "NL", "nl");
	}
	
	public static MIIPLanguage getInstance(){
		return language;
	}	
}
