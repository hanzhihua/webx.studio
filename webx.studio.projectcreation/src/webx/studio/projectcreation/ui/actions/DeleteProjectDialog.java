package webx.studio.projectcreation.ui.actions;


import org.eclipse.core.resources.IResourceRuleFactory;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.ISchedulingRule;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.MultiRule;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;

public class DeleteProjectDialog extends MessageDialog {

	private JejuProject[] projects;

	public DeleteProjectDialog(Shell parentShell, JejuProject[] projects) {
		super(parentShell, "Delete Project", null, null, QUESTION,
				new String[] { IDialogConstants.OK_LABEL,
						IDialogConstants.CANCEL_LABEL }, 0);
		if (projects == null)
			throw new IllegalArgumentException();
		this.projects = projects;

		if (projects.length == 1)
			message = NLS.bind("Are you sure you want to delete {0}?",
					projects[0].getName());
		else
			message = NLS.bind(
					"Are you sure you want to delete the {0} projects?",
					projects.length + "");
	}

	protected Control createCustomArea(Composite parent) {
		// create a composite with standard margins and spacing
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());
		Dialog.applyDialogFont(composite);

		return composite;
	}

	protected void buttonPressed(int buttonId) {
		if (buttonId == OK) {
			Thread t = new Thread("Delete project") {
				public void run() {
					Job job = new Job("Deleting server(s)...") {
						protected IStatus run(IProgressMonitor monitor) {
							if (projects.length == 0) {
								// all servers have been deleted from list
								return Status.OK_STATUS;
							}
							try {
								if (monitor.isCanceled())
									return Status.CANCEL_STATUS;

								int size = projects.length;
								for (int i = 0; i < size; i++)
									JejuProjectCore.deleteWebXProject(projects[i]);

								if (monitor.isCanceled())
									return Status.CANCEL_STATUS;

							} catch (Exception e) {
								ProjectCreationPlugin.logThrowable(e);
							}

							return Status.OK_STATUS;
						}
					};

					ISchedulingRule[] rules = new ISchedulingRule[1];
					IResourceRuleFactory ruleFactory = ResourcesPlugin
							.getWorkspace().getRuleFactory();
					rules[0] = ruleFactory.createRule(ResourcesPlugin
							.getWorkspace().getRoot());
					job.setRule(MultiRule.combine(rules));
					job.setPriority(Job.BUILD);

					job.schedule();
				}
			};
			t.setDaemon(true);
			t.start();
		}
		super.buttonPressed(buttonId);
	}
}
