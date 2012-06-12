package webx.studio.server.core.jetty;

import hidden.org.codehaus.plexus.util.StringUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.reflect.FieldUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.debug.core.model.IStreamsProxy;
import org.eclipse.debug.core.model.RuntimeProcess;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;

import webx.studio.projectcreation.ui.ProjectCreationConstants;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;
import webx.studio.server.core.PingThread;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerBehaviourDelegate;
import webx.studio.server.core.ServerUtil;
import webx.studio.server.core.WebModule;
import webx.studio.server.jetty.Activator;
import webx.studio.server.jetty.Constants;
import webx.studio.server.util.AutoConfigUtils;
import webx.studio.utils.PortUtil;
import webx.studio.utils.ProjectUtil;

public class JettyServerBehaviour extends ServerBehaviourDelegate {

	private PingThread ping;
	private int port;
	protected transient IDebugEventSetListener processListener;

	@Override
	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy configuration,
			IProgressMonitor monitor) {

		configuration.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_MAIN_TYPE_NAME,
				JettyServerConstants.BOOTSTRAP_CLASS_NAME);
		configuration.setAttribute(
				IJavaLaunchConfigurationConstants.ATTR_CLASSPATH_PROVIDER,
				"jettyClassPathProvider");

		StringBuilder sb = new StringBuilder();

		try {
			port = Integer.parseInt(server.getPort());
			if (!PortUtil.available(port)) {
				port = PortUtil.findAAvailablePort(50000, 60000);
			}
			configuration.setAttribute(Constants.PORT, String.valueOf(port));
		} catch (Exception e) {
			port = PortUtil.findAAvailablePort(50000, 60000);
			configuration.setAttribute(Constants.PORT, port
					+ "");
			Trace.trace(Trace.CONFIG, e.getMessage(), e);
		}

		configuration.setAttribute(Constants.SSL_PORT, "8443");
		String webappsKeyValue = "";
		for (WebModule webModule : ServerUtil.getWebModules(server)) {
			String contextKey = ServerUtil.getWebmoduleContextKey(webModule
					.getWebxProjectName());
			String dirKey = ServerUtil.getWemoduleWebappdirKey(webModule
					.getWebxProjectName());
			String classpathKey = ServerUtil.getWebmoduleClasspathKey(webModule
					.getWebxProjectName());
			configuration.setAttribute(contextKey, webModule.getContextPath());
			configuration.setAttribute(dirKey, webModule.getWebappdir());

			List<String> filtedPaths = new ArrayList<String>();

			try {

				String m2Repo = ((Path) JavaCore
						.getClasspathVariable(ProjectCreationConstants.M2_REPO_KEY))
						.toFile().getCanonicalPath();
				if (StringUtils.isNotBlank(m2Repo)) {
					Map<String, IProject> projectMap = ServerUtil
							.getAllRelativeWarProjects(server);
					IProject p = projectMap.get(webModule.getWebxProjectName());
					if (p != null)
						filtedPaths = server.getApi()
								.getNonTestAndProvidedPaths(
										new File(p.getLocation().toOSString(),
												"pom.xml"), m2Repo);
					webModule.setClasspath(ServerUtil
							.getWebappClasspath(server, webModule
									.getWebxProjectName(), filtedPaths));
					configuration.setAttribute(Constants.CLASSPATH_PROVIDER,
							"maven");
				} else {
					webModule.setClasspath(ServerUtil
							.getWebappClasspathByEclipse(configuration, server,
									webModule.getWebxProjectName()));
					configuration.setAttribute(Constants.CLASSPATH_PROVIDER,
							"eclipse");
				}
			} catch (Exception e) {
				ServerPlugin.logError(e);
				Trace.traceError(e);
				try {
					webModule.setClasspath(ServerUtil
							.getWebappClasspathByEclipse(configuration, server,
									webModule.getWebxProjectName()));
					configuration.setAttribute(Constants.CLASSPATH_PROVIDER,
							"eclipse");
				} catch (Exception ee) {
					Trace.traceError(ee);
					webModule.setClasspath("");
					configuration.setAttribute(Constants.CLASSPATH_PROVIDER,
							"error");

				}
			}

			configuration.setAttribute(classpathKey, webModule.getClasspath());
			if (webappsKeyValue.length() == 0)
				webappsKeyValue = contextKey + "___" + classpathKey + "___"
						+ dirKey;
			else
				webappsKeyValue += "," + contextKey + "___" + classpathKey
						+ "___" + dirKey;
		}

		configuration.setAttribute(Constants.WEBAPPS_KEY, webappsKeyValue);

		configuration.setAttribute(Constants.PARENT_LOADER_PRIORITY,
				Boolean.TRUE.toString());

		if (!server.isWithoutAutoconfig()) {
			configuration.setAttribute(Constants.AUTOCINFIG_SKIP, "false");
			StringBuilder autoconfigsKey = new StringBuilder();
			for (String webxProjectName : server.getOnlyWebXProjectNames()) {
				StringBuilder dests = new StringBuilder();
				JejuProject webxProject = JejuProjectCore
						.getWebXProject(webxProjectName);
				if (webxProject == null) {
					continue;
				}
				for (String eclipseProjectName : webxProject.getProjectNames()) {
					IProject project = ProjectUtil
							.getProject(eclipseProjectName);
					if (project == null)
						continue;
					List<String> tmps = AutoConfigUtils.getDestDirs(project);
					if (CollectionUtils.isEmpty(tmps)) {
						continue;
					} else {
						for (String str : tmps) {
							if (dests.length() > 0)
								dests.append(File.pathSeparator);
							dests.append(str);
						}
					}
				}
				configuration.setAttribute(ServerUtil
						.getAutoconfigDestsKey(webxProjectName), dests
						.toString());
				configuration.setAttribute(ServerUtil
						.getAutoconfigCharsetKey(webxProjectName), webxProject
						.getAutoconfigCharset());
				configuration.setAttribute(ServerUtil
						.getAutoconfigUserPropertiesKey(webxProjectName),
						webxProject.getAntxPropertiesFile());

				if (autoconfigsKey.length() > 0)
					autoconfigsKey.append(File.pathSeparator);
				autoconfigsKey
						.append(ServerUtil
								.getAutoconfigDestsKey(webxProjectName)
								+ "___"
								+ ServerUtil
										.getAutoconfigCharsetKey(webxProjectName)
								+ "___"
								+ ServerUtil
										.getAutoconfigUserPropertiesKey(webxProjectName));
			}
			configuration.setAttribute(Constants.AUTOCONFIG_WEBX_PROJECTS_KEY,
					autoconfigsKey.toString());
		}

		if (server.isWithoutReloadfunction()) {
			configuration.setAttribute(Constants.PROVIDER_RELOAD_FUNCTION,
					Boolean.FALSE.toString());
			configuration.setAttribute(Constants.WEBX_MODE, Boolean.TRUE.toString());
//			try {
//				File jvmHome = JavaRuntime.computeVMInstall(configuration).getInstallLocation();
//				JdtUtil.disableReloadJVM(jvmHome);
//			} catch (Exception e) {
//				Trace.traceError(e);
//			}
		} else {
			configuration.setAttribute(Constants.PROVIDER_RELOAD_FUNCTION,
					Boolean.TRUE.toString());
			configuration.setAttribute(Constants.WEBX_MODE, Boolean.FALSE.toString());
//			try {
//				File jvmHome = JavaRuntime.computeVMInstall(configuration).getInstallLocation();
//				JdtUtil.enableReloadJVM(jvmHome, ProjectUtil.getReloadJVM(Activator.getDefault().getBundle()));
//			} catch (Exception e1) {
//				Trace.traceError(e1);
//			}
			try {
				configuration.setAttribute(Constants.RELOAD_CLASSPATH,ProjectUtil
								.getReloadClasspath(Activator.getDefault()
										.getBundle()));
			} catch (Exception e) {
				Trace.traceError(e);
			}
		}
	}

	@Override
	public String getRuntimeClass() {
		return JettyServerConstants.BOOTSTRAP_CLASS_NAME;
	}

	@Override
	public void setupLaunch(ILaunch launch, String launchMode,
			IProgressMonitor monitor) throws CoreException {

		server.setServerState(Server.STATE_STARTING);
		server.setMode(launchMode);
		server.setLaunch(launch);

		String url = "http://";
		if(StringUtils.isBlank(server.getHost())){
			url+= "localhost";
		}else{
			url+=server.getHost();
		}
		try {
			if (port != 80)
				url += ":" + port;
			ping = new PingThread(server, url, -1);
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Can't ping for Jetty startup.");
		}

	}

	@Override
	public void stop(boolean force) {
		if (force) {
			terminate();
			return;
		}
		int state = server.getServerState();
		if (state == Server.STATE_STOPPED || state == Server.STATE_STOPPING)
			return;
		else if (state == Server.STATE_STARTING) {
			terminate();
			return;
		}

		try {
			Trace.trace(Trace.FINEST, "Stopping Jetty");
			if (state != Server.STATE_STOPPED)
				server.setServerState(Server.STATE_STOPPING);
			ILaunch launch = server.getLaunch();
			if (launch != null) {
				IProcess process = launch.getProcesses()[0];
				try {
					if (process instanceof RuntimeProcess) {
						RuntimeProcess rp = (RuntimeProcess) process;
						IStreamsProxy sp = (IStreamsProxy) FieldUtils
								.readDeclaredField(rp, "fStreamsProxy", true);
						sp.write("exit" + System.getProperty("line.separator"));
					}
				} catch (Exception e) {
					launch.terminate();
				}
			}
//			stopImpl();

		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error stopping Jetty", e);
		}

	}

	private void terminate() {
		if (server.getServerState() == Server.STATE_STOPPED)
			return;

		try {
			server.setServerState(Server.STATE_STOPPING);
			Trace.trace(Trace.FINEST, "Killing the Jetty process");
			ILaunch launch = server.getLaunch();
			if (launch != null) {
				launch.terminate();
			}
			stopImpl();
		} catch (Exception e) {
			Trace.trace(Trace.SEVERE, "Error killing the process", e);
		}
	}

	void stopImpl() {
		if (ping != null) {
			ping.stop();
			ping = null;
		}
		if (processListener != null) {
			DebugPlugin.getDefault().removeDebugEventListener(processListener);
			processListener = null;
		}
		server.setServerState(Server.STATE_STOPPED);
	}

	void addProcessListener(final IProcess newProcess) {
		if (processListener != null || newProcess == null)
			return;

		processListener = new IDebugEventSetListener() {
			public void handleDebugEvents(DebugEvent[] events) {
				if (events != null) {
					int size = events.length;
					for (int i = 0; i < size; i++) {
						if (newProcess != null
								&& newProcess.equals(events[i].getSource())
								&& events[i].getKind() == DebugEvent.TERMINATE) {
							stopImpl();
						}
					}
				}
			}
		};

		DebugPlugin.getDefault().addDebugEventListener(processListener);
	}

}
