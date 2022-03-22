package org.miip.waterway.rest;

import javax.servlet.Servlet;
import javax.ws.rs.ApplicationPath;

import org.condast.commons.messaging.http.AbstractServletWrapper;
import org.glassfish.jersey.server.ResourceConfig;
import org.glassfish.jersey.servlet.ServletContainer;
import org.miip.waterway.rest.resources.RadarResource;

public class RestServlet extends AbstractServletWrapper {

	//same as alias in plugin.xml
	public static final String S_CONTEXT_PATH = "miip2017";

	public RestServlet() {
		super( S_CONTEXT_PATH );
	}

	@Override
	protected Servlet onCreateServlet(String contextPath) {
		RestApplication resourceConfig = new RestApplication();
		return new ServletContainer(resourceConfig);
	}

	@ApplicationPath(S_CONTEXT_PATH)
	private class RestApplication extends ResourceConfig {

		//Loading classes is the safest way...
		//in equinox the scanning of packages may not work
		private RestApplication() {
			register( RadarResource.class );
		}
	}
}
