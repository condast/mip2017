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
import org.condast.commons.autonomy.ca.ICollisionAvoidance;
import org.condast.commons.autonomy.model.IPhysical;
import org.condast.commons.autonomy.model.IReferenceEnvironment;
import org.condast.commons.autonomy.sa.ISituationalAwareness;
import org.condast.commons.autonomy.sa.radar.VesselRadarData;
import org.condast.commons.log.LogFactory;
import org.condast.commons.messaging.rest.ResponseCode;
import org.miip.waterway.model.IVessel;
import org.miip.waterway.radar.ILEDRadarData;
import org.miip.waterway.radar.LEDRadarData;
import org.miip.waterway.radar.RadarOptions;
import org.miip.waterway.radar.RestRadar;
import org.miip.waterway.rest.core.Dispatcher;

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
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response setupRadar( @QueryParam("id") String id, @QueryParam("token") String token ) {
		try {
			logger.fine("Setup Query for Radar " + id );
			RadarOptions settings = dispatcher.getOptions();
			if( settings == null )
				return Response.noContent().build();
			ILEDRadarData data = settings.toRadarData();
			Gson gson = new Gson();
			String result = gson.toJson(data, LEDRadarData.class);
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
	@GET
	@Path("/radar")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response getRadar( @QueryParam("id") String id, @QueryParam("token") long token, @QueryParam("leds") int leds, @QueryParam("range") double range, @QueryParam("sense") double sensitivity ) {
		Response response = Response.noContent().build();
		try {
			logger.info("Query for Radar " + id + ", nr of leds: " + leds );
			IReferenceEnvironment<IVessel, IPhysical> env = (IReferenceEnvironment<IVessel, IPhysical>) Dispatcher.getInstance().getActiveEnvironment();
			if( env == null )
				return response;

			IVessel reference = env.getInhabitant();
			if( reference == null )
				return response;
			ICollisionAvoidance<IVessel, VesselRadarData> ca = reference.getCollisionAvoidance();
			ISituationalAwareness<VesselRadarData> sa = ca.getSituationalAwareness( ICollisionAvoidance.DefaultSituationalAwareness.VESSEL_RADAR.toString());
			if( sa == null ) {
				response = Response.ok( ResponseCode.RESPONSE_EMPTY ).build();
				return response;
			}
			int nrOfLeds = leds;
			RadarOptions options = dispatcher.getOptions();
			if( sensitivity > 0 )
				options.setSensitivity(sensitivity);

			if( range > 0 )
				options.setRange(range);
			RestRadar radar = new RestRadar( options, nrOfLeds, sa );
			List<RestRadar.MIIPRadarData> rgbs = radar.drawField();
			Gson gson = new Gson();
			int angle = options.getCounter();
			response = Response.ok( gson.toJson( rgbs.get( angle ))).build();
			angle +=1;
			angle %= nrOfLeds;
			options.setCounter( angle );
		}
		catch( Exception ex ) {
			ex.printStackTrace();
			response = Response.serverError().build();
		}
		return response;
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