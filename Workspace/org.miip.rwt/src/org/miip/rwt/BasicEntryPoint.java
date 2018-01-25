package org.miip.rwt;

import org.condast.commons.xml.AbstractXMLBuilder;
import org.condast.commons.xml.BuildEvent;
import org.condast.commons.xml.IBuildListener;
import org.condast.commons.preferences.AbstractPreferenceStore;
import org.condast.commons.preferences.IPreferenceStore;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.activate.ActivationEvent;
import org.condast.commons.ui.activate.IActivateListener;
import org.condast.commons.ui.logger.LogComposite;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.xml.AbstractXMLBuilder.Selection;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.Widget;
import org.miip.rwt.service.Dispatcher;
import org.miip.rwt.xml.XMLFactoryBuilder;
import org.miip.waterway.environment.IEnvironment;
import org.miip.waterway.model.def.IPhysical;
import org.osgi.service.prefs.Preferences;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	private static final String S_REST_STORE = "org.miip.waterway.rest";
	
	public enum Composites{
		MIIP_COMPOSITE,
		LOG_COMPOSITE;
	}
	
	public enum Options{
		OPTIONS,
		LOG;
	}
		
	private LogComposite lc;
	
	private IActivateListener alistener = new IActivateListener() {

		@Override
		public void notifyActivationChange(ActivationEvent event) {
			Store store = null;
			try {
				store = new Store( Options.LOG.name(), S_REST_STORE);
				store.setBoolean(Options.LOG, 0, event.isActivated());
			}
			finally {
				store.close();
			}
		}		
	};
	
	private IBuildListener<Widget> listener = new IBuildListener<Widget>(){

		@SuppressWarnings("unchecked")
		@Override
		public void notifyTestEvent(BuildEvent<Widget> event) {
			if( !Selection.isOfSelection(event.getName()))
				return;
			switch( Selection.valueOf( event.getName())) {
			case INPUT:
				String use_str = event.getAttribute(AbstractXMLBuilder.AttributeNames.USE);
				if(!StringUtils.isEmpty(use_str) && ( event.getData() instanceof IInputWidget)) {
					IInputWidget<IEnvironment<IPhysical>> widget = (IInputWidget<IEnvironment<IPhysical>>) event.getData();
					widget.setInput( Dispatcher.getInstance().getEnvironment( use_str ));
				}
				break;
			case ITEM:
				break;
			case COMPOSITE:
				String name = event.getAttribute(AbstractXMLBuilder.AttributeNames.NAME);
				if( StringUtils.isEmpty(name))
					return;
				Composites cmp = Composites.valueOf( StringStyler.styleToEnum( name ));
				switch( cmp ){
				case MIIP_COMPOSITE:
					break;
				case LOG_COMPOSITE:
					lc = (LogComposite) event.getData();
					lc.addActivateListener(alistener);
					break;
				default:
						break;
				}
			break;
			case TABFOLDER:
				TabFolder folder = (TabFolder) event.getData();
				folder.addSelectionListener( new SelectionAdapter() {
					private static final long serialVersionUID = 1L;

					@Override
					public void widgetSelected(SelectionEvent e) {
						try {
							TabItem item = (TabItem) e.item;
							if( lc == null )
								return;
							boolean activate = lc.equals( item.getControl() );
							lc.activate(activate);
						}
						catch( Exception ex ) {
							ex.printStackTrace();
						}
					}
				});

				break;
			default:
				break;
			}
		}
	};
	
	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new FillLayout());       
        XMLFactoryBuilder builder = new XMLFactoryBuilder( parent, this.getClass());
        builder.addListener(listener);
        builder.build();
        builder.removeListener(listener);
    }
	
	private class Store extends AbstractPreferenceStore{

		protected Store(String bundleName, String name ) {
			super(bundleName);
			addChild(name);
		}

		@Override
		protected IPreferenceStore<String, String> onAddChild(Preferences preferences) {
			return null;
		}

		public void setBoolean(Options options, int position, boolean choice) {
			super.setBoolean(options.name(), position, choice);
		}
	}
}
