package org.miip.rwt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;
import org.miip.rwt.entry.BannerEntryPoint;
import org.miip.rwt.entry.MIIPEntryPoint;
import org.miip.rwt.entry.SettingsEntryPoint;

public class BasicApplication implements ApplicationConfiguration {

	private static final String S_ENTRY_POINT = "/home";
	private static final String S_MIIP_ENTRY_POINT = "/miip";
	private static final String S_BANNER_ENTRY_POINT = "/banner";
	private static final String S_SETTINGS_ENTRY_POINT = "/settings";
	
	private static final String S_MIIP_THEME = "miip.theme";
	private static final String S_THEME_CSS = "themes/theme.css";

    public void configure(Application application) {
        application.addStyleSheet( S_MIIP_THEME, S_THEME_CSS );
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "MIIP 2017 Collision Avoidance");
        properties.put( WebClient.THEME_ID, S_MIIP_THEME );

        application.setOperationMode( OperationMode.SWT_COMPATIBILITY );       
        application.addEntryPoint( S_ENTRY_POINT, BasicEntryPoint.class, properties);
        application.addEntryPoint( S_BANNER_ENTRY_POINT, SettingsEntryPoint.class, properties);
        application.addEntryPoint( S_MIIP_ENTRY_POINT, MIIPEntryPoint.class, properties);
   }
}
