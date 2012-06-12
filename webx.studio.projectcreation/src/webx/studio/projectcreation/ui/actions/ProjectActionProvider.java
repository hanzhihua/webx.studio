package webx.studio.projectcreation.ui.actions;

import java.util.Iterator;


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
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonViewerSite;
import org.eclipse.ui.navigator.ICommonViewerWorkbenchSite;

import webx.studio.projectcreation.ui.core.JejuProject;

public class ProjectActionProvider extends CommonActionProvider {

	private ICommonActionExtensionSite actionSite;
	protected Action[] actions;
	protected Action openAction, deleteAction, syncAction,
			showServerViewAction;

	public static final String NEW_MENU_ID = "jeju.projectcreation.ui.newMenuId";

	public ProjectActionProvider() {
	}

	public void init(ICommonActionExtensionSite aSite) {
		super.init(aSite);
		this.actionSite = aSite;
		ICommonViewerSite site = aSite.getViewSite();
		if (site instanceof ICommonViewerWorkbenchSite) {
			StructuredViewer v = aSite.getStructuredViewer();
			if (v instanceof CommonViewer) {
				CommonViewer cv = (CommonViewer) v;
				ICommonViewerWorkbenchSite wsSite = (ICommonViewerWorkbenchSite) site;
				makeProjectActions(cv, wsSite.getSelectionProvider());
			}
		}
	}

	private void makeProjectActions(CommonViewer tableViewer,
			ISelectionProvider provider) {
		Shell shell = tableViewer.getTree().getShell();
		openAction = new OpenProjectAction(provider);
		deleteAction = new DeleteAction(shell, provider);
		syncAction = new SyncAction();
		showServerViewAction = new ShowServerViewAction();
	}

	public void fillActionBars(IActionBars actionBars) {
//		actionBars.setGlobalActionHandler("org.eclipse.ui.navigator.Open",
//				openAction);
		actionBars.updateActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.DELETE.getId(),
				deleteAction);
		IContributionManager cm = actionBars.getToolBarManager();
		cm.removeAll();
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

		JejuProject project = null;
		if (selection != null && !selection.isEmpty()) {
			Iterator iterator = selection.iterator();
			Object obj = iterator.next();
			if (obj instanceof JejuProject)
				project = (JejuProject) obj;
			if (iterator.hasNext()) {
				project = null;
			}
		}
		menu.add(showServerViewAction);
		menu.add(new Separator());
		MenuManager newMenu = new MenuManager("Ne&w", NEW_MENU_ID);
		IAction newServerAction = new NewProjectWizardAction();
		newServerAction.setText("Project");
		newMenu.add(newServerAction);
		menu.add(newMenu);
		if (project != null) {
			menu.add(openAction);
			menu.add(deleteAction);

			
		}
		
		menu.add(new Separator());
		menu.add(syncAction);
	}
}
