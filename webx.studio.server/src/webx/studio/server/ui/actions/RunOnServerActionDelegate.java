package webx.studio.server.ui.actions;

import java.util.Iterator;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.eclipse.ui.handlers.HandlerUtil;

import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerCore;
import webx.studio.server.core.ServerUtil;

public class RunOnServerActionDelegate  implements IWorkbenchWindowActionDelegate {

	protected static final String[] launchModes = {
		ILaunchManager.RUN_MODE, ILaunchManager.DEBUG_MODE};

	protected Object selection;
	protected IWorkbenchWindow window;
	protected String launchMode = ILaunchManager.RUN_MODE;

	public void run(IAction action) {
		// TODO Auto-generated method stub
//		System.out.println(this.selection);
		if(this.selection instanceof JavaProject){
			JavaProject jp = (JavaProject)this.selection;
			JejuProject jejuProject = null;
			for(JejuProject tmpJeju:JejuProjectCore.getWebXProjectList()){
				if(tmpJeju.isWarProject(jp.getProject().getName())){
					jejuProject = tmpJeju;
					break;
				}
			}
			if(jejuProject == null )
				return;
			Server server = ServerCore.getServer(jejuProject);
			if(server != null ){

				ServerUtil.addWebXProjectToServer(server, jejuProject.getName());
				server.save();
				StartAction.start(server, launchMode,ServerPlugin.getActiveWorkbenchShell());
			}
		}
	}

	public void selectionChanged(IAction action, ISelection sel) {
		selection = null;
		if (sel == null || sel.isEmpty() || !(sel instanceof IStructuredSelection)) {
			action.setEnabled(false);
			return;
		}

		IStructuredSelection select = (IStructuredSelection) sel;
		Iterator iterator = select.iterator();
		if (iterator.hasNext())
			selection = iterator.next();
		if (iterator.hasNext()) { // more than one selection (should never happen)
			action.setEnabled(false);
			selection = null;
			return;
		}
	}

	public void dispose() {
		// TODO Auto-generated method stub

	}

	public void init(IWorkbenchWindow window) {
		this.window = window;
	}

	public void setLaunchMode(String launchMode) {
		this.launchMode = launchMode;
	}

}
