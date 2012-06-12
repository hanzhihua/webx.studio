package webx.studio.service.server.ui.actions;

import java.util.Iterator;


import org.apache.commons.lang.StringUtils;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.internal.core.JavaProject;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;

import webx.studio.server.ServerPlugin;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerUtil;
import webx.studio.service.server.ui.wizard.NewSerivceServerWizard;

public class RunOnBoortActionDelegate   implements IWorkbenchWindowActionDelegate {

	protected static final String[] launchModes = {
		ILaunchManager.RUN_MODE, ILaunchManager.DEBUG_MODE};

	protected Object selection;
	protected IWorkbenchWindow window;
	protected String launchMode = ILaunchManager.RUN_MODE;

	public void run(IAction action) {
		if(this.selection instanceof JavaProject){
			JavaProject jp = (JavaProject)this.selection;
			String projectName = jp.getProject().getName();
			ServiceServer ss = getServiceServer(projectName);
			if(ss != null)
			StartAction.start(ss, launchMode,ServerPlugin.getActiveWorkbenchShell());
		}
	}

	private ServiceServer getServiceServer(String projectName){
		for(ServiceServer ss:ServiceServerUtil.getServiceServers()){
			String[] projects = ss.getServiceProjects();
			if(projects == null || projects.length != 1){
				continue;
			}
			if(StringUtils.equals(projectName, projects[0])){
				return ss;
			}
		}
		NewSerivceServerWizard wizard = new NewSerivceServerWizard(projectName);
		WizardDialog dialog = new WizardDialog(ServerPlugin.getActiveWorkbenchShell(), wizard);
		if (dialog.open() == Window.CANCEL) {
			return null;
		}
		return wizard.getServiceServer();
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
