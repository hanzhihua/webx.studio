package webx.studio.server.ui.wizard;


import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import webx.studio.server.core.Server;


public class ModifyModulesWizard extends Wizard implements INewWizard {

	private final Server server;

	private final ServerStructureWizardPage structure = new ServerStructureWizardPage(
			"test123..........","Add and Remove WebX Project...","Modify WebX Project that are configured on the server") {
		public void setVisible(boolean visible) {
			super.setVisible(visible);
			if (visible) {
				composite.setServer(ModifyModulesWizard.this.server);
			}
		}
	};


	public void init(IWorkbench workbench, IStructuredSelection selection) {
		// TODO Auto-generated method stub

	}


	public boolean performFinish() {
		server.save();
		return true;
	}

	public void addPages() {
		addPage(structure);

	}

	public ModifyModulesWizard(Server server) {
		this.server = server;
	}

}
