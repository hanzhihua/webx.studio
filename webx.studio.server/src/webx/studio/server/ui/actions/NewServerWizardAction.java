package webx.studio.server.ui.actions;


import org.eclipse.ui.IWorkbenchWizard;

import webx.studio.ImageResource;
import webx.studio.server.ui.wizard.NewServerWizard;


public class NewServerWizardAction  extends LaunchWizardAction {
	protected String[] ids;
	protected String[] values;

	/**
	 * New server action.
	 */
	public NewServerWizardAction() {
		super();

		setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_NEW_SERVER));
		setText("Create Server");
	}

	/**
	 * New server action.
	 *
	 * @param ids ids to pass into the action
	 * @param values values to pass into the action
	 */
	public NewServerWizardAction(String[] ids, String[] values) {
		this();
		this.ids = ids;
		this.values = values;
	}

	/**
	 * Return the wizard that should be opened.
	 *
	 * @return org.eclipse.ui.IWorkbenchWizard
	 */
	protected IWorkbenchWizard getWizard() {
		return new NewServerWizard();
	}
}