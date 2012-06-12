package webx.studio.service.server.ui;

import java.util.Iterator;


import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IContributionManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerChild;
import webx.studio.service.server.ui.actions.AutoConfigAction;
import webx.studio.service.server.ui.actions.DeleteAction;
import webx.studio.service.server.ui.actions.NewServiceServerWizardAction;
import webx.studio.service.server.ui.actions.OpenAction;
import webx.studio.service.server.ui.actions.StartAction;
import webx.studio.service.server.ui.actions.StopAction;

public class ServerActionProvider extends CommonActionProvider {

	public static final String NEW_MENU_ID = "jeju.service.server.ui.newMenuId";

	private ICommonActionExtensionSite actionSite;
	private Action openAction;
	private Action deleteAction;
	private Action[] actions;
	protected Action autoConfigAction;


	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		this.actionSite = aSite;
		ICommonViewerSite site = aSite.getViewSite();
		if (site instanceof ICommonViewerWorkbenchSite) {
			StructuredViewer v = aSite.getStructuredViewer();
			if (v instanceof CommonViewer) {
				CommonViewer cv = (CommonViewer) v;
				ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) site;
				makeServerActions(cv, wsSite.getSelectionProvider());
			}
		}
	}

	private void makeServerActions(CommonViewer tableViewer,
			ISelectionProvider provider) {
		Shell shell = tableViewer.getTree().getShell();
		actions = new Action[3];
		actions[0] = new StartAction(shell, provider, ILaunchManager.DEBUG_MODE);
		actions[1] = new StartAction(shell, provider, ILaunchManager.RUN_MODE);
		actions[2] = new StopAction(shell, provider);
		openAction = new OpenAction(provider);
		deleteAction = new DeleteAction(shell, provider);
//		addAction = new AddAction(shell,provider,"Add Project");
		autoConfigAction = new AutoConfigAction(shell, provider);
	}

	public void fillActionBars(IActionBars actionBars) {
		actionBars.updateActionBars();
		IContributionManager cm = actionBars.getToolBarManager();
		cm.removeAll();
		for (int i = 0; i < actions.length; i++)
			cm.add(actions[i]);
	}

	public void fillContextMenu(IMenuManager menu) {
		menu.removeAll();
		ICommonViewerSite site = actionSite.getViewSite();
		IStructuredSelection selection = null;
		if (site instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) site;
			selection = (IStructuredSelection) wsSite.getSelectionProvider()
					.getSelection();
		}

		ServiceServer serviceServer = null;
		ServiceServerChild child = null;
		if (selection != null && !selection.isEmpty()) {
			Iterator iterator = selection.iterator();
			Object obj = iterator.next();
			if (obj instanceof ServiceServer) {
				serviceServer = (ServiceServer) obj;
			}else if(obj instanceof ServiceServerChild){
				child = (ServiceServerChild)obj;
			}
			if (iterator.hasNext()) {
				serviceServer= null;
				child = null;
			}
		}

		MenuManager newMenu = new MenuManager("Ne&w", NEW_MENU_ID);
		IAction newServerAction = new NewServiceServerWizardAction();
		newServerAction.setText("Boort server");
		newMenu.add(newServerAction);
		menu.add(newMenu);

		if (serviceServer != null ){
			menu.add(openAction);
			menu.add(deleteAction);
			menu.add(new Separator());
			for (int i = 0; i < actions.length; i++)
				menu.add(actions[i]);
			menu.add(new Separator());
//			menu.add(addAction);
		}else if(child != null){
//			menu.add(deleteAction);
		}
		menu.add(new Separator());
		menu.add(autoConfigAction);
	}
}
