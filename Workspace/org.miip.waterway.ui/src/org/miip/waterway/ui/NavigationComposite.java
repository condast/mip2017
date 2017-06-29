package org.miip.waterway.ui;

import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.layout.GridLayout;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.condast.commons.authentication.core.IAuthenticationManager;
import org.eclipse.rap.rwt.RWT;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.miip.waterway.ui.xml.XMLFactoryBuilder;
import org.eclipse.swt.layout.GridData;

public class NavigationComposite extends Composite {
	private static final long serialVersionUID = 1L;

	private static final String RWT_NAVIGATION = "vg_navigation";

	private Button imageButton;
	private Text text;
	private NavigationToolBar navigationBar;
	
	public NavigationComposite(Composite parent, int style) {
		super(parent, style);
		this.createComposite(parent, style);
	}
	
	protected void createComposite( Composite parent, int style ){
		this.setData(RWT.CUSTOM_VARIANT, RWT_NAVIGATION );	
		setLayout(new GridLayout(1, false));	
		imageButton = new Button(this, SWT.FLAT);
		//imageButton.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, false, 1, 1));
		//imageButton.setData( RWT.MARKUP_ENABLED, Boolean.TRUE );
		imageButton.addSelectionListener( new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected(SelectionEvent e) {
				super.widgetSelected(e);
				navigationBar.setDefault( e );	
			}
		});
		GridData layoutData = new GridData(SWT.FILL, SWT.FILL, true, false);
		layoutData.heightHint = 120;
		layoutData.widthHint = 120;
		imageButton.setLayoutData(layoutData);
		
		text = new Text(this, SWT.NONE);
		text.setData(RWT.CUSTOM_VARIANT, RWT_NAVIGATION );
		text.setLayoutData(new GridData(SWT.CENTER, SWT.FILL, false, false, 1, 1));
		
        navigationBar = new NavigationToolBar( this, SWT.VERTICAL | SWT.RIGHT );
        navigationBar.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1));
 	}
	
	public void addItem( String itemName ){
		navigationBar.addToolItem(itemName);
	}
	
	public ToolItem getItem( XMLFactoryBuilder.Selection selection ){
		return navigationBar.getItem(selection );
	}

	public Image getImage(){
		return this.imageButton.getImage();
	}
	
	public void setImage( Image image ){
		this.imageButton.setImage(image);
	}
	
	public String getText(){
		return this.text.getText();
	}
	
	public void setText( String text ){
		this.text.setText(text);
	}
	
	public XMLFactoryBuilder.Selection getSelection(){
		return navigationBar.getSelection();
	}
	
	public void setSelection( XMLFactoryBuilder.Selection selection ){
		navigationBar.setSelection( selection );
	}
	
	public void setManager( IAuthenticationManager manager ){
		this.navigationBar.setManager(manager);
	}
	
	public void addSelectionListener( SelectionListener listener ){
		navigationBar.addSelectionListener(listener);
	}

	public void removeSelectionListener( SelectionListener listener ){
		navigationBar.removeSelectionListener(listener);
	}

	private static class NavigationToolBar extends ToolBar{
		private static final long serialVersionUID = 1L;

		private XMLFactoryBuilder.Selection selection;
		
		private SelectionListener listener = new SelectionAdapter(){
			private static final long serialVersionUID = 1L;

			@Override
			public void widgetSelected( final SelectionEvent event ) {
				//A selection change also always calls the currently selected tool item.
				//This selection event can be discarded
				if( Display.getCurrent().isDisposed() )
					return;
				ToolItem item = (ToolItem) event.widget;
				if( !item.getSelection())
					return;
				Display.getCurrent().asyncExec( new Runnable(){

					@Override
					public void run() {
						try{
							ToolItem item = (ToolItem) event.getSource();
							if( selected != null ){
								selected.setImage(null);
							}
							selected = item;
							//selected.setImage( LabelProviderImages.getInstance().getImage( Images.CHECK ));
							selection = ( XMLFactoryBuilder.Selection )item.getData();
							//prefs.putSettings( NavigationPreferences.Attributes.SELECTED_ITEM, selection.name() );
							switch( selection ){
							case REPORTS:
								//Rectangle rec = item.getBounds();
								//if( event.detail == SWT.ARROW ) {
								//Point point = toDisplay( event.x, event.y );
								//Point point = toDisplay(rec.x+rec.width,rec.y);
								//dossier_menu.setLocation( point );					
								//dossier_menu.setVisible( true );
								break;
								/*				   
						case LOG_IN:
							AuthenticationManager manager = AuthenticationManager.getInstance();
							manager.login();
							break;
								 */	
							case LOG_OFF:
								manager.logout();
								break;
							default:
								break;
							}
							for( SelectionListener list: listeners )
								list.widgetSelected( event );
						}
						catch( Exception ex ){
							ex.printStackTrace();
						}
					}		
				});
			}
		};

		private IAuthenticationManager manager;
		private Map<XMLFactoryBuilder.Selection, ToolItem> toolItems;
		private ToolItem selected;
		
		private Collection<SelectionListener> listeners;
		
		private NavigationToolBar( Composite parent, int style) {
			super(parent, style);
			this.toolItems = new HashMap<XMLFactoryBuilder.Selection, ToolItem>();
			this.listeners = new ArrayList<SelectionListener>();
			try{
				this.createComposite(parent, style);
			}
			catch( Exception ex ){
				ex.printStackTrace();
			}
		}
		
		protected void createComposite( Composite parent, int style  ){		
			
			this.setLayoutData(new GridData(SWT.FILL,SWT.FILL,true,true));
			this.setData(RWT.CUSTOM_VARIANT, RWT_NAVIGATION );	
			
			for( XMLFactoryBuilder.Selection selection: XMLFactoryBuilder.Selection.values() ){
				if( XMLFactoryBuilder.Selection.DEFAULT.equals( selection ))
					continue;
			}
			this.pack();
		}
		
		void addToolItem( String itemName ){
			ToolItem  item = new ToolItem(this, SWT.RADIO );
			item.setData(RWT.CUSTOM_VARIANT, RWT_NAVIGATION);
			item.setText( itemName );
			item.addSelectionListener( listener );
			toolItems.put( selection, item );			
		}
		
		void setDefault( SelectionEvent event ){
			setSelection( XMLFactoryBuilder.Selection.DEFAULT );
			for( SelectionListener listener: listeners )
				listener.widgetSelected( event );
		}

		ToolItem getItem( XMLFactoryBuilder.Selection selection ){
			return toolItems.get(selection );
		}
		
		void setManager(IAuthenticationManager manager) {
			this.manager = manager;
		}

		private void addSelectionListener(SelectionListener listener) {
			this.listeners.add( listener );
		}

		private void removeSelectionListener(SelectionListener listener) {
			this.listeners.remove( listener );
		}
		
		XMLFactoryBuilder.Selection getSelection(){
			return selection;
		}

		void setSelection( XMLFactoryBuilder.Selection selection ){
			if( this.selected != null ){
				this.selected.setImage(null);
				this.selected.setSelection(false);
			}
			this.selection = selection;
		}
	}
}
