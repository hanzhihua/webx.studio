package webx.studio.server.ui.actions;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.wizard.WizardDialog;

import webx.studio.server.ui.wizard.NewServerWizard;


public class NewServerAction  extends NewWizardAction {
	protected String[] ids;
	protected String[] values;

	/**
	 * Create a new NewServerAction.
	 */
	public NewServerAction() {
		super();
	}

	/**
	 * Create a new NewServerAction with some initial task model
	 * properties.
	 *
	 * @param ids
	 * @param values
	 */
	public NewServerAction(String[] ids, String[] values) {
		super();
		this.ids = ids;
		this.values = values;
	}

	/**
	 * Performs this action.
	 * <p>
	 * This method is called when the delegating action has been triggered.
	 * Implement this method to do the actual work.
	 * </p>
	 *
	 * @param action the action proxy that handles the presentation portion of the
	 *   action
	 */
	public void run(IAction action) {
		NewServerWizard wizard =  new NewServerWizard();
		WizardDialog dialog = new WizardDialog(workbench.getActiveWorkbenchWindow().getShell(), wizard);
		dialog.open();
	}
}