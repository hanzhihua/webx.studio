package webx.studio.server.ui.wizard;


import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import webx.studio.server.core.Server;
import webx.studio.server.core.ServerCore;


/**
 * @author zhihua.hanzh
 *
 */
public class NewServerWizard extends Wizard implements INewWizard {

	private final ServerInformationWizardPage information = new ServerInformationWizardPage("Basic Information");
	private final ServerStructureWizardPage structure = new ServerStructureWizardPage("Jeju Projects in Server") {
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			if (visible) {
				composite.setServer(information.getServer());
			}
		}
	};

	public NewServerWizard() {
		setWindowTitle("New Jeju Server");

	}


	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}


	public boolean performFinish() {
		Server server = information.getServer();
		if(ServerCore.findServer(server.getId()) != null){
			MessageDialog.openError(getShell(), "Creation Problems",
					"Server["+server.getId()+"] already exist!");
			information.reset();
			return false;
		}
		server.save();
		return true;
	}

	public void addPages() {
		addPage(information);
		addPage(structure);

	}

}
