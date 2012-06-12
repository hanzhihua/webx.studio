package webx.studio.server.ui.actions;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

import webx.studio.ImageResource;
import webx.studio.server.core.Server;

public class StopAction extends AbstractServerAction {
	public StopAction(Shell shell, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, "stop");
		setToolTipText("Stop the server");
		setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_LAUNCH_STOP));
	}

	public void perform(Server server) {

		stop(server, shell);

	}

	public static void stop(Server server, Shell shell) {
		server.stop(false);

	}

	public boolean accept(Server server) {
		if (((Server) server).getServerType() == null)
			return false;

		return server.getServerState() != Server.STATE_STOPPED;
	}

}