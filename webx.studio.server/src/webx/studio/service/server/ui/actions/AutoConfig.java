package webx.studio.service.server.ui.actions;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.ui.JavaPlugin;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IOConsoleOutputStream;

import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerUtil;
import webx.studio.server.util.AutoConfigConstants;
import webx.studio.server.util.AutoConfigUtils;
import webx.studio.service.server.core.ServiceServerChild;
import webx.studio.utils.JdtUtil;
import webx.studio.utils.ProgressUtil;
import webx.studio.utils.ProjectUtil;

import com.alibaba.antx.config.ConfigRuntimeImpl;

public class AutoConfig {

	private static final String CONSOLE_NAME = " Boort Autoconf ";
	private final List<ServiceServerChild> children;
	private final Set<String> projectNames = new HashSet<String>();
	private Server server;

	public AutoConfig(List<ServiceServerChild> children) {
		this.children = children;
		for (ServiceServerChild child : children) {
			projectNames.add(child.getName());
			addRelateProject(projectNames, child.getName());
		}
	}

	private void addRelateProject(Set<String> projectNames, String projectName) {
		try {
			List<IJavaProject> relateJavaProjects = JdtUtil
					.getAllDependingJavaProjects(JavaCore.create(ProjectUtil
							.getProject(projectName)));
			for (IJavaProject jp : relateJavaProjects) {
				projectNames.add(jp.getProject().getName());
			}
		} catch (JavaModelException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void run() {

		final Job job = getJob();
		job.schedule();
	}

	public Job getJob() {
		final Job job = new Job("Excute autoconfig") {

			protected void canceling() {
				this.getThread()
						.stop(new OperationCanceledException(
								"Forcely stop antoconf!"));
			}

			public IStatus run(IProgressMonitor monitor) {

				IOConsole mc = getIOConsole();
				IOConsoleOutputStream consoleStream = mc.newOutputStream();

				System.setIn(mc.getInputStream());
				System.setErr(new PrintStream(consoleStream));
				System.setOut(new PrintStream(consoleStream));

				ClassLoader ctl = Thread.currentThread()
						.getContextClassLoader();
				Thread.currentThread().setContextClassLoader(
						ConfigRuntimeImpl.class.getClassLoader());

				List<String> dests = new ArrayList<String>();
				for (String projectName : projectNames) {
					IProject project = ProjectUtil.getProject(projectName);
					List<String> tmps = AutoConfigUtils.getDestDirs(project);
					if (CollectionUtils.isEmpty(tmps)) {
					} else {
						System.out.println("Project:[" + project.getName()
								+ "] have autoconfig directory " + tmps);
						dests.addAll(tmps);
					}
				}

				if (dests.size() == 0) {
					System.out
							.println("Boort Server haven't any autoconfig directory!");
					System.out.println("Boort Server autoconfig done!");
					return Status.OK_STATUS;
				}


				try {
					consoleStream.write("Auto config begin !");
					ConfigRuntimeImpl runtimeImpl = new ConfigRuntimeImpl(
							System.in, System.out, System.err, "");
					runtimeImpl.setDests(dests.toArray(new String[0]));

					String userPropertiesFile = getUserPropertiesFile(children);
					if (StringUtils.isNotBlank(userPropertiesFile)) {
						runtimeImpl.setUserPropertiesFile(userPropertiesFile, null);
					}

					runtimeImpl.start();
					consoleStream.write("\r\n");
					consoleStream.write("\r\n");
					consoleStream.write("\r\n");

					Thread.currentThread().setContextClassLoader(ctl);
					consoleStream.write("Auto config done !");
					System.setIn(System.in);
					System.setErr(System.err);
					System.setOut(System.out);
					monitor.done();
					consoleStream.close();
				} catch (Exception e) {
					ServerPlugin.logError(e);
				}
				return Status.OK_STATUS;

			}
		};
		return job;
	}

	public String getUserPropertiesFile(List<ServiceServerChild> children) {
		if (children == null) {
			return null;
		}
		for (ServiceServerChild child : children) {
			IProject project = ProjectUtil.getProject(child.getName());
			File projectDir = new File(project.getLocation().toOSString());
			File antoconfigProperties = new File(projectDir, "antx.properties");
			if (antoconfigProperties.exists())
				return antoconfigProperties.getPath();
			antoconfigProperties = new File(projectDir.getParentFile(),
					"antx.properties");
			if (antoconfigProperties.exists())
				return antoconfigProperties.getPath();
			antoconfigProperties = new File(projectDir.getParentFile()
					.getParentFile(), "antx.properties");
			if (antoconfigProperties.exists())
				return antoconfigProperties.getPath();

		}
		return null;
	}

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
					.getWorkbench().getSharedImages()
					.getImageDescriptor("IMG_OBJS_TASK_TSK"));
			ConsolePlugin.getDefault().getConsoleManager()
					.addConsoles(new IConsole[] { mc });

		}
		ConsolePlugin.getDefault().getConsoleManager().showConsoleView(mc);
		mc.clearConsole();
		return mc;
	}

}
