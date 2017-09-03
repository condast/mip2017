package org.miip.waterway.rest.resources;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.latlng.Vector;
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.rest.Dispatcher;
import org.miip.waterway.rest.model.Radar;
import org.miip.waterway.sa.SituationalAwareness;

import com.google.gson.Gson;

@Path("/sa")
public class RadarResource{

	private Logger logger = Logger.getLogger( this.getClass().getName());

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/radar")
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRadar() {
		logger.info("Query for Radar" );
		IMIIPEnvironment ce = Dispatcher.getInstance().getEnvironment();
		SituationalAwareness sa = ce.getSituationalAwareness();
		StringBuffer buffer = new StringBuffer();
		if( sa == null )
			return createResponse(buffer.toString());
		SequentialBinaryTreeSet<Vector<Integer>>  data = sa.getBinaryView();
		List<Vector<Integer>> vectors = data.getValues(5);
		if( vectors.isEmpty() )
			return createResponse(buffer.toString());
		Collections.sort( vectors, new VectorComparator());
		
		for( Vector<Integer> entry: vectors ){
			logger.fine("Angle: " + entry.getKey() + ", distance: " + entry.getValue() );
		}
		Radar radar = new Radar();
		radar.setInput(sa);
		Gson gson = new Gson();
		return createResponse( String.valueOf( gson.toJson( radar.getColours()) ));
	}

	private class VectorComparator implements Comparator<Vector<Integer>> {

		@Override
		public int compare(Vector<Integer> arg0, Vector<Integer> arg1) {
			return (int)( arg0.getValue() - arg1.getValue());
		}
	}

	private static Response createResponse( String message ){
		ResponseBuilder builder = Response.ok( message );

		builder.status(200)
		.header("Access-Control-Allow-Origin", "*")
		.header("Access-Control-Allow-Headers", "origin, content-type, accept, authorization")
		.header("Access-Control-Allow-Credentials", "true")
		.header("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS, HEAD")
		.header("Access-Control-Max-Age", "1209600");
		return builder.build();
	}
}