package webx.studio.server.ui.actions;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.ide.IDE;

import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerResourceManager;


public class ServerActionHandler extends AbstractHandler {


	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection sel = HandlerUtil.getCurrentSelectionChecked(event);
		Object obj = null;
		if (sel instanceof IStructuredSelection) {
			IStructuredSelection select = (IStructuredSelection) sel;
			obj = select.getFirstElement();
		}

		String id = event.getCommand().getId();
		String mode = ILaunchManager.RUN_MODE;
		if (id.endsWith("debug")){
			mode = ILaunchManager.DEBUG_MODE;
		}else if (id.endsWith("stop")){
			mode = null;
		}else if(id.endsWith("edit")){
			IPath path = ServerPlugin.getInstance().getStateLocation()
			.append(ServerResourceManager.SERVER_DATA_FILE);
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
			 try {
				IDE.openEditorOnFileStore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileStore);
			} catch (PartInitException e) {
				Trace.traceError(e);
			}
			return null;
		}

		if (obj instanceof Server) {
			Server server = (Server) obj;
			if (mode == null)
				StopAction.stop(server, HandlerUtil.getActiveShell(event));
			else
				StartAction.start(server, mode, HandlerUtil.getActiveShell(event));
			return null;
		}

		return null;
	}

}
