package org.miip.waterway.rest.resources;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import org.condast.commons.data.binary.SequentialBinaryTreeSet;
import org.condast.commons.data.latlng.Vector;
import org.condast.commons.log.LogFactory;
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
	public String setupRadar( @QueryParam("id") String id, @QueryParam("token") String token ) {
		logger.info("Query for Radar " + id );
		IRadarData[] data = settings.toRadarData();
		Gson gson = new Gson();
		String result = gson.toJson(data);
		return result;
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/log")
	@Produces(MediaType.TEXT_PLAIN)
	public String log( @QueryParam("id") String id, @QueryParam("token") String token, String message ) {
		if( !settings.isLogging() )
			return Boolean.FALSE.toString();
		Level restLevel = LogFactory.createLogLevel(id, Level.SEVERE.intValue() - 1); 
		logger.log( restLevel, message );
		return Boolean.TRUE.toString();
	}

	// This method is called if TEXT_PLAIN is request
	@GET
	@Path("/radar")
	@Produces(MediaType.APPLICATION_JSON)
	public String getRadar( @QueryParam("id") String id, @QueryParam("token") String token, @QueryParam("leds") String leds ) {
		logger.info("Query for Radar " + id );
		IReferenceEnvironment<IPhysical> env = (IReferenceEnvironment<IPhysical>) Dispatcher.getInstance().getEnvironment( S_ENVIRONMENT ); 
		IVessel reference = (IVessel) env.getInhabitant();
		ISituationalAwareness<IPhysical, ?> sa = reference.getSituationalAwareness();
		if( sa == null )
			return "[]";
		RestRadar radar = new RestRadar();
		radar.setInput(sa);
		SequentialBinaryTreeSet<Vector<Double>>  data = radar.getBinaryView();
		if(( data == null ) ||  data.isEmpty())
			return "[]";
		
		int scale = StringUtils.isEmpty( leds )? 0: Integer.parseInt( leds );
		Iterator<Vector<Double>> iterator  = data.getValues( data.scale( scale ) -1).iterator();
		Collection<RGB> rgbs = new ArrayList<RGB>();
		while( iterator.hasNext() ){
			Map.Entry<Double, Double> entry = iterator.next();
			rgbs.add( getColour(sa, (int)entry.getKey().doubleValue(), entry.getValue()));
		}
		Gson gson = new Gson();
		return gson.toJson( rgbs.toArray( new RGB[ rgbs.size()]));
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

	@SuppressWarnings("unused")
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
	}
}