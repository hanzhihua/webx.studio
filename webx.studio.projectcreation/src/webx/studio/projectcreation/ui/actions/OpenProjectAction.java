package webx.studio.projectcreation.ui.actions;


import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.editor.IProjectEditorInput;
import webx.studio.projectcreation.ui.editor.ProjectEditorInput;


public class OpenProjectAction  extends AbstractProjectAction {

	public OpenProjectAction(ISelectionProvider sp) {
		super(sp, "Open");
	}

	public void perform(JejuProject project) {
		try {
			editProject(project.getId());
		} catch (Exception e) {
			ProjectCreationPlugin.logThrowable(e);
		}
	}

	protected void editProject(String projectId) {
		if (projectId == null)
			return;

		IWorkbenchWindow workbenchWindow = ProjectCreationPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		try {
			IProjectEditorInput input = new ProjectEditorInput(projectId);
			page.openEditor(input, IProjectEditorInput.EDITOR_ID);
		} catch (Exception e) {
			ProjectCreationPlugin.logThrowable(e);
		}
	}
	
	public static void open(JejuProject project){
		
		if(project == null)
			return;
		
		IWorkbenchWindow workbenchWindow = ProjectCreationPlugin.getDefault().getWorkbench().getActiveWorkbenchWindow();
		IWorkbenchPage page = workbenchWindow.getActivePage();

		try {
			IProjectEditorInput input = new ProjectEditorInput(project.getId());
			page.openEditor(input, IProjectEditorInput.EDITOR_ID);
		} catch (Exception e) {
			ProjectCreationPlugin.logThrowable(e);
		}
		
	}

}
