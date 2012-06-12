package webx.studio.server.core.jetty;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerUtil;
import webx.studio.server.jetty.Activator;
import webx.studio.server.jetty.Constants;
import webx.studio.utils.LaunchUtil;

public class JettyLaunchConfigurationDelegate extends
		AbstractJavaLaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		Server server = ServerUtil.getServer(configuration);
		if (server == null) {
			Trace.trace(Trace.FINEST,
					"Launch configuration could not find server");
			return;
		}

		JettyServerBehaviour jettyServer = (JettyServerBehaviour) server
				.getBehaviourDelegate(monitor);
		String mainTypeName = jettyServer.getRuntimeClass();
		IVMInstall vm = verifyVMInstall(configuration);

		IVMRunner runner = vm.getVMRunner(mode);
		if (runner == null)
			runner = vm.getVMRunner(ILaunchManager.RUN_MODE);

		File workingDir = verifyWorkingDirectory(configuration);
		String workingDirName = null;
		if (workingDir != null)
			workingDirName = workingDir.getAbsolutePath();

		String pgmArgs = getProgramArguments(configuration);
		String vmArgs = getVMArguments(configuration);
		String[] envp = getEnvironment(configuration);

		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
		String[] classpath = getClasspath(configuration);

		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(
				mainTypeName, classpath);
		runConfig.setProgramArguments(execArgs.getProgramArgumentsArray());
		runConfig.setEnvironment(envp);
		runConfig.setVMArguments(getRuntimeArguments(configuration,
				execArgs.getVMArgumentsArray(),
				ILaunchManager.DEBUG_MODE.equals(mode)));

		runConfig.setVMSpecificAttributesMap(vmAttributesMap);

		String[] bootpath = getBootpath(configuration);
		if (bootpath != null && bootpath.length > 0)
			runConfig.setBootClassPath(bootpath);

		setDefaultSourceLocator(launch, configuration);
		jettyServer.setupLaunch(launch, mode, monitor);
		try {
			runner.run(runConfig, launch, monitor);
			jettyServer.addProcessListener(launch.getProcesses()[0]);
		} catch (Exception e) {
			jettyServer.stopImpl();
		}
	}


	private List<String> getJettyArgs(ILaunchConfiguration configuration)
			throws CoreException {

		List<String> runtimeVmArgs = new ArrayList<String>();

		Field[] fields = Constants.class.getFields();
		for (Field field : fields) {
			int modifier = field.getModifiers();
			if (Modifier.isPublic(modifier) && Modifier.isFinal(modifier)
					&& Modifier.isStatic(modifier)) {
				try {
					Object value = field.get(null);
					if (value != null)
						LaunchUtil.addAttribute(configuration, runtimeVmArgs, value+"");
				} catch (Exception e) {
					Trace.trace(Trace.WARNING, e.getMessage());
				}
			}
		}
		return runtimeVmArgs;
	}

	private String[] getRuntimeArguments(ILaunchConfiguration configuration,
			String[] oringinalVMArguments, boolean isDebug)
			throws CoreException {
		List<String> runtimeVmArgs = getJettyArgs(configuration);
		runtimeVmArgs.addAll(JettyEnv.JAVA_OPTS_LIST);

		String webappsKey = configuration.getAttribute(
				Constants.WEBAPPS_KEY, "");

		for (String webapp : webappsKey.split(",")) {
			for (String item : webapp.split("___")) {
				runtimeVmArgs.add("-D" + item + "="
						+ configuration.getAttribute(item, ""));
			}
		}

//		String autoconfigsKey = configuration.getAttribute(Constants.AUTOCONFIG_WEBX_PROJECTS_KEY, "");
//		for(String webxProject:autoconfigsKey.split(File.pathSeparator)){
//			for(String item:webxProject.split("___")) {
//				runtimeVmArgs.add("-D" + item + "="
//						+ configuration.getAttribute(item, ""));
//			}
//		}

		if (isDebug) {
			runtimeVmArgs.add("-D"+Constants.DEBUG_MODE+"=" + true);
		}

		runtimeVmArgs.add("-D"+Constants.ECLIPSE_LISTENER+"="
				+ ServerPlugin.getDefault().getListenerPort());
		Server server = ServerUtil.getServer(configuration);
		if(server != null){
			if(StringUtils.endsWithIgnoreCase("GBK",server.getUriCharset())){
				runtimeVmArgs.add("-Dorg.eclipse.jetty.util.URI.charset=GBK");
			}else if(StringUtils.endsWithIgnoreCase("UTF-8",server.getUriCharset())){
				runtimeVmArgs.add("-Dorg.eclipse.jetty.util.URI.charset=UTF-8");
			}
		}
		runtimeVmArgs.addAll(Arrays.asList(oringinalVMArguments));
		return runtimeVmArgs.toArray(new String[runtimeVmArgs.size()]);
	}

}
