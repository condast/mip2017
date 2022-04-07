package test.org.condast.rcp;

import java.util.HashMap;
import java.util.Map;
import org.condast.commons.ui.entry.EntryFactoryBuilder;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.application.Application.OperationMode;
import org.eclipse.rap.rwt.client.WebClient;

import test.org.condast.rcp.entries.BasicEntryPoint;
import test.org.condast.rcp.entries.FieldDesignerEntryPoint;


public class BasicApplication implements ApplicationConfiguration {

	public static final String S_ARNAC = "/arnac";
	private static final String S_ARNAC_THEME = "arnac.theme";
	private static final String S_THEME_CSS = "themes/theme.css";

    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Hello RAP");
		application.addStyleSheet( S_ARNAC_THEME, S_THEME_CSS );
		application.setOperationMode( OperationMode.SWT_COMPATIBILITY );
        application.addEntryPoint("/hello", BasicEntryPoint.class, properties);
        application.addEntryPoint("/testfield", FieldDesignerEntryPoint.class, properties);
        
        EntryFactoryBuilder builder = new EntryFactoryBuilder( application, BasicApplication.class );
        builder.build();
    }

}
