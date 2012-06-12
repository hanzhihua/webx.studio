package webx.studio.server.ui.cnf;

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
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerChild;
import webx.studio.server.core.ServerUtil;

public class DeleteServerDialog extends MessageDialog {

	private Server server;
	private ServerChild webxProjectModel;

	protected List<Server> runningServersList;
	protected boolean runningServerCanStop;

	protected Button checkDeleteConfigs;
	protected Button checkDeleteRunning;
	protected Button checkDeleteRunningStop;

	public DeleteServerDialog(Shell parentShell, Server server) {
		super(parentShell, "Delete Server", null, null, QUESTION, new String[] {
				IDialogConstants.OK_LABEL, IDialogConstants.CANCEL_LABEL }, 0);

		if (server == null)
			throw new IllegalArgumentException();
		this.server = server;
		message = NLS.bind("Are you sure you want to delete {0}?", server
				.getName());
	}

	public DeleteServerDialog(Shell parentShell,
			ServerChild webxProjectModel) {
		super(parentShell, "Delete WebX project", null, null, QUESTION,
				new String[] { IDialogConstants.OK_LABEL,
						IDialogConstants.CANCEL_LABEL }, 0);

		if (webxProjectModel == null)
			throw new IllegalArgumentException();
		this.webxProjectModel = webxProjectModel;
		message = NLS.bind("Are you sure you want to delete {0} from Server["
				+ webxProjectModel.getServer().getName() + "]?",
				webxProjectModel.getName());
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
			} else if (webxProjectModel != null) {
				Thread t = new Thread("Delete Webx project from the server") {
					public void run() {

						Job job = new Job("Delete Webx project from the server...") {
							protected IStatus run(IProgressMonitor monitor) {
								try {
									if (monitor.isCanceled())
										return Status.CANCEL_STATUS;

									ServerUtil.removeWebXProjectFromServer((Server)webxProjectModel.getServer(), webxProjectModel.getName());
									((Server)(webxProjectModel.getServer())).save();

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