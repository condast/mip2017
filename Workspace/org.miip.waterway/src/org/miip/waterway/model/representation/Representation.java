package org.miip.waterway.model.representation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import org.miip.waterway.model.def.IModel;

public class Representation {
	
	private Map<Integer, ModelData> representations;

	public Representation() {
		representations = new TreeMap<Integer,ModelData>();
	}
	
	public void addModel( IModel model, int distance ){
		ModelData data = representations.get( distance);
		if( data == null ){
			data = new ModelData( model );
		}
		representations.put(distance, data);
	}

	public void removeModel( IModel model ){
		Iterator<Map.Entry<Integer, ModelData>> iterator = representations.entrySet().iterator();
		while( iterator.hasNext() ){
			Map.Entry<Integer, ModelData> entry = iterator.next();
			Collection<IModel> models = entry.getValue().models;
			if( models.contains( model )){
				models.remove( model);
				if( models.isEmpty())
					representations.remove( entry );
			}
		}
	}
	
	private class ModelData{
		private Collection<IModel> models;

		public ModelData( IModel model) {
			this();
			this.addModel(model);
		}
		
		public ModelData() {
			models = new ArrayList<IModel>();
		}
		
		public void addModel( IModel model ){
			this.models.add( model);
		}

		public void removeModel( IModel model ){
			this.models.remove( model);
		}
	}
}
