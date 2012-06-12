package webx.studio.server.ui.cnf;


import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

import webx.studio.server.core.Server;

public class ServerDecorator extends LabelProvider implements
		ILightweightLabelDecorator {

	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof Server) {
			int state = ((Server) element).getServerState();
			String stateStr = "";
			if (state == Server.STATE_STARTING){
				stateStr = "Starting";
			}else if (state == Server.STATE_STOPPING){
				stateStr = "Stopping";
			}else if (state == Server.STATE_STARTED) {
				String mode = ((Server) element).getMode();
				if (ILaunchManager.DEBUG_MODE.equals(mode))
					stateStr= "Debugging";
				else
					stateStr= "Started";
			} else if (state == Server.STATE_STOPPED){
				stateStr = "Stopped";
			}else if(state == Server.STATE_AUTOCONF){
				stateStr = "do Autoconf";
			}

			decoration.addSuffix("  [" + stateStr + "]");
		}

	}

	public String decorateText(String text, Object element) {
		return "decoration " + text;
	}

}
