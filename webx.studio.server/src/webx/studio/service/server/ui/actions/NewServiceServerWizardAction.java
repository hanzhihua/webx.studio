package webx.studio.service.server.ui.actions;


import org.eclipse.ui.IWorkbenchWizard;

import webx.studio.ImageResource;
import webx.studio.server.ui.actions.LaunchWizardAction;
import webx.studio.service.server.ui.wizard.NewSerivceServerWizard;


public class NewServiceServerWizardAction  extends LaunchWizardAction {

	public NewServiceServerWizardAction(){
		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_SERVICE_SERVER));
	}

	protected IWorkbenchWizard getWizard() {
		return new NewSerivceServerWizard();
	}

}
