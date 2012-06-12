package webx.studio.projectcreation.ui.actions;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWizard;
import org.eclipse.ui.PlatformUI;

import webx.studio.ImageResource;
import webx.studio.projectcreation.ui.ProjectCreationWizard;


public class NewProjectWizardAction   extends Action {
	protected String[] ids;
	protected String[] values;

	public NewProjectWizardAction() {
		super();

		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_JEJU_PROJECT));
		setText("Create Jeju Project");
	}

	protected IWorkbenchWizard getWizard() {
		return new ProjectCreationWizard();
	}

	public void run() {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkbenchWindow workbenchWindow = workbench.getActiveWorkbenchWindow();
		ISelection selection = workbenchWindow.getSelectionService().getSelection();

		IStructuredSelection selectionToPass = null;
		if (selection instanceof IStructuredSelection)
			selectionToPass = (IStructuredSelection) selection;
		else
			selectionToPass = StructuredSelection.EMPTY;

		IWorkbenchWizard wizard = getWizard();
		wizard.init(workbench, selectionToPass);
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.open();
	}

}
