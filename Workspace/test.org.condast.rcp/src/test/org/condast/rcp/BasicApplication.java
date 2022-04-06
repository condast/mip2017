package test.org.condast.rcp;

import java.util.HashMap;
import java.util.Map;
import org.condast.commons.ui.entry.EntryFactoryBuilder;
import org.eclipse.rap.rwt.application.Application;
import org.eclipse.rap.rwt.application.ApplicationConfiguration;
import org.eclipse.rap.rwt.client.WebClient;

import test.org.condast.rcp.entries.BasicEntryPoint;
import test.org.condast.rcp.entries.FieldDesignerEntryPoint;


public class BasicApplication implements ApplicationConfiguration {

    public void configure(Application application) {
        Map<String, String> properties = new HashMap<String, String>();
        properties.put(WebClient.PAGE_TITLE, "Hello RAP");
        application.addEntryPoint("/hello", BasicEntryPoint.class, properties);
        application.addEntryPoint("/testfield", FieldDesignerEntryPoint.class, properties);
        
        EntryFactoryBuilder builder = new EntryFactoryBuilder( application, BasicApplication.class );
        builder.build();
    }

}
