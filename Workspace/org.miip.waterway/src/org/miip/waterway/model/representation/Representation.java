package org.miip.waterway.model.representation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.miip.waterway.model.def.IPhysical;

public class Representation {
	
	private Map<Integer, ModelData> representations;

	public Representation() {
		representations = new TreeMap<Integer,ModelData>();
	}
	
	public void addModel( IPhysical model, int distance ){
		ModelData data = representations.get( distance);
		if( data == null ){
			data = new ModelData( model );
		}
		representations.put(distance, data);
	}

	public void removeModel( IPhysical model ){
		Iterator<Map.Entry<Integer, ModelData>> iterator = representations.entrySet().iterator();
		while( iterator.hasNext() ){
			Map.Entry<Integer, ModelData> entry = iterator.next();
			Collection<IPhysical> models = entry.getValue().models;
			if( models.contains( model )){
				models.remove( model);
				if( models.isEmpty())
					representations.remove( entry );
			}
		}
	}
	
	private class ModelData{
		private Collection<IPhysical> models;

		public ModelData( IPhysical model) {
			this();
			this.addModel(model);
		}
		
		public ModelData() {
			models = new ArrayList<IPhysical>();
		}
		
		public void addModel( IPhysical model ){
			this.models.add( model);
		}

		public void removeModel( IPhysical model ){
			this.models.remove( model);
		}
	}
}
