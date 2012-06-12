package webx.studio.service.server.ui.actions;



import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

import webx.studio.ImageResource;
import webx.studio.service.server.core.ServiceServer;

public class StopAction extends AbstractServerAction {
	public StopAction(Shell shell, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, "stop");
		setToolTipText("Stop the server");
		setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_LAUNCH_STOP));
	}

	public void perform(ServiceServer server) {

		stop(server, shell);

	}

	public static void stop(ServiceServer server, Shell shell) {
		server.stop();

	}

	public boolean accept(ServiceServer server) {
		return server.getServerState() != ServiceServer.STATE_STOPPED;
	}

}