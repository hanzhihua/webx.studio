package webx.studio.service.server.ui.wizard;

import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.WorkspaceModifyOperation;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;
import org.eclipse.ui.progress.WorkbenchJob;

import webx.studio.maven.MavenExecuteException;
import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.server.ServerPlugin;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.utils.UIUtil;

public class NewSerivceServerWizard extends Wizard implements INewWizard {

	private final ServiceServerInformationWizardPage information = new ServiceServerInformationWizardPage("Basic Information");
	private ServiceServer serviceServer;


	public ServiceServer getServiceServer() {
		return serviceServer;
	}

	public NewSerivceServerWizard() {
		setWindowTitle("New boort server");
	}

	public NewSerivceServerWizard(String projectName) {
		setWindowTitle("New boort server");
		information.setProject(projectName);
	}

	public void init(IWorkbench workbench, IStructuredSelection selection) {
	}


	public boolean performFinish() {
		final ServiceServer serviceServer = information.generateServiceServer();
		WorkspaceModifyOperation op = new WorkspaceModifyOperation() {
			protected void execute(IProgressMonitor monitor)
					throws CoreException {

				monitor.beginTask(
						"Create a Jeju service server [ " +serviceServer.getName()+ " ]", 1);
				IOConsole mc = getIOConsole();
				IOConsoleOutputStream consoleStream = mc.newOutputStream();

				System.setIn(mc.getInputStream());
				System.setErr(new PrintStream(consoleStream));
				System.setOut(new PrintStream(consoleStream));
				try{
					serviceServer.download(ServiceServer.DEFAULT_GROUP_ID,ServiceServer.DEFAULT_ARTIFACT_ID,serviceServer.getHome());
					serviceServer.save();
					NewSerivceServerWizard.this.serviceServer = serviceServer;
				}catch (Exception e) {
					throw new CoreException(new Status(IStatus.ERROR,
							ProjectCreationPlugin.PLUGIN_ID, -1,
							e.getMessage(), e));
				}finally{
					System.setIn(System.in);
					System.setErr(System.err);
					System.setOut(System.out);
					try {
						consoleStream.close();
					} catch (Exception e) {
						ServerPlugin.logError(e);
					}
					monitor.done();
					WorkbenchJob job = new WorkbenchJob("Show server service view") {
						public IStatus runInUIThread(IProgressMonitor monitor) {
							UIUtil.showView("jeju.service.server.ui.ServiceServersView");
							return Status.OK_STATUS;
						}
					};

					job.setSystem(true);
					job.setPriority(Job.SHORT);
					job.schedule(150);

				}

			}};

			try {
				getContainer().run(true, true, op);
			} catch (InterruptedException e) {
				return false;
			} catch (InvocationTargetException e) {
				e.printStackTrace();

				Throwable t = e.getTargetException();
				if (t instanceof CoreException) {
					CoreException ce = (CoreException) t;
					Throwable tmpt = ce.getStatus().getException();
					String errMessage = null;
					if (tmpt instanceof MavenExecuteException) {
						errMessage = "Maven command execute failed!";
					}
					ErrorDialog.openError(
							getShell(),
							"Failed to create service server", errMessage,
							((CoreException) t).getStatus());
				} else {
					MessageDialog.openError(getShell(), "Creation Problems",
							NLS.bind("Internal error: {0}", t.getMessage()));
				}
				ServerPlugin.logThrowable(t);
				return false;
			}

//			UIUtil.showView("jeju.service.server.ui.ServiceServersView");

		return true;
	}

	public void addPages() {
		addPage(information);
	}

	private static final String CONSOLE_NAME = " Jeju Service Server Creation Console ";
	private IOConsole getIOConsole() {
		org.eclipse.debug.ui.console.IConsole.class.toString();

		IOConsole mc = null;
		IConsoleManager consoleManager = ConsolePlugin.getDefault()
				.getConsoleManager();
		IConsole[] consoles = consoleManager.getConsoles();
		if (consoles != null) {
			for (IConsole console : consoles) {
				if (CONSOLE_NAME.equalsIgnoreCase(console.getName())) {
					mc = (IOConsole) console;
					break;
				}
			}
		}
		if (mc == null) {
			mc = new IOConsole(CONSOLE_NAME, JavaPlugin.getDefault()
					.getWorkbench().getSharedImages().getImageDescriptor(
							"IMG_OBJS_TASK_TSK"));
			ConsolePlugin.getDefault().getConsoleManager().addConsoles(
					new IConsole[] { mc });

		}
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(mc);
		mc.clearConsole();
		return mc;
	}

}
