package webx.studio.service.server.core;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.core.model.IProcess;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;
import webx.studio.server.jetty.Constants;
import webx.studio.utils.LaunchUtil;

public class ServicerServerLaunchConfigurationDelegate extends
		AbstractJavaLaunchConfigurationDelegate {

	public void launch(ILaunchConfiguration configuration, String mode,
			ILaunch launch, IProgressMonitor monitor) throws CoreException {

		ServiceServer server = ServiceServerUtil.getServer(configuration);

		IVMInstall vm = verifyVMInstall(configuration);
		IVMRunner runner = vm.getVMRunner(mode);
		if (runner == null)
			runner = vm.getVMRunner(ILaunchManager.RUN_MODE);
		String pgmArgs = getProgramArguments(configuration);
		String vmArgs = getVMArguments(configuration);
		String[] envp = getEnvironment(configuration);
		ExecutionArguments execArgs = new ExecutionArguments(vmArgs, pgmArgs);
		Map vmAttributesMap = getVMSpecificAttributesMap(configuration);
		String[] classpath = (String[])configuration.getAttribute("classpathList", new ArrayList()).toArray(new String[0]);
		VMRunnerConfiguration runConfig = new VMRunnerConfiguration(
				ServiceServer.DEFAULT_BOOTSTRAP_CLASS_NAME, classpath);

		runConfig.setProgramArguments(getRuntimeArguments(configuration,execArgs.getProgramArgumentsArray()));
		runConfig.setEnvironment(envp);
		runConfig.setVMSpecificAttributesMap(vmAttributesMap);
		runConfig.setVMArguments(getVMRuntimeArguments(configuration,
				execArgs.getVMArgumentsArray()));

		String[] bootpath = getBootpath(configuration);
		if (bootpath != null && bootpath.length > 0)
			runConfig.setBootClassPath(bootpath);

		try {
			runner.run(runConfig, launch, monitor);
			server.getBehaviour().addProcessListener(launch.getProcesses()[0]);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private String[] getRuntimeArguments(ILaunchConfiguration configuration,
			String[] oringinalVMArguments) {
		List<String> runtimeArgs = new ArrayList<String>();
		try {
			String args = configuration.getAttribute(BoortConstants.ARG, "");
			if (StringUtils.isNotBlank(args)) {
				for (String str : StringUtils.split(args)) {
					runtimeArgs.add(str);
				}
			}
		} catch (CoreException e) {
			ServerPlugin.logError(e);
		}
		runtimeArgs.addAll(Arrays.asList(oringinalVMArguments));
		return runtimeArgs.toArray(new String[runtimeArgs.size()]);
	}

	private String[] getVMRuntimeArguments(ILaunchConfiguration configuration,
			String[] oringinalVMArguments) {
		List<String> runtimeVmArgs = new ArrayList<String>();
		try {
			ServiceServer server = ServiceServerUtil.getServer(configuration);

			LaunchUtil.addAttribute(configuration, runtimeVmArgs, BoortConstants.SPRING_CONFIG);
			LaunchUtil.addAttribute(configuration,runtimeVmArgs, BoortConstants.APP_NAME);
			LaunchUtil.addAttribute(configuration,runtimeVmArgs, BoortConstants.APP_PORT);
			LaunchUtil.addAttribute(configuration,runtimeVmArgs, BoortConstants.APP_TYPE);
			String vmargs = configuration.getAttribute(BoortConstants.VM_ARG, "");
			if (StringUtils.isNotBlank(vmargs)) {
				for (String str : StringUtils.split(vmargs)) {
					runtimeVmArgs.add(str);
				}
			}
		} catch (CoreException e) {
			ServerPlugin.logError(e);
		}
		runtimeVmArgs.addAll(Arrays.asList(oringinalVMArguments));
		return runtimeVmArgs.toArray(new String[runtimeVmArgs.size()]);
	}

}
