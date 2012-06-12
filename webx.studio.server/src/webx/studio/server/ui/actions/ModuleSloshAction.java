package webx.studio.server.ui.actions;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;

import webx.studio.ImageResource;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.server.core.Server;
import webx.studio.server.ui.wizard.ModifyModulesWizard;



public class ModuleSloshAction extends AbstractServerAction {

	public ModuleSloshAction(Shell shell, ISelectionProvider selectionProvider) {
		super(shell, selectionProvider, "&Add and Remove...");
		setToolTipText("Add and Remove resources to the server");
//		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_ETOOL_MODIFY_MODULES));
//		setHoverImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_CTOOL_MODIFY_MODULES));
//		setDisabledImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_DTOOL_MODIFY_MODULES));
		try {
			selectionChanged((IStructuredSelection) selectionProvider.getSelection());
		} catch (Exception e) {
//			ServerUIPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, ServerUIPlugin.PLUGIN_ID, e.getLocalizedMessage(), e));
			e.printStackTrace();
		}
	}

	public void perform(final Server server) {
		if (server == null)
			return;
		String[] projectNames = ((Server)server).getWebXProjectNames();
		if(projectNames.length == 0 && JejuProjectCore.getWebXProjectList().size() == 0){
			MessageDialog.openInformation(shell, "Server", "There are no resources that can be added or removed from the server.");
			return ;
		}
		ModifyModulesWizard wizard = new ModifyModulesWizard((Server)server);
		WizardDialog dialog = new WizardDialog(shell, wizard);
		dialog.open();


	}

}
