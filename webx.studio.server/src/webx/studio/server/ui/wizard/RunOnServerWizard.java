package webx.studio.server.ui.wizard;


import org.eclipse.jface.wizard.Wizard;

import webx.studio.projectcreation.ui.core.JejuProject;

public class RunOnServerWizard  extends Wizard {
	
	private final JejuProject jejuProject;
	public RunOnServerWizard(JejuProject jejuProject){
		this.jejuProject = jejuProject;
		setNeedsProgressMonitor(true);
	}
	@Override
	public boolean performFinish() {
		// TODO Auto-generated method stub
		return false;
	}

	

}
