package webx.studio.service.server.ui.actions;


import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.swt.widgets.Shell;

import webx.studio.ImageResource;
import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;
import webx.studio.service.server.core.ServiceServer;

public class StartAction extends AbstractServerAction {

	protected String launchMode = ILaunchManager.RUN_MODE;

	public StartAction(Shell shell, ISelectionProvider selectionProvider,
			String launchMode) {
		super(shell, selectionProvider, "start");
		this.launchMode = launchMode;
		if (launchMode == ILaunchManager.RUN_MODE) {
			setToolTipText("Start the server");
			setText("&Start");
			setImageDescriptor(ImageResource
					.getImageDescriptor(ImageResource.IMG_LAUNCH_START));
		} else if (launchMode == ILaunchManager.DEBUG_MODE) {
			setToolTipText("Start the server in debug mode");
			setText("&Debug");
			setImageDescriptor(ImageResource
					.getImageDescriptor(ImageResource.IMG_LAUNCH_DEBUG));
		}
	}

	
	public void perform(ServiceServer server) {
		start(server, launchMode, shell);

	}

	public static void start(ServiceServer server, String launchMode,
			final Shell shell) {
		if (server.getServerState() == Server.STATE_STOPPED) {
			try {
				server.start(launchMode, null);
			} catch (Exception e) {
				ServerPlugin.logError(e);
			}
		}
	}

}