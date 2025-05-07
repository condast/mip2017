package org.miip.rwt.entry;

import java.util.HashMap;
import java.util.Map;

import org.condast.commons.preferences.IPreferenceStore;
import org.condast.commons.preferences.xml.AbstractXMLBuilder;
import org.condast.commons.preferences.xml.AbstractXMLBuilder.Selection;
import org.condast.commons.strings.StringStyler;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.activate.ActivationEvent;
import org.condast.commons.ui.activate.IActivateListener;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.ui.widgets.logger.LogComposite;
import org.condast.commons.xml.BuildEvent;
import org.condast.commons.xml.IBuildListener;
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
import org.miip.waterway.model.def.IMIIPEnvironment;
import org.miip.waterway.ui.swt.MiipComposite;
import org.miip.waterway.ui.swt.pond.PondComposite;

public class MIIPEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	private static final String S_PREFERENCE_RADAR = "Rest";

	private enum CompositeNames{
		MIIP_COMPOSITE,
		POND_COMPOSITE,
		LOG_COMPOSITE;
	}

	private enum TabItems{
		MAIN,
		COLLISION_AVOIDANCE_DEMO,
		DEBUG,
		LOG;
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
			try {
				switch( Selection.valueOf( event.getName())) {
				case INPUT:
					String use_str = event.getAttribute(AbstractXMLBuilder.AttributeNames.USE);
					if((!StringUtils.isEmpty(use_str) && ( event.getData() instanceof IInputWidget ))) {
						IInputWidget<IMIIPEnvironment> widget = (IInputWidget<IMIIPEnvironment>) event.getData();
						widget.setInput( dispatcher.getEnvironment( use_str ));
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
								String str = (String) item.getData();
								if( StringUtils.isEmpty(str))
									return;
								TabItems ti = TabItems.valueOf( StringStyler.styleToEnum( (String) item.getData()));
								IMIIPEnvironment env = dispatcher.getActiveEnvironment();
								switch( ti ) {
								case MAIN:
									if( env != null )
										env.setEnabled(false);
									MiipComposite miip = (MiipComposite) item.getControl();
									if( miip.getInput() != null )
										miip.getInput().setEnabled(true);
									miip.setInput(dispatcher.getActiveEnvironment());
									break;
								case COLLISION_AVOIDANCE_DEMO:
									if( env != null )
										env.setEnabled(false);
									PondComposite pc = (PondComposite) item.getControl();
									if( pc.getInput() != null )
										pc.getInput().setEnabled(true);
									pc.setInput(dispatcher.getActiveEnvironment());
									break;
								default:
									break;
								}
								super.widgetSelected(e);
							}
							catch( Exception ex ) {
								ex.printStackTrace();
							}
						}
					});
					break;
				case COMPOSITE:
					String name_str = event.getAttribute(AbstractXMLBuilder.AttributeNames.NAME );
					if(StringUtils.isEmpty(name_str))
						break;
					CompositeNames cmp = CompositeNames.valueOf( StringStyler.styleToEnum( name_str));
					switch( cmp ) {
					case POND_COMPOSITE:
						//PondComposite pondcomp = (PondComposite) event.getData();
						break;
					case MIIP_COMPOSITE:
						MiipComposite miipcomp = (MiipComposite) event.getData();
						dispatcher.setMiipComposite( miipcomp );
						break;
					case LOG_COMPOSITE:
						logComposite = (LogComposite) event.getData();
						logComposite.addActivateListener(slistener);
						break;
					default:
						break;
					}

					break;
				default:
					break;
				}
			}
			catch( Exception ex ) {
				ex.printStackTrace();
			}
		}
	};

	private IActivateListener slistener = new IActivateListener() {

		@Override
		public void notifyActivationChange(ActivationEvent event) {
			LogComposite button = (LogComposite) event.getSource();
			boolean choice  = button.isActivated();
			IPreferenceStore<String, String> store = preferences.get( S_PREFERENCE_RADAR );
			store.setBoolean( Options.OPTIONS.name(), 0, choice);
		}
	};

	@Override
	protected void createContents(Composite parent) {
		try {
			preferences = new HashMap<>();
			parent.setLayout(new FillLayout());
			XMLFactoryBuilder builder = new XMLFactoryBuilder( parent, this.getClass());
			builder.addListener(listener);
			builder.build();
			builder.removeListener(listener);
			preferences.put(S_PREFERENCE_RADAR, builder.getPreferences( S_PREFERENCE_RADAR ));
		}
		catch( Exception ex ) {
			ex.printStackTrace();
		}
	}
}