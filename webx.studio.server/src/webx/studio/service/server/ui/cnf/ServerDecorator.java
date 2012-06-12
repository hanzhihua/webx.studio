package webx.studio.service.server.ui.cnf;



import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.IDecoration;
import org.eclipse.jface.viewers.ILightweightLabelDecorator;
import org.eclipse.jface.viewers.LabelProvider;

import webx.studio.service.server.core.ServiceServer;

public class ServerDecorator extends LabelProvider implements ILightweightLabelDecorator {


	public void decorate(Object element, IDecoration decoration) {
		if (element instanceof ServiceServer) {
			int state = ((ServiceServer) element).getServerState();
			String stateStr = "";
			if (state == ServiceServer.STATE_STARTING){
				stateStr = "Starting";
			}else if (state == ServiceServer.STATE_STOPPING){
				stateStr = "Stopping";
			}else if (state == ServiceServer.STATE_STARTED) {
				String mode = ((ServiceServer) element).getMode();
				if (ILaunchManager.DEBUG_MODE.equals(mode))
					stateStr= "Debugging";
				else
					stateStr= "Started";
			} else if (state == ServiceServer.STATE_STOPPED){
				stateStr = "Stopped";
			}

			decoration.addSuffix("  [" + stateStr + "]");
		}

	}




}
