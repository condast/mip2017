package org.miip.rwt;

import org.condast.commons.xml.AbstractXMLBuilder;
import org.condast.commons.xml.BuildEvent;
import org.condast.commons.xml.IBuildListener;

import java.util.Map;

import org.condast.commons.preferences.IPreferenceStore;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.logger.LogComposite;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.xml.AbstractXMLBuilder.Selection;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.miip.rwt.service.Dispatcher;
import org.miip.rwt.xml.XMLFactoryBuilder;
import org.miip.rwt.xml.XMLFactoryBuilder.Store;
import org.miip.waterway.environment.IEnvironment;
import org.miip.waterway.model.def.IPhysical;
import org.miip.waterway.ui.swt.MiipComposite;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	private static final String S_PREFERENCE_RADAR = "org.miip.waterway.rest";
	
	private enum CompositeNames{
		MIIP_COMPOSITE,
		LOG_COMPOSITE;
	}

	public enum Options{
		OPTIONS;

		@Override
		public String toString() {
			return StringStyler.prettyString( super.toString() );
		}
	}

	private Map<String, IPreferenceStore<String, String>> preferences;
	private Dispatcher dispatcher = Dispatcher.getInstance();
	
	private LogComposite logComposite;
	private IBuildListener<Widget> listener = new IBuildListener<Widget>(){

		@SuppressWarnings("unchecked")
		@Override
		public void notifyTestEvent(BuildEvent<Widget> event) {
			if( !Selection.isOfSelection(event.getName()))
				return;
			switch( Selection.valueOf( event.getName())) {
			case INPUT:
				String use_str = event.getAttribute(AbstractXMLBuilder.AttributeNames.USE);
				if((!StringUtils.isEmpty(use_str) && ( event.getData() instanceof IInputWidget ))) {
					IInputWidget<IEnvironment<IPhysical>> widget = (IInputWidget<IEnvironment<IPhysical>>) event.getData();
					widget.setInput( Dispatcher.getInstance().getEnvironment( use_str ));
				}
				break;
			case COMPOSITE:
				String name_str = event.getAttribute(AbstractXMLBuilder.AttributeNames.NAME );
				if(StringUtils.isEmpty(name_str))
					break;
				CompositeNames cmp = CompositeNames.valueOf( StringStyler.styleToEnum( name_str));
				switch( cmp ) {
				case MIIP_COMPOSITE:
					MiipComposite miipcomp = (MiipComposite) event.getData();
					miipcomp.setFactories( dispatcher.getFactories().values());
					break;
				case LOG_COMPOSITE:
					logComposite = (LogComposite) event.getData();
					logComposite.addSelectionListener(slistener);
					break;
				default:
					break;
				}
				
			break;
			default:
				break;
			}
		}
	};
	
	private SelectionListener slistener = new SelectionAdapter() {
		private static final long serialVersionUID = 1L;

		@Override
		public void widgetSelected( SelectionEvent e ) {
			Button button = (Button) e.widget;
			boolean choice  = button.getSelection();
			XMLFactoryBuilder.Store store = (Store) preferences.get( S_PREFERENCE_RADAR );
			store.setBoolean( Options.OPTIONS.name(), 0, choice);
		}
	};
	
	@Override
    protected void createContents(Composite parent) {
        parent.setLayout(new FillLayout());       
        XMLFactoryBuilder builder = new XMLFactoryBuilder( parent, this.getClass());
        builder.addListener(listener);
        builder.build();
        builder.removeListener(listener);
        preferences = builder.getPreferences();
   }
}
