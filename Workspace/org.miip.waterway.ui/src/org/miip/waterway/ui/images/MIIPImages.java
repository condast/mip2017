/*******************************************************************************
 * Copyright (c) 2014 Chaupal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *******************************************************************************/
package org.miip.waterway.ui.images;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.ui.image.AbstractImages;
import org.eclipse.swt.graphics.Image;

public class MIIPImages extends AbstractImages{

	public static final String BUNDLE_ID = "org.miip.waterway.ui";
	
	private Map<String, String> imageMap;
		
	private static MIIPImages images = new MIIPImages();
	
	private MIIPImages() {
		super( "", BUNDLE_ID );
		imageMap = new HashMap<String, String>();
	}

	/**
	 * Get an instance of this map
	 * @return
	 */
	public static MIIPImages getInstance(){
		return images;
	}
	
	@Override
	public void initialise(){
	}
	
	public void setImage( String name, String url ){
		super.setImage(name);
		this.imageMap.put(name, url );
	}
	/**
	 * Get the image
	 * @param desc
	 * @return
	 */
	public static Image getImage( String name ){
		return getInstance().getImageFromName( name);
	}
}