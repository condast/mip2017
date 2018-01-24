/*******************************************************************************
 * Copyright (c) 2014 Chaupal.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Apache License, Version 2.0
 * which accompanies this distribution, and is available at
 * http://www.apache.org/licenses/LICENSE-2.0.html
 *******************************************************************************/
package org.miip.rwt.xml;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.condast.commons.preferences.AbstractPreferenceStore;
import org.condast.commons.preferences.IPreferenceStore;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.swt.IStyle;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.ui.widgets.StatusBar;
import org.condast.commons.xml.AbstractXMLBuilder;
import org.condast.commons.xml.AbstractXmlHandler;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.miip.waterway.ui.NavigationComposite;
import org.miip.waterway.ui.images.MIIPImages;
import org.osgi.service.prefs.Preferences;
import org.xml.sax.Attributes;

public class XMLFactoryBuilder extends AbstractXMLBuilder<Widget, AbstractXMLBuilder.Selection> {
	
	private Class<?> clss;

	private Map<String, IPreferenceStore<String,String>> preferences;

	public XMLFactoryBuilder( Composite parent, Class<?> clss ) {
		super( new XMLHandler( clss, parent ), clss.getResource( S_DEFAULT_FOLDER + File.separator + S_DEFAULT_DESIGN_FILE) );
		this.clss = clss;
	}

	public XMLFactoryBuilder( Composite parent ) {
		this( parent, XMLFactoryBuilder.class );
	}

	public static String getLocation( String defaultLocation ){
		if( !StringUtils.isEmpty( defaultLocation ))
			return defaultLocation;
		return defaultLocation;
	}

	public Map<String,IPreferenceStore<String,String>> getPreferences(){
		return this.preferences;
	}

	protected Class<?> getClss() {
		return clss;
	}

	public Composite getRoot(){
		XMLHandler handler = (XMLHandler) super.getHandler();
		return handler.getRoot();
	}
	
	@Override
	public void build() {
		super.build();
		XMLHandler handler = (XMLHandler) getHandler();
		this.preferences = handler.getPreferences();
	}

	@Override
	public Widget[] getUnits() {
		return getHandler().getUnits();
	}
	
	private static class XMLHandler extends AbstractXmlHandler<Widget,XMLFactoryBuilder.Selection>{
		
		private Composite composite;
		private Composite root;
		private Class<?> clss;
		private LayoutDataBuilder databuilder;
		private Map<String, IPreferenceStore<String,String>> preferences;
		
		public XMLHandler( Class<?> clss, Composite parent ) {
			super( EnumSet.allOf( XMLFactoryBuilder.Selection.class));
			this.root = parent;
			this.clss = clss;
			this.preferences = new HashMap<String, IPreferenceStore<String,String>>();
		}

		public Composite getRoot(){
			return root;
		}
		
		protected Map<String, IPreferenceStore<String, String>> getPreferences() {
			return preferences;
		}

		@Override
		public Composite[] getUnits() {
			Composite[] comps = new Composite[1];
			comps[0] = composite;
			return comps;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected Widget parseNode( Selection node, Attributes attributes) {
			Widget  retval = null;
			String style_str = getAttribute( attributes, AttributeNames.STYLE );
			String name = getAttribute( attributes, AttributeNames.NAME );
			String url = getAttribute( attributes, AttributeNames.URL );
			String data = getAttribute( attributes, AttributeNames.DATA );
			//String height_str = getAttribute( attributes, AttributeNames.HEIGHT );
			//int height = StringUtils.isEmpty( height_str )? 50: Integer.parseInt( height_str );
			String size_str = getAttribute( attributes, AttributeNames.SIZE );
			int size = StringUtils.isEmpty( size_str )? 50: Integer.parseInt( size_str );
			int style = StringUtils.isEmpty(style_str)? SWT.NONE: IStyle.SWT.convert( style_str );
			boolean horizontal = SWT.HORIZONTAL == style;
			String class_str = getAttribute( attributes, AttributeNames.CLASS );

			//String width_str = getAttribute( attributes, AttributeNames.WIDTH );
			//int width = StringUtils.isEmpty( width_str )? 50: Integer.parseInt( width_str );

			Widget parent = ( super.getCurrentData() == null )? this.root: ( Widget)super.getCurrentData();
			Widget widget = null;
			Composite comp = null;
			switch( node ){
			case STORE:
				String id = getAttribute( attributes, AttributeNames.ID );
				preferences.put( id, new Store( id, name ));
				break;
			case FRONTEND:
				widget = new Composite((Composite) parent, IStyle.SWT.convert( style_str ) | SWT.BORDER );
				composite = (Composite) widget;
				composite.setLayout( new FillLayout( SWT.HORIZONTAL ));
				retval = composite;
				break;
			case LAYOUT:
				LayoutBuilder layoutBuilder = new LayoutBuilder();
				comp = (Composite) parent;
				layoutBuilder.setLayout(comp, attributes); 
				break;
			case NAVIGATION:
				widget = new NavigationComposite( (Composite) parent, style);
				GridData gd_nav = new GridData(SWT.FILL, SWT.FILL, horizontal, !horizontal);
				if( !StringUtils.isEmpty( size_str ))
					gd_nav.widthHint = size;
				Control control = (Control) widget;
				control.setLayoutData( gd_nav);
				//navcomp.addSelectionListener(listener);
				retval = (Composite) widget;
				break;
			case TABFOLDER:
				widget = new TabFolder( (Composite) parent, style);
				comp = (Composite) widget;
				comp.setLayout( new GridLayout());
				//GridData gd_tab = new GridData(SWT.FILL, SWT.FILL, horizontal, !horizontal);
				//if( !StringUtils.isEmpty( size_str ))
				//	gd_tab.widthHint = size;
				control = (Control) widget;
				//control.setLayoutData( gd_tab);
				retval = (Composite) widget;
				break;
			case COMPOSITE:
				if( parent instanceof Composite ){
					widget = createComposite( class_str, (Composite) parent, style );
				}else if( parent instanceof TabItem ){
					TabItem item = (TabItem) parent;
					TabFolder folder = (TabFolder)item.getParent();
					widget = createComposite( class_str, folder, style );
					item.setControl((Control) widget);
				}
				break;
			case INPUT:
				if( !StringUtils.isEmpty( class_str )) {
					Object input = createObject(clss, class_str);
					IInputWidget<Object> inpwid = (IInputWidget<Object>) parent;
					inpwid.setInput(input);
				}
				widget = parent;
				break;
			case LAYOUT_DATA:
				this.databuilder = new LayoutDataBuilder();
				comp = (Composite) parent;
				comp.setLayoutData(this.databuilder.getData());
				break;
			case HORIZONTAL:
				this.databuilder.setGridLayoutData(node, attributes);
				break;
			case VERTICAL:
				this.databuilder.setGridLayoutData(node, attributes);
				break;
			case IMAGE:
				NavigationComposite navcomp = (NavigationComposite) super.getCurrentData();
				Image image = MIIPImages.getImageFromResource( navcomp.getDisplay(), this.getClass(), url );
				navcomp.setImage( image );
				break;
			case ITEM:
				if( super.getCurrentData() instanceof NavigationComposite ){
					navcomp = (NavigationComposite) parent;
					String select_str = getAttribute( attributes, AttributeNames.SELECT );
					boolean select=  StringUtils.isEmpty( select_str )?false: Boolean.parseBoolean( select_str );
					String link = getAttribute( attributes, AttributeNames.LINK );
					navcomp.addItem( name, link, select );
				}else if( super.getCurrentData() instanceof TabFolder ){
					TabFolder tabcomp = (TabFolder) super.getCurrentData();
					TabItem item = new TabItem( tabcomp, style );
					item.setText(name);
					widget = item;
				}
				break;
			case BODY:
				comp = new Composite((Composite) parent, IStyle.SWT.convert( style_str ));
				comp.setLayout(new FillLayout());
				widget = comp;
				GridData gd_body = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
				gd_body.horizontalIndent = 0;
				gd_body.verticalIndent = 0;
				control = (Control) widget;
				control.setLayoutData( gd_body);
				break;
			case STATUS_BAR:
				StatusBar bar = new StatusBar((Composite) parent, IStyle.SWT.convert( style_str ));
				widget = bar;
				if( !StringUtils.isEmpty( name ))				
					bar.setLabelText( name );
				widget = bar;
				bar.setLayout(new FillLayout(SWT.HORIZONTAL));
				//GridData gd_text_status = new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1);
				//gd_text_status.heightHint = height;
				//bar.setLayoutData(gd_text_status);
				break;
			default:
				break;
			}
			if( widget != null ){
				if( !StringUtils.isEmpty( data ))
					widget.setData( RWT.CUSTOM_VARIANT, data );
				retval = widget;
			}
			return retval;
		}

		@Override
		protected void completeNode(Enum<Selection> node) {
			// TODO Auto-generated method stub
			
		}

		@Override
		protected void addValue(Enum<Selection> node, String value) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public Composite getUnit(String id) {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	@SuppressWarnings("unchecked")
	protected static Composite createComposite( String className, Composite parent, int style ){
		if( StringUtils.isEmpty( className ))
			return null;
		Composite composite = null;
		try{
			Class<Composite> cls = (Class<Composite>) Class.forName( className );
			Constructor<Composite> cons = cls.getConstructor( Composite.class, Integer.TYPE);          
			composite = cons.newInstance( parent, style );
		}
		catch( Exception ex ){
			ex.printStackTrace();
		}
		return composite;
	}

	private static class LayoutBuilder{

		private enum Layout{
			FILL_LAYOUT,
			GRID_LAYOUT
		}

		private enum LayoutAttributes{
			NUM_COLUMS,
			SPACE_EVENLY;
		}

		public void setLayout( Composite parent, Attributes attributes ){
			String name = getAttribute( attributes, AttributeNames.NAME );
			Layout layout = StringUtils.isEmpty( name )? Layout.FILL_LAYOUT: 
				Layout.valueOf( StringStyler.styleToEnum( name ));
			String numcol_str = getAttribute(attributes, LayoutAttributes.NUM_COLUMS );
			int numcol = StringUtils.isEmpty( numcol_str)?1: Integer.parseInt( numcol_str);
			String space_str = getAttribute(attributes, LayoutAttributes.SPACE_EVENLY );
			boolean space = StringUtils.isEmpty( space_str)?false: Boolean.parseBoolean( space_str);
			switch( layout ){
			case GRID_LAYOUT:
				parent.setLayout( new GridLayout( numcol, space ));
				break;
			default:
				String type_str = getAttribute(attributes, LayoutAttributes.SPACE_EVENLY );
				int type = StringUtils.isEmpty( type_str)?0: Integer.parseInt( type_str);
				parent.setLayout( new FillLayout( type ));
				break;
			}

		}
	}

	private static class LayoutDataBuilder{

		private enum LayoutAttributes{
			ALIGN,
			GRAB_EXCESS,
			SPAN;
		}

		private GridData data;

		protected LayoutDataBuilder() {
			this( new GridData() );
		}
		
		protected LayoutDataBuilder( GridData data) {
			this.data = data;
		}

		public GridData getData() {
			return data;
		}

		private void setGridLayoutData( Selection layout, Attributes attributes ){
			String align_str = getAttribute( attributes, LayoutAttributes.ALIGN );
			Integer align = StringUtils.isEmpty( align_str )? SWT.FILL: 
				IStyle.SWT.convert( StringStyler.styleToEnum( align_str )); 
			String grab_excess_str = getAttribute( attributes, LayoutAttributes.GRAB_EXCESS );
			boolean grab_excess = StringUtils.isEmpty( grab_excess_str)? false: 
				Boolean.parseBoolean( grab_excess_str);
			String span_str = getAttribute( attributes, LayoutAttributes.SPAN );
			int span = StringUtils.isEmpty( span_str )? 0: Integer.parseInt( span_str );
			switch( layout ){
			case HORIZONTAL:
				data.grabExcessHorizontalSpace = grab_excess;
				data.horizontalAlignment = align;
				data.horizontalSpan = span;
				break;
			default:
				data.grabExcessVerticalSpace = grab_excess;
				data.verticalAlignment = align;
				data.verticalSpan = span;
				break;
			}
		}
	}

	public static class Store extends AbstractPreferenceStore{

		protected Store( String bundleName, String category) {
			super(bundleName);
			super.addChild( category );
		}
	
		public Store(Preferences preferences) {
			super(preferences);
		}

		@Override
		protected IPreferenceStore<String, String> onAddChild(Preferences preferences) {
			return new Store( preferences );
		}

		@Override
		public void setBoolean(String name, int position, boolean choice) {
			super.setBoolean(name, position, choice);
		}		
	}
}