package webx.studio.server.ui;

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
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.keys.IBindingService;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import webx.studio.server.core.Server;
import webx.studio.server.core.ServerChild;
import webx.studio.server.ui.actions.AutoConfigAction;
import webx.studio.server.ui.actions.DeleteAction;
import webx.studio.server.ui.actions.NewServerWizardAction;
import webx.studio.server.ui.actions.OpenAction;
import webx.studio.server.ui.actions.RefreshServerAction;
import webx.studio.server.ui.actions.ShowInConsoleAction;
import webx.studio.server.ui.actions.ShowInDebugAction;
import webx.studio.server.ui.actions.ShowProjectViewAction;
import webx.studio.server.ui.actions.StartAction;
import webx.studio.server.ui.actions.StopAction;


/**
 * @author zhihua.hanzh
 *
 */
public class ServerActionProvider extends CommonActionProvider {

	private ICommonActionExtensionSite actionSite;
	protected Action[] actions;
	protected Action refreshAction,openAction,showInConsoleAction, showInDebugAction;
	protected Action deleteAction;
	protected Action autoConfigAction;
	private Action showProjectViewAction;

	public static final String NEW_MENU_ID = "jeju.server.ui.newMenuId";
	public static final String SHOW_IN_MENU_ID = "jeju.server.ui.showInQuickMenu";


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
		refreshAction = new RefreshServerAction(provider);
		openAction = new OpenAction(provider);
		showInConsoleAction = new ShowInConsoleAction(provider);
		showInDebugAction = new ShowInDebugAction(provider);
		deleteAction = new DeleteAction(shell, provider);
		autoConfigAction = new AutoConfigAction(shell, provider);
		showProjectViewAction = new ShowProjectViewAction();
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
		Shell shell = actionSite.getViewSite().getShell();
		if (site instanceof ICommonViewerWorkbenchSite) {
			ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) site;
			selection = (IStructuredSelection) wsSite.getSelectionProvider()
					.getSelection();
		}

		Server server = null;
		ServerChild webxProject = null;
		if (selection != null && !selection.isEmpty()) {
			Iterator iterator = selection.iterator();
			Object obj = iterator.next();
			if (obj instanceof Server){
				server = (Server) obj;
			}else if(obj instanceof ServerChild){
				webxProject = (ServerChild)obj;
			}
			if (iterator.hasNext()) {
				server = null;
				webxProject= null;
			}
		}

		menu.add(showProjectViewAction);
		menu.add(new Separator());
		MenuManager newMenu = new MenuManager("Ne&w", NEW_MENU_ID);
		IAction newServerAction = new NewServerWizardAction();
		newServerAction.setText("Server");
		newMenu.add(newServerAction);
		menu.add(newMenu);

		if (server != null ){
			menu.add(refreshAction);
			menu.add(openAction);
			MenuManager showInMenu = new MenuManager("Show In", SHOW_IN_MENU_ID);
			showInMenu.add(showInConsoleAction);
			showInMenu.add(showInDebugAction);
			menu.add(showInMenu);
			menu.add(deleteAction);
			menu.add(new Separator());

			for (int i = 0; i < actions.length; i++)
				menu.add(actions[i]);
			menu.add(new Separator());
		}else if(webxProject != null){
			menu.add(deleteAction);
		}
		menu.add(new Separator());
		menu.add(autoConfigAction);
	}



}
