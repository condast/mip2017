package org.miip.rwt;

import org.condast.commons.xml.AbstractXMLBuilder;
import org.condast.commons.xml.BuildEvent;
import org.condast.commons.xml.IBuildListener;
import org.condast.commons.strings.StringUtils;
import org.condast.commons.ui.swt.IInputWidget;
import org.condast.commons.xml.AbstractXMLBuilder.Selection;
import org.eclipse.rap.rwt.application.AbstractEntryPoint;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Widget;
import org.miip.rwt.service.Dispatcher;
import org.miip.rwt.xml.XMLFactoryBuilder;
import org.miip.waterway.environment.IEnvironment;

public class BasicEntryPoint extends AbstractEntryPoint {
	private static final long serialVersionUID = 1L;

	private IBuildListener<Widget> listener = new IBuildListener<Widget>(){

		@SuppressWarnings("unchecked")
		@Override
		public void notifyTestEvent(BuildEvent<Widget> event) {
			if( !Selection.isOfSelection(event.getName()))
				return;
			switch( Selection.valueOf( event.getName())) {
			case INPUT:
				String use_str = event.getAttribute(AbstractXMLBuilder.AttributeNames.USE);
				if(!StringUtils.isEmpty(use_str)) {
					IInputWidget<IEnvironment> widget = (IInputWidget<IEnvironment>) event.getData();
					widget.setInput( Dispatcher.getInstance().getEnvironment( use_str ));
				}
				break;
			case COMPOSITE:
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
}
