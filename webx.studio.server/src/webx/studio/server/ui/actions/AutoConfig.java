package webx.studio.server.ui.actions;

import java.io.File;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.OperationCanceledException;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
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
import webx.studio.utils.ProjectUtil;

import com.alibaba.antx.config.ConfigRuntimeImpl;
import com.alibaba.antx.util.i18n.LocaleInfo;

public class AutoConfig {

	private Set<String> webxProjectNames;
	private static final String CONSOLE_NAME = " Jeju Server Autoconf ";
	private Server server;

	public AutoConfig(Set<String> webxProjectNames) {
		this.webxProjectNames = webxProjectNames;
	}

	public AutoConfig(Server server, Set<String> webxProjectNames) {
		this.server = server;
		this.webxProjectNames = webxProjectNames;
	}

	public void run() {

		final Job job = getJob();

		// job.setRule(SerialRule.instance);
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

				// VelocityTemplateEngine.getInstance();

				IOConsole mc = getIOConsole();
				IOConsoleOutputStream consoleStream = mc.newOutputStream();

				System.setIn(mc.getInputStream());
				System.setErr(new PrintStream(consoleStream));
				System.setOut(new PrintStream(consoleStream));

				ClassLoader ctl = Thread.currentThread()
						.getContextClassLoader();
				Thread.currentThread().setContextClassLoader(
						ConfigRuntimeImpl.class.getClassLoader());

				for (String webxProjectName : webxProjectNames) {
					JejuProject jejuProject = JejuProjectCore
							.getWebXProject(webxProjectName);
					if (jejuProject == null) {
						try {
							consoleStream.write("WebX Project["
									+ webxProjectName + "] doesn't exist!");
							continue;
						} catch (Exception e) {
							ServerPlugin.logError(e);
						}
					}

					try {
						List<String> dests = new ArrayList<String>();
						if (server != null) {
							ServerUtil.copyClasspath(server, webxProjectName);
							dests.add(ServerUtil.getTmpServerDir(server,
									webxProjectName).getAbsolutePath());
						}

						String destFiles = jejuProject.getAntxDestFiles();
						if (StringUtils.isBlank(destFiles)) {
							for (String eclipseProjectName : jejuProject
									.getProjectNames()) {

								IProject project = ProjectUtil
										.getProject(eclipseProjectName);
								if (project == null)
									continue;
								List<String> tmps = AutoConfigUtils
										.getDestDirs(project);
								if (CollectionUtils.isEmpty(tmps)) {
								} else {
									System.out.println("Project:["
											+ project.getName()
											+ "] have autoconfig directory "
											+ tmps);
									dests.addAll(tmps);
								}
							}
						} else {
							for (String str : StringUtils.split(destFiles, ",")) {
								if (StringUtils
										.equals(AutoConfigConstants.MAVEN_PROJECT_BASEDIR,
												str)) {
									for (String projectName : jejuProject
											.getProjectNames()) {
										IProject project = ProjectUtil
												.getProject(projectName);
										String path = project.getLocation()
												.toString();
										File file = new File(path);
										String filePath = file
												.getAbsolutePath();
										filePath = filePath.replaceAll("\\\\",
												"/");
										if (filePath.indexOf("/deploy/") != -1) {
											break;
										}
										dests.add(filePath);
									}
								} else {
									dests.add(str);
								}
							}
						}
						if (dests.size() == 0) {
							System.out.println("WebX Project["
									+ " webxProjectName "
									+ "] haven't any autoconfig directory!");
							System.out.println("WebX Project["
									+ " webxProjectName "
									+ "] autoconfig done!");
							continue;
						}

						consoleStream.write("WebX Project [" + webxProjectName
								+ "] auto config begin !");
//						LocaleInfo.getDefault().setDefault("china");
						ConfigRuntimeImpl runtimeImpl = new ConfigRuntimeImpl(
								System.in, System.out, System.err,
								StringUtils.trimToNull(jejuProject
										.getAutoconfigCharset()));
						String includeDescriptorPatterns = jejuProject
								.getAntxIncludeDescriptorPatterns();
						if (StringUtils.isNotBlank(includeDescriptorPatterns))
							runtimeImpl.setDescriptorPatterns(
									includeDescriptorPatterns, new String());
						runtimeImpl.setDests(dests.toArray(new String[0]));
//						 runtimeImpl.setVerbose();

						if (StringUtils.isNotBlank(jejuProject
								.getAntxPropertiesFile())) {
							String antxPropertiesFileStr = StringUtils
									.trim(jejuProject.getAntxPropertiesFile());
							File file = new File(antxPropertiesFileStr);
							if (file.isAbsolute()) {
								runtimeImpl.setUserPropertiesFile(
										antxPropertiesFileStr, null);
							} else {
								File topDir = JejuProjectCore
										.getTopDir(jejuProject);
								if (topDir != null && topDir.isDirectory()) {
									File antoconfigProperties = new File(
											topDir, antxPropertiesFileStr);
									if (antoconfigProperties.exists()) {
										runtimeImpl.setUserPropertiesFile(
												antoconfigProperties
														.getCanonicalPath(),
												null);
									}
								}
							}
						} else {
							File topDir = JejuProjectCore
									.getTopDir(jejuProject);
							if (topDir != null && topDir.isDirectory()) {
								File antoconfigProperties = new File(topDir,
										"antx.properties");
								if (antoconfigProperties.exists()
										&& antoconfigProperties.isFile()) {
									runtimeImpl.setUserPropertiesFile(
											antoconfigProperties
													.getCanonicalPath(), null);
								}
							}
						}
						runtimeImpl.start();
						consoleStream.write("WebX Project [" + webxProjectName
								+ "] auto config done !");
						consoleStream.write("\r\n");
						consoleStream.write("\r\n");
						consoleStream.write("\r\n");
					} catch (OperationCanceledException e) {
						ServerPlugin.logError(e);
						System.err.println("The error[" + e.getMessage()
								+ "] occurs");
						throw e;
					} catch (Exception e) {
						ServerPlugin.logError(e);
						System.err.println("The error[" + e.getMessage()
								+ "] occurs");
					}
				}
//				try {
//					ResourcesPlugin
//							.getWorkspace()
//							.getRoot()
//							.refreshLocal(IResource.DEPTH_INFINITE,
//									ProgressUtil.getMonitorFor(null));
//				} catch (CoreException e) {
//					ServerPlugin.logError(e);
//				}

				Thread.currentThread().setContextClassLoader(ctl);
				System.setIn(System.in);
				System.setErr(System.err);
				System.setOut(System.out);
				monitor.done();
				try {
					// mc.getInputStream().close();
					consoleStream.close();
				} catch (Exception e) {
					ServerPlugin.logError(e);
				}
				return Status.OK_STATUS;

			}
		};
		// job.setRule(SerialRule.instance);
		return job;
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
