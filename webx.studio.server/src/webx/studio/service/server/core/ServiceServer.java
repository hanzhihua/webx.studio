package webx.studio.service.server.core;

import hidden.edu.emory.mathcs.backport.java.util.Arrays;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.ObjectUtils;
import org.apache.maven.artifact.Artifact;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jface.dialogs.MessageDialog;

import webx.studio.maven.MavenApi;
import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;
import webx.studio.service.server.ui.event.IServiceServerListener;
import webx.studio.service.server.ui.event.ServiceServerEvent;
import webx.studio.utils.ProgressUtil;
import webx.studio.xml.Base;
import webx.studio.xml.XMLMemento;

public class ServiceServer extends Base {

	public static final String DEFAULT_SPRING_CONFIG = "classpath*:META-INF/spring/*.xml";
	public static final String SERVICE_SERVER_BASE_PATH = ServerPlugin.getInstance().getStateLocation().append("service_servers").toOSString();

	public static final String THIS_LAUNCH_ID = "serviceServerLaunchType";
	public static final int STATE_STARTED = 2;
	public static final int STATE_STARTING = 1;
	public static final int STATE_STOPPED = 4;
	public static final int STATE_STOPPING = 3;
	public static final int STATE_UNKNOWN = 0;

	private transient int serverState = STATE_STOPPED;
	private transient String mode = ILaunchManager.RUN_MODE;

	public static final String ATTR_SERVER_ID = "server-id";
	public static final String DEFAULT_BOOTSTRAP_CLASS_NAME = "com.alibaba.boort.Server";
	public static final String DEFAULT_GROUP_ID = "com.alibaba.platform.shared";
	public static final String DEFAULT_ARTIFACT_ID = "boort";
	private String home;
	private String version;
	private transient ILaunch launch;

	private ServiceServerBehaviour behaviour;

	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}

	public ILaunch getLaunch() {
		return this.launch;
	}


	public static final String SERVICE_PROJECT_LIST = "service-projects";


	public String[] getServiceProjects() {

		List<String> list = getAttribute(SERVICE_PROJECT_LIST,
				new ArrayList<String>());
		return list.toArray(new String[0]);
	}

	public void setServiceProjects(List<String> projects) {
		map.put(SERVICE_PROJECT_LIST, projects);
	}

	public String getVersion() {
		return ObjectUtils.toString(map.get("version"));
	}

	public void setVersion(String version) {
		this.version = version;
		map.put("version", this.version);
	}

	private List<IServiceServerListener> listeners = new ArrayList<IServiceServerListener>();
	private final MavenApi api = new MavenApi();

	public MavenApi getApi() {
		return api;
	}

	public void addListener(IServiceServerListener listener) {
		listeners.add(listener);
	}

	public void removeListener(IServiceServerListener listener) {
		listeners.remove(listener);
	}

	public String getHome() {
		return ObjectUtils.toString(map.get("home"));
	}

	public void setHome(String home) {
		this.home = home;
		map.put("home", home);
	}

	private String conf =DEFAULT_SPRING_CONFIG;

	public String getConf() {
		return ObjectUtils.toString(map.get("conf"));
	}

	public void setConf(String conf) {
		this.conf = conf;
		map.put("conf", conf);
	}

	private String arg;
	private String vmArg;



	public String getArg() {
		return ObjectUtils.toString(map.get("arg"));
	}

	public void setArg(String arg) {
		this.arg = arg;
		map.put("arg", arg);
	}

	public String getVmArg() {
		return ObjectUtils.toString(map.get("vmArg"));
	}

	public void setVmArg(String vmArg) {
		this.vmArg = vmArg;
		map.put("vmArg", vmArg);
	}

	private String appName,appPort,appType;
	public static final int DEFAULT_APP_PORT = 20880;
	public static final String DEFAULT_APP_TYPE = "dubbo.provider";



	public String getAppName() {
		return ObjectUtils.toString(map.get("appName"));
	}

	public void setAppName(String appName) {
		this.appName = appName;
		map.put("appName", appName);
	}

	public String getAppPort() {
		return ObjectUtils.toString(map.get("appPort"));
	}

	public void setAppPort(String appPort) {
		this.appPort = appPort;
		map.put("appPort", appPort);
	}

	public String getAppType() {
		return ObjectUtils.toString(map.get("appType"));
	}

	public void setAppType(String appType) {
		this.appType = appType;
		map.put("appType", appType);
	}

	public ServiceServer() {
	}

	public void start(final String launchMode, final IProgressMonitor monitor)
			throws CoreException {


		String[] serviceProjects = getServiceProjects();
		if (serviceProjects == null || serviceProjects.length == 0) {
			MessageDialog.openError(ServerPlugin.getActiveWorkbenchShell(),
					"Run Service server",
					" you can't run service server without any java project. ");
			return;
		}
		this.setServerState(STATE_STARTING);
		this.setMode(launchMode);
		startImpl(launchMode, monitor);
	}

	public void setServerState(int state) {
		if (state == serverState)
			return;
		this.serverState = state;
		for (IServiceServerListener listener : listeners) {
			listener.serviceServerChanged(new ServiceServerEvent(this, serverState));
		}
	}

	public void setMode(String m) {
		if (m == mode)
			return;

		this.mode = m;
	}

	private IStatus startImpl(String launchMode, IProgressMonitor monitor) {
		monitor = ProgressUtil.getMonitorFor(monitor);
		try {
			ILaunchConfiguration launchConfig = getLaunchConfiguration(true,
					monitor);
			if (launchConfig != null) {
				ILaunch launch = launchConfig.launch(launchMode, monitor);
				setLaunch(launch);
			}

			this.setServerState(ServiceServer.STATE_STARTED);
		} catch (Exception e) {
			Trace.traceError(e);
		}
		return Status.OK_STATUS;
	}

	public ILaunchConfigurationType getLaunchConfigurationType() {
		try {
			ILaunchManager launchManager = DebugPlugin.getDefault()
					.getLaunchManager();
			return launchManager.getLaunchConfigurationType(THIS_LAUNCH_ID);
		} catch (Exception e) {
			Trace.trace(Trace.CONFIG, e.getMessage(), e);
			return null;
		}
	}

	public ILaunchConfiguration getLaunchConfiguration(boolean create,
			IProgressMonitor monitor) throws CoreException {

		ILaunchConfigurationType launchConfigType = getLaunchConfigurationType();

		if (launchConfigType == null)
			return null;

		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfiguration[] launchConfigs = launchManager
				.getLaunchConfigurations(launchConfigType);

		if (launchConfigs != null) {
			for (ILaunchConfiguration launchConfig : launchConfigs) {
				String serverId = launchConfig.getAttribute(ATTR_SERVER_ID,
						(String) null);
				if (getId().equals(serverId)) {
					final ILaunchConfigurationWorkingCopy wc = launchConfig
							.getWorkingCopy();
					setupLaunchConfiguration(wc, monitor);
					if (wc.isDirty()) {
						final ILaunchConfiguration[] lc = new ILaunchConfiguration[1];
						Job job = new Job("Saving launch configuration") {
							protected IStatus run(IProgressMonitor monitor2) {
								try {
									lc[0] = wc.doSave();
								} catch (CoreException ce) {

								}
								return Status.OK_STATUS;
							}
						};
						job.setSystem(true);
						job.schedule();
						try {
							job.join();
						} catch (Exception e) {

						}
						if (job.getState() != Job.NONE) {
							job.cancel();
							lc[0] = wc.doSave();
						}

						return lc[0];
					}
					return launchConfig;
				}
			}
		}

		String launchName = getName();
		launchName = launchManager
				.generateUniqueLaunchConfigurationNameFrom(launchName);
		ILaunchConfigurationWorkingCopy wc = launchConfigType.newInstance(null,
				launchName);

		setupLaunchConfiguration(wc, monitor);
		return wc.doSave();
	}

	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy configuration,
			IProgressMonitor monitor) throws CoreException {
		configuration.setAttribute(ATTR_SERVER_ID, getId());
		String[] classpath = ServiceServerUtil.getClasspath(configuration);
		configuration.setAttribute("classpathList",Arrays.asList(classpath));
		configuration.setAttribute(BoortConstants.SPRING_CONFIG, getConf());
		configuration.setAttribute(BoortConstants.APP_NAME, getAppName());
		configuration.setAttribute(BoortConstants.APP_PORT, getAppPort());
		configuration.setAttribute(BoortConstants.APP_TYPE, getAppType());
		configuration.setAttribute(BoortConstants.VM_ARG, getVmArg());
		configuration.setAttribute(BoortConstants.ARG, getArg());
		behaviour = new ServiceServerBehaviour(this);

	}

	public void mergeClasspath(List<IRuntimeClasspathEntry> cp,
			IRuntimeClasspathEntry entry) {
		Iterator<IRuntimeClasspathEntry> iterator = cp.iterator();
		while (iterator.hasNext()) {
			IRuntimeClasspathEntry entry2 = iterator.next();

			if (entry2.getPath().equals(entry.getPath()))
				return;
		}

		cp.add(entry);
	}


	protected void load(XMLMemento memento) {
		map = new HashMap<String, Object>();

		Iterator<String> iterator = memento.getNames().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			map.put(key, memento.getString(key));
		}
		XMLMemento[] children = memento.getChildren("list");
		if (children != null) {
			for (XMLMemento child : children)
				loadList(child);
		}
		XMLMemento[] maps = memento.getChildren("map");
		if (maps != null) {
			for (XMLMemento m : maps)
				loadMap(m);
		}

	}

	protected void loadFromMemento(XMLMemento memento, IProgressMonitor monitor) {
		load(memento);
	}

	public void save(){
		ServiceServerResourceManager.getInstance().addServer(this);
	}

	public int getServerState() {
		return serverState;
	}

	public String getMode() {
		return mode;
	}

	public void download(String groupId,String artifactId,String destDir) throws Exception{
		Artifact artifact = api.resvoleArtifact(groupId, artifactId, null);
		FileUtils.copyFileToDirectory(artifact.getFile(), new File(destDir));
		setVersion(artifact.getBaseVersion());
	}

	public void stop() {
		if (getServerState() == STATE_STOPPED)
			return;
		this.setServerState(ServiceServer.STATE_STOPPING);
		ILaunch launch = getLaunch() ;
		if(launch != null){
			try {
				launch.terminate();
//				Thread.sleep(1*1000);
			} catch (Exception e) {
				ServerPlugin.logError(e);
			}finally{
				setLaunch(null);
			}
		}

		this.setServerState(ServiceServer.STATE_STOPPED);
	}

	public void stopWithoutTerminate(){
		if (getServerState() == STATE_STOPPED)
			return;
		this.setServerState(ServiceServer.STATE_STOPPING);
		this.setServerState(ServiceServer.STATE_STOPPED);
	}

	public void delete() {
		ServiceServerResourceManager.getInstance().removeServer(this);
		try {
			FileUtils.deleteDirectory(new File(getHome()));
		} catch (IOException e) {
			ServerPlugin.logError(e);
		}
		deleteMetadata();
	}

	private void deleteMetadata() {
		listeners.clear();
		deleteLaunchConfigurations();
	}

	private void deleteLaunchConfigurations() {
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfigurationType launchConfigType = launchManager
				.getLaunchConfigurationType(THIS_LAUNCH_ID);

		ILaunchConfiguration[] configs = null;
		try {
			configs = launchManager.getLaunchConfigurations(launchConfigType);
			int size = configs.length;
			for (int i = 0; i < size; i++) {
				try {
					if (getId().equals(
							configs[i].getAttribute(ATTR_SERVER_ID,
									(String) null)))
						configs[i].delete();
				} catch (Exception e) {
					// ignore
				}
			}
		} catch (Exception e) {
			// ignore
		}
	}

	public ServiceServerBehaviour getBehaviour() {
		return behaviour;
	}



}
