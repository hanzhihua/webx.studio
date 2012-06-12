package webx.studio.service.server.ui.actions;

import java.util.List;


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
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

import webx.studio.server.ServerPlugin;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerChild;
import webx.studio.service.server.core.ServiceServerUtil;

public class DeleteServerDialog extends MessageDialog {

	private ServiceServer server;
	private ServiceServerChild child;

	protected List<ServiceServer> runningServersList;
	protected boolean runningServerCanStop;

	protected Button checkDeleteConfigs;
	protected Button checkDeleteRunning;
	protected Button checkDeleteRunningStop;

	public DeleteServerDialog(Shell parentShell, ServiceServer server) {
		super(parentShell, "Delete Service Server", null, null, QUESTION, new String[] {
				IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

		if (server == null)
			throw new IllegalArgumentException();
		this.server = server;
		message = NLS.bind("Are you sure you want to delete {0}?", server
				.getName());
	}

	public DeleteServerDialog(Shell parentShell,
			ServiceServerChild child) {
		super(parentShell, "Delete one java project", null, null, QUESTION,
				new String[] { IDialogConstants.OK_LABEL,
						IDialogConstants.CANCEL_LABEL }, 0);

		if (child == null)
			throw new IllegalArgumentException();
		this.child = child;
		message = NLS.bind("Are you sure you want to delete {0} from service server["
				+ child.getServer().getName() + "]?",
				child.getName());
	}

	protected Control createCustomArea(Composite parent) {
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
			if (server != null) {
				Thread t = new Thread("Delete servers") {
					public void run() {

						Job job = new Job("Deleting server(s)...") {
							protected IStatus run(IProgressMonitor monitor) {
								try {
									if (monitor.isCanceled())
										return Status.CANCEL_STATUS;

									server.delete();

									if (monitor.isCanceled())
										return Status.CANCEL_STATUS;

								} catch (Exception e) {

									return new Status(IStatus.ERROR,
											ServerPlugin.PLUGIN_ID, 0, e
													.getMessage(), e);
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
			} else if (child != null) {
				Thread t = new Thread("Delete one java project from the service server") {
					public void run() {

						Job job = new Job("Delete one java project from the service server...") {
							protected IStatus run(IProgressMonitor monitor) {
								try {
									if (monitor.isCanceled())
										return Status.CANCEL_STATUS;

									ServiceServerUtil.removeProjectFromServiceServer(child.getServer(), child.getName());
									((ServiceServer)(child.getServer())).save();

									if (monitor.isCanceled())
										return Status.CANCEL_STATUS;

								} catch (Exception e) {

									return new Status(IStatus.ERROR,
											ServerPlugin.PLUGIN_ID, 0, e
													.getMessage(), e);
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
		}
		super.buttonPressed(buttonId);
	}

}