package webx.studio.server.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.IJobChangeEvent;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.core.runtime.jobs.JobChangeAdapter;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;

import webx.studio.maven.MavenApi;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;
import webx.studio.server.ui.SaveEditorPrompter;
import webx.studio.server.ui.actions.AutoConfig;
import webx.studio.server.ui.event.IServerListener;
import webx.studio.server.ui.event.ServerEvent;
import webx.studio.utils.ProgressUtil;
import webx.studio.xml.Base;
import webx.studio.xml.XMLMemento;

/**
 * @author zhihua.hanzh
 *
 */
public class Server extends Base {

	public static final String ATTR_SERVER_ID = "server-id";
	public static final String ATTR_SERVER_TYPE_ID = "type-id";
	public static final String FILE_EXTENSION = "server";
	public static final int STATE_STARTED = 2;
	public static final int STATE_STARTING = 1;

	public static final int STATE_STOPPED = 4;
	public static final int STATE_STOPPING = 3;
	public static final int STATE_AUTOCONF = 5;
	public static final int STATE_UNKNOWN = 0;
	public static final String WEBX_PROJECT_LIST = "webx-projects";

	public static final String[] DEFAULT_URI_CHARSETS = { "GBK",
	"UTF-8" };

	private final MavenApi api = new MavenApi();
	protected ServerBehaviourDelegate behaviourDelegate;
	private transient ILaunch launch;
	private List<IServerListener> listeners = new ArrayList<IServerListener>();
	private transient String mode = ILaunchManager.RUN_MODE;

	private transient int serverState = STATE_STOPPED;

	private ServerType type;
	private Job autoconfJob;

	Server() {

	}

	public void addListener(IServerListener listener) {
		listeners.add(listener);
	}

	public IStatus canStart(String launchMode) {
		if (getServerType() == null)
			return new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, 0,
					"Could not find server type.", null);
		int state = getServerState();
		if (state != STATE_STOPPED && state != STATE_UNKNOWN)
			return new Status(
					IStatus.ERROR,
					ServerPlugin.PLUGIN_ID,
					0,
					"The server cannot start because its state isn't currently stopped. ",
					null);
		return Status.OK_STATUS;
	}

	public void delete() {
		ServerResourceManager.getInstance().removeServer(this);
		deleteMetadata();
	}

	private void deleteLaunchConfigurations() {
		if (getServerType() == null)
			return;

		String launchConfigId = ((ServerType) getServerType())
				.getLaunchConfigId();
		ILaunchManager launchManager = DebugPlugin.getDefault()
				.getLaunchManager();
		ILaunchConfigurationType launchConfigType = launchManager
				.getLaunchConfigurationType(launchConfigId);

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

	private void deleteMetadata() {
		listeners.clear();
		deleteLaunchConfigurations();
	}

	public MavenApi getApi() {
		return api;
	}

	public ServerBehaviourDelegate getBehaviourDelegate() {
		return behaviourDelegate;
	}

	public ServerBehaviourDelegate getBehaviourDelegate(IProgressMonitor monitor)
			throws CoreException {
		if (behaviourDelegate != null || type == null)
			return behaviourDelegate;

		if (behaviourDelegate == null) {
			long time = System.currentTimeMillis();
			behaviourDelegate = type.createServerBehaviourDelegate();
			if (behaviourDelegate != null)
				behaviourDelegate.initialize(this, monitor);
			Trace.trace(Trace.PERFORMANCE, "Server.getBehaviourDelegate(): <"
					+ (System.currentTimeMillis() - time) + "> "
					+ getServerType().getId());
		}
		return behaviourDelegate;
	}

	public ILaunch getLaunch() {
		return this.launch;
	}

	public ILaunchConfiguration getLaunchConfiguration(boolean create,
			IProgressMonitor monitor) throws CoreException {

		ILaunchConfigurationType launchConfigType = type
				.getLaunchConfigurationType();

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
		wc.setAttribute(ATTR_SERVER_ID, getId());
		setupLaunchConfiguration(wc, monitor);
		return wc.doSave();
	}

	public String getMode() {
		return mode;
	}

	public String[] getOnlyWebXProjectNames() {
		List<String> origlist = getAttribute(WEBX_PROJECT_LIST,
				new ArrayList<String>());
		List<String> list = new ArrayList<String>();
		for (String str : origlist) {
			list.add(ServerUtil.getWebXProjectName(str));
		}

		return list.toArray(new String[0]);
	}

	public int getServerState() {
		return serverState;
	}

	public ServerType getServerType() {
		return type;
	}

	public String[] getWebXProjectNames() {

		List<String> list = getAttribute(WEBX_PROJECT_LIST,
				new ArrayList<String>());
		return list.toArray(new String[0]);
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

		ServerType serverType = null;
		String serverTypeId = getAttribute(ATTR_SERVER_TYPE_ID, (String) null);
		if (serverTypeId != null)
			serverType = ServerCore.findServerType(serverTypeId);
		if (serverType != null) {
			this.setType(serverType);
		}
	}

	protected void loadFromMemento(XMLMemento memento, IProgressMonitor monitor) {
		load(memento);
	}

	public void removeListener(IServerListener listener) {
		listeners.remove(listener);
	}

	public void save() {
		ServerResourceManager.getInstance().addServer(this);
	}

	public void setLaunch(ILaunch launch) {
		this.launch = launch;
	}

	public void setMode(String m) {
		if (m == mode)
			return;

		this.mode = m;
	}

	public void setServerState(int state) {
		if (state == serverState)
			return;

		this.serverState = state;
		for (IServerListener listener : listeners) {
			listener.serverChanged(new ServerEvent(this, serverState));
		}
	}

	public void setType(ServerType type) {
		this.type = type;
		map.put(ATTR_SERVER_TYPE_ID, type.getId());
	}

	public void setupLaunchConfiguration(
			ILaunchConfigurationWorkingCopy configuration,
			IProgressMonitor monitor) {

		try {
			getBehaviourDelegate(monitor).setupLaunchConfiguration(
					configuration, monitor);
		} catch (CoreException e) {
			Trace.trace(Trace.SEVERE,
					"Error calling delegate setupLaunchConfiguration() "
							+ toString(), e);
		}

	}

	public void setWebXProjects(List<String> webxProjectNames) {
		map.put(WEBX_PROJECT_LIST, webxProjectNames);
	}

	public void start(final String launchMode, final IProgressMonitor monitor)
			throws CoreException {
		ServerType type = getServerType();
		if (type == null) {
			return;
		}
		final SaveEditorPrompter editorHelper = ServerPlugin
				.getSaveEditorHelper();
		if ("JBoss".equalsIgnoreCase(type.getVendor())) {
			MessageDialog.open(MessageDialog.WARNING, Display.getCurrent()
					.getActiveShell(), "",
					"JBoss Server hasn't implemented yet!", SWT.SHEET);
			return;
		}

		if (editorHelper != null)
			editorHelper.saveAllEditors();

		String[] webxProjects = getOnlyWebXProjectNames();
		if (webxProjects == null || webxProjects.length == 0) {
			MessageDialog.openError(ServerPlugin.getActiveWorkbenchShell(),
					"Run Server",
					" you can't run Server without any webx project. ");
			return;
		}

		Set<String> webxProjectSet = new HashSet<String>();
		for (String str : webxProjects) {
			if (JejuProjectCore.getWebXProject(str) == null) {
				MessageDialog.open(MessageDialog.WARNING, Display.getCurrent()
						.getActiveShell(), "", "WebX Project[" + str
						+ "] doesn't exist,firstly remove it!", SWT.SHEET);
				return;
			}
			webxProjectSet.add(str);
		}

		Trace.trace(Trace.FINEST, "Starting server: " + Server.this.toString()
				+ ", launchMode: " + launchMode);

		if (!isWithoutAutoconfig()) {
			autoconfJob = new AutoConfig(this, webxProjectSet).getJob();
			autoconfJob.addJobChangeListener(new JobChangeAdapter() {
				public void done(IJobChangeEvent event) {
					IStatus status = event.getResult();
					if (status != Status.OK_STATUS) {
						return;
					}
					Server.this.setServerState(STATE_STARTING);
					Server.this.setMode(launchMode);
					if (editorHelper != null) {
						editorHelper.setDebugNeverSave();
					}
					startImpl(launchMode, monitor);
					if (editorHelper != null) {
						editorHelper.setDebugOriginalValue();
					}

				}
			});
			this.setServerState(STATE_AUTOCONF);
			autoconfJob.schedule();
			return;
		}
		this.setServerState(STATE_STARTING);
		this.setMode(launchMode);
		if (editorHelper != null) {
			editorHelper.setDebugNeverSave();
		}
		;
		startImpl(launchMode, monitor);
		if (editorHelper != null) {
			editorHelper.setDebugOriginalValue();
		}

	}

	private IStatus startImpl(String launchMode, IProgressMonitor monitor) {
		monitor = ProgressUtil.getMonitorFor(monitor);
		try {
			ILaunchConfiguration launchConfig = getLaunchConfiguration(true,
					monitor);
			if (launchConfig != null) {
				launchConfig.launch(launchMode, monitor);
			}
		} catch (Exception e) {
			Trace.traceError(e);
		}
		return Status.OK_STATUS;
	}

	public void stop(boolean force) {
		if (getServerState() == STATE_STOPPED)
			return;
		if (getServerState() == STATE_AUTOCONF) {
			if (autoconfJob != null) {
				autoconfJob.cancel();
				autoconfJob = null;
				try {
					Thread.sleep(1 * 1000);
				} catch (InterruptedException e) {
				}
				setServerState(STATE_STOPPED);
			}

		}
		Trace.trace(Trace.FINEST, "Stopping server: " + Server.this.toString());
		try {
			getBehaviourDelegate(null).stop(force);
		} catch (RuntimeException e) {
			Trace.trace(Trace.SEVERE, "Error calling delegate stop() "
					+ Server.this.toString(), e);
			throw e;
		} catch (Throwable t) {
			Trace.trace(Trace.SEVERE, "Error calling delegate stop() "
					+ Server.this.toString(), t);
			throw new RuntimeException(t);
		}
	}

	public String toString() {
		return getName();
	}

}
