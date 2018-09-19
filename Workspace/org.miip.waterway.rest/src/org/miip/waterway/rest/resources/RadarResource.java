package org.miip.waterway.rest.resources;

import java.util.List;
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

import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.log.LogFactory;
import org.condast.commons.messaging.rest.ResponseCode;
import org.condast.commons.strings.StringUtils;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.radar.IRadarData;
import org.miip.waterway.radar.RadarData;
import org.miip.waterway.radar.RadarOptions;
import org.miip.waterway.rest.core.Dispatcher;
import org.miip.waterway.rest.model.RestRadar;

import com.google.gson.Gson;

@Path("/sa")
public class RadarResource{
		
	private Logger logger = Logger.getLogger( this.getClass().getName());

	private Dispatcher dispatcher = Dispatcher.getInstance();
		
	public RadarResource() {
		super();
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/setup")
	@Produces(MediaType.APPLICATION_JSON)
	public Response setupRadar( @QueryParam("id") String id, @QueryParam("token") String token ) {
		try {
			logger.info("Query for Radar " + id );
			RadarOptions settings = dispatcher.getOptions();
			if( settings == null )
				return Response.noContent().build();
			IRadarData data = settings.toRadarData();
			Gson gson = new Gson();
			String result = gson.toJson(data, RadarData.class);
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
			RadarOptions settings = dispatcher.getOptions();
			if( settings == null )
				return Response.noContent().build();
			logger.log( restLevel, message );
			OptionsData options = new OptionsData( settings.isLogging());
			Gson gson = new Gson();
			String result = gson.toJson( options, OptionsData.class);
			return Response.ok( result ).build();
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
		}
	}

	// This method is called if TEXT_PLAIN is request
	@SuppressWarnings("unchecked")
	@GET
	@Path("/radar")
	@Produces(MediaType.APPLICATION_JSON)
	//@Consumes("application/x-www-form-urlencoded") 
	public Response getRadar( @QueryParam("id") String id, @QueryParam("token") String token, @QueryParam("msg") String leds ) {
		try {
			logger.info("Query for Radar " + id );
			IReferenceEnvironment<IVessel, IPhysical> env = (IReferenceEnvironment<IVessel, IPhysical>) Dispatcher.getInstance().getActiveEnvironment(); 
			Response response = null;
			if( env == null )
				return Response.noContent().build();
			IVessel reference = (IVessel) env.getInhabitant();
			if( reference == null )
				return Response.noContent().build();
			ISituationalAwareness<IVessel,IPhysical> sa = reference.getSituationalAwareness();
			if( sa == null )
				return Response.ok( ResponseCode.RESPONSE_EMPTY ).build();
			int nrOfLeds = StringUtils.isEmpty(leds)?0: Integer.parseInt(leds);
			RadarOptions options = dispatcher.getOptions();
			RestRadar radar = new RestRadar( options, nrOfLeds, sa );
			List<RestRadar.RadarData> rgbs = radar.drawField();
			Gson gson = new Gson();
			int angle = options.getCounter();
			response = Response.ok( gson.toJson( rgbs.get( angle ))).build();
			angle +=1;
			angle %= nrOfLeds;
			options.setCounter( angle );
			return response;
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			return Response.serverError().build();
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