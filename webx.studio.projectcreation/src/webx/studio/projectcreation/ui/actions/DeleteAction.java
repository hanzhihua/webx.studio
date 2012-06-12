package webx.studio.projectcreation.ui.actions;

import java.util.Iterator;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.SelectionProviderAction;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.utils.UIUtil;

public class DeleteAction   extends SelectionProviderAction {

	private Shell shell;
	private JejuProject[] projects;

	protected DeleteAction(Shell shell, ISelectionProvider selectionProvider) {
		super(selectionProvider, "Delete");
		this.shell = shell;
		ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
		setImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE));
		setDisabledImageDescriptor(sharedImages.getImageDescriptor(ISharedImages.IMG_TOOL_DELETE_DISABLED));
		setActionDefinitionId(IWorkbenchActionDefinitionIds.DELETE);
		setEnabled(false);
	}

	@Override
	public void selectionChanged(IStructuredSelection sel) {
		if (sel.isEmpty()) {
			setEnabled(false);
			return;
		}
		boolean enabled = false;
		Iterator iterator = sel.iterator();
		while (iterator.hasNext()) {
			Object obj = iterator.next();
			if (obj instanceof JejuProject) {
				enabled = true;
			}else {
				setEnabled(false);
				return;
			}
		}
		setEnabled(enabled);
	}

	@Override
	public void run() {
		JejuProject project = null;

		IStructuredSelection sel = getStructuredSelection();
		// filter the selection
		if (!sel.isEmpty()) {
			Iterator iterator = sel.iterator();
			Object obj = iterator.next();
			if (obj instanceof JejuProject)
				project = (JejuProject) obj;
			if (iterator.hasNext()) {
				project = null;
			}
		}

		if (project != null)
			deleteProject(project);
		UIUtil.refreshPackageExplorer();

	}

	protected void deleteProject(JejuProject project){
		projects = new JejuProject[1];
		projects[0] = project;
		DeleteProjectDialog dpd = new DeleteProjectDialog(shell, projects);
		dpd.open();
	}



}
