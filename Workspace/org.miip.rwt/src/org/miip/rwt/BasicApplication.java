package org.miip.rwt;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;


public class BasicApplication implements ApplicationConfiguration {

	private static final String S_ENTRY_POINT = "/home";

    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "MIIP 2017 Collision Avoidance");

        application.setOperationMode( OperationMode.SWT_COMPATIBILITY );       
        application.addEntryPoint( S_ENTRY_POINT, BasicEntryPoint.class, properties);
   }

}
