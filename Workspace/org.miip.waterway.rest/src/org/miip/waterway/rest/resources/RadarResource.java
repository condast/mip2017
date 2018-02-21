package org.miip.waterway.rest.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Vector;
import org.condast.commons.log.LogFactory;
import org.condast.commons.messaging.rest.ResponseCode;
import org.condast.commons.strings.StringUtils;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.model.def.IReferenceEnvironment;
import org.miip.waterway.radar.IRadarData;
import org.miip.waterway.rest.model.RestRadar;
import org.miip.waterway.rest.service.Dispatcher;
import org.miip.waterway.rest.store.RadarOptions;
import org.miip.waterway.sa.ISituationalAwareness;

import com.google.gson.Gson;

@Path("/sa")
public class RadarResource{
		
	private String S_ENVIRONMENT = "org.miip.pond.model.PondEnvironment";
	private Logger logger = Logger.getLogger( this.getClass().getName());

	private Dispatcher dispatcher = Dispatcher.getInstance();
	private RadarOptions settings;
	
	public RadarResource() {
		super();
		settings = dispatcher.getOptions();
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/setup")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setupRadar( @QueryParam("id") String id, @QueryParam("token") String token ) {
		try {
			logger.info("Query for Radar " + id );
			IRadarData data = settings.toRadarData();
			Gson gson = new Gson();
			String result = gson.toJson(data);
			return Response.ok( result ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/log")
	@Produces(MediaType.APPLICATION_JSON)
	public Response log( @QueryParam("id") String id, @QueryParam("token") String token, @QueryParam("msg") String message ) {
		try {
			Level restLevel = LogFactory.createLogLevel(id, Level.SEVERE.intValue() - 1); 
			logger.log( restLevel, message );
			OptionsData data = new OptionsData( settings.isLogging());
			Gson gson = new Gson();
			String result = gson.toJson(data);
			return Response.ok( result ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	@POST
	@Path("/log")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes("application/x-www-form-urlencoded") 
	public Response logPost( @QueryParam("id") String id, @QueryParam("token") String token, @FormParam("msg") String message ) {
		try {
			Level restLevel = LogFactory.createLogLevel(id, Level.SEVERE.intValue() - 1); 
			logger.log( restLevel, message );
			OptionsData data = new OptionsData( settings.isLogging());
			Gson gson = new Gson();
			String result = gson.toJson(data);
			return Response.ok( result ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/radar")
	@Produces(MediaType.APPLICATION_JSON)
	//@Consumes("application/x-www-form-urlencoded") 
	public Response getRadar( @QueryParam("id") String id, @QueryParam("token") String token, @QueryParam("msg") String leds ) {
		try {
			logger.info("Query for Radar " + id );
			IReferenceEnvironment<IPhysical> env = (IReferenceEnvironment<IPhysical>) Dispatcher.getInstance().getEnvironment( S_ENVIRONMENT ); 
			IVessel reference = (IVessel) env.getInhabitant();
			ISituationalAwareness<IPhysical, ?> sa = reference.getSituationalAwareness();
			if( sa == null )
				return Response.ok( ResponseCode.RESPONSE_EMPTY ).build();
			RestRadar radar = new RestRadar();
			radar.setInput(sa);
			SequentialBinaryTreeSet<Vector<Double>>  data = radar.getBinaryView();
			if(( data == null ) ||  data.isEmpty())
				return Response.ok( ResponseCode.RESPONSE_EMPTY ).build();

			int scale = StringUtils.isEmpty( leds )? 1: Integer.parseInt( leds );
			Iterator<Vector<Double>> iterator = data.fill( scale).iterator();
			Collection<RGB> rgbs = new ArrayList<RGB>();
			while( iterator.hasNext() ){
				Map.Entry<Double, Double> entry = iterator.next();
				rgbs.add( getColour(sa, (int)entry.getKey().doubleValue(), entry.getValue()));
			}
			Gson gson = new Gson();
			return Response.ok( gson.toJson( rgbs )).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	protected RGB getColour( ISituationalAwareness<IPhysical,?> sa, int angle, double distance ){
		if( sa == null)
			return new RGB( angle, 0, 0, 0, 0 );
	
		RestRadar radar = new RestRadar();
		radar.setInput(sa);
		if( distance <= radar.getSensitivity() )
			return new RGB( angle, 255, 0, 0, 0 );
		if( distance > radar.getRange())
			return new RGB( angle, 255, 0, 0, 255 );
		return getLinearColour( angle, (int) distance, (int) radar.getRange(), (int)radar.getSensitivity() );
	}
	
	private RGB getLinearColour( int angle, int distance, int range, int sensitivity ){
		boolean far = ( distance > ( range - sensitivity ));
		int red = far? 50: (int)( 255 * ( 1 - distance/range ));
		int green = far? 255: (int)( 255 * distance/range );
		int blue = 50;
		return new RGB( angle, red, green, blue, 0 );
	}

	private class RGB{
		private int a;
		private int r,g,b;
		private int t;

		RGB( int angle, int r, int g, int b, int transparency) {
			super();
			this.a = angle;
			this.r = r;
			this.g = g;
			this.b = b;
			this.t= transparency;
		}

		@Override
		public String toString() {
			StringBuffer buffer = new StringBuffer();
			buffer.append("RGB[" + a + ":]= {");
			buffer.append(String.valueOf(r) + ", ");
			buffer.append(String.valueOf(g) + ", ");
			buffer.append(String.valueOf(b) + ", ");
			buffer.append(String.valueOf(t) + "}");
			return buffer.toString();
		}
		
		
	}
	
	private class OptionsData{
		private boolean o;

		public OptionsData( boolean o) {
			super();
			this.o = o;
		}

		@SuppressWarnings("unused")
		public boolean getOptions() {
			return o;
		}
	}
}