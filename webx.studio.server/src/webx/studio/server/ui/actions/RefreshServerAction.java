package webx.studio.server.ui.actions;


import org.eclipse.jface.viewers.ISelectionProvider;

import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;

public class RefreshServerAction  extends AbstractServerAction {

	public RefreshServerAction(ISelectionProvider sp) {
		super(sp, "Refresh server");
	}

	public void perform(Server server) {
		try {
			server.getApi().reinit();
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}
	}

//	public boolean accept(Server server) {
//		return true;
//	}


	public static void open(Server server){
		try {
			server.getApi().reinit();
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}
	}
}