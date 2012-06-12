package webx.studio.projectcreation.ui.core;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


import org.apache.commons.collections.CollectionUtils;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.views.IWebXProjectLifecycleListener;
import webx.studio.xml.XMLMemento;

public class JejuProjectResourceManager {

	public static final String WEBX_PROJECT_METADATA_FILE = "webx-project-metadata.xml";

	private static String filename = ProjectCreationPlugin.getDefault()
			.getStateLocation().append(WEBX_PROJECT_METADATA_FILE).toOSString();

	private List<JejuProject> projects;
	private final Map<String, JejuProject> projectMap = new HashMap<String, JejuProject>();
	private final Map<String, JejuProject> projectIdMap = new HashMap<String, JejuProject>();

	protected List<IWebXProjectLifecycleListener> projectListeners = new ArrayList<IWebXProjectLifecycleListener>(
			3);

	private static JejuProjectResourceManager instance = new JejuProjectResourceManager();

	private static final byte EVENT_ADDED = 0;
	private static final byte EVENT_CHANGED = 1;
	private static final byte EVENT_REMOVED = 2;
	private static final byte EVENT_RELOAD_PROJECTS = 3;

	private static boolean initialized;
	private static boolean initializing;

	private JejuProjectResourceManager() {
		super();
	}

	protected synchronized void init() {
		if (initialized || initializing)
			return;
		initializing = true;
		projects = new ArrayList<JejuProject>();
		loadProjectList();
		initialized = true;
	}

	public synchronized void reload() {
		initialized = false;
		initializing = false;

		List<JejuProject> oldProjects = projects;
		init();
		List<JejuProject> newProjects = projects;
		if (oldProjects != null) {
			for (JejuProject oldProject : oldProjects) {
				if (!newProjects.contains(oldProject)) {
					fireProjectEvent(oldProject, EVENT_REMOVED);
				}
			}
			Collection<JejuProject> remainProjects = CollectionUtils.removeAll(
					newProjects, oldProjects);
			for (JejuProject remainProject : remainProjects) {
				fireProjectEvent(remainProject, EVENT_ADDED);
			}
		}
	}

	public static JejuProjectResourceManager getInstance() {
		return instance;
	}

	public void addWebXProjectLifecycleListener(
			IWebXProjectLifecycleListener listener) {

		synchronized (projectListeners) {
			projectListeners.add(listener);
		}
	}

	public void removeWebXProjectLifecycleListener(
			IWebXProjectLifecycleListener listener) {

		synchronized (projectListeners) {
			projectListeners.remove(listener);
		}
	}

	public JejuProject getWebXProject(String name) {
		if (!initialized)
			init();
		return projectMap.get(name);
	}

	public List<JejuProject> getProjects() {
		if (!initialized)
			init();
		return this.projects;
	}

	private void saveProjectList() {
		projectMap.clear();
		projectIdMap.clear();
		try {
			XMLMemento memento = XMLMemento
					.createWriteRoot("webx-project-metadata");

			for (JejuProject webxProject : projects) {
				XMLMemento child = memento.createChild("webx-project");
				child.putString("id", webxProject.id);
				child.putString("serverId", webxProject.serverId);
				child.putString("name", webxProject.name);
				child.putString("warProjectName", webxProject.warProjectName);
				child.putString("webxVersion", webxProject.webxVersion);
				child.putString("settingFile", webxProject.settingFile);
				child.putString("antxPropertiesFile",
						webxProject.antxPropertiesFile);
				child.putString("autoconfigCharset",
						webxProject.autoconfigCharset);
				child.putString("antxDestFiles", webxProject.antxDestFiles);
				child.putString("antxIncludeDescriptorPatterns",
						webxProject.antxIncludeDescriptorPatterns);
				child.putString("webRoot", webxProject.webRoot);
				child.putBoolean("isConvert", webxProject.isConvert);
				for (String projectName : webxProject.projectNames) {
					XMLMemento grandChild = child.createChild("project");
					grandChild.putString("name", projectName);
				}
				projectIdMap.put(webxProject.getId(), webxProject);
				projectMap.put(webxProject.getName(), webxProject);
			}
			memento.saveToFile(filename);
		} catch (Exception e) {
			ProjectCreationPlugin.logThrowable(e);
		}
	}

	private void loadProjectList() {
		projectMap.clear();
		projectIdMap.clear();
		projects.clear();
		try {
			XMLMemento memento = XMLMemento.loadMemento(filename);
			XMLMemento[] children = memento.getChildren("webx-project");
			for (XMLMemento child : children) {
				String name = child.getString("name");
				String id = child.getString("id");
				String warProjectName = child.getString("warProjectName");
				JejuProject webxProject = new JejuProject(id, name);
				webxProject.setWarProjectName(warProjectName);
				webxProject.setServerId(child.getString("serverId"));
				webxProject.setWebxVersion(child.getString("webxVersion"));
				webxProject.setSettingFile(child.getString("settingFile"));
				webxProject.setAntxPropertiesFile(child
						.getString("antxPropertiesFile"));
				webxProject.setAutoconfigCharset(child
						.getString("autoconfigCharset"));
				webxProject.setAntxDestFiles(child.getString("antxDestFiles"));
				webxProject.setAntxIncludeDescriptorPatterns(child
						.getString("antxIncludeDescriptorPatterns"));
				webxProject.setWebRoot(child.getString("webRoot"));
				webxProject.setConvert(child.getBoolean("isConvert"));
				XMLMemento[] grandChildren = child.getChildren("project");
				for (XMLMemento grandChild : grandChildren) {
					webxProject.projectNames.add(grandChild.getString("name"));
				}
				projects.add(webxProject);
				projectIdMap.put(webxProject.getId(), webxProject);
				projectMap.put(webxProject.getName(), webxProject);
			}
		} catch (Exception e) {
			ProjectCreationPlugin.logThrowable(e);
		}
	}

	public void updateWebXProject(JejuProject webxProject) {
		if (!initialized)
			init();
		if (webxProject == null)
			return;
		fireProjectEvent(webxProject, EVENT_CHANGED);
		saveProjectList();
	}

	public void addWebXProject(JejuProject webxProject) {
		if (!initialized)
			init();
		if (webxProject == null)
			return;
		if (!projects.contains(webxProject)) {
			registerProject(webxProject);
		} else {
			fireProjectEvent(webxProject, EVENT_CHANGED);
		}
		saveProjectList();
	}

	public void removeWebXProject(JejuProject webxProject) {
		if (!initialized)
			init();
		if (webxProject == null)
			return;
		fireProjectEvent(webxProject, EVENT_REMOVED);
		if (projects.contains(webxProject)) {
			projects.remove(webxProject);
			saveProjectList();
		}
	}

	private void registerProject(JejuProject webxProject) {
		if (!initialized)
			init();
		if (webxProject == null)
			return;
		projects.add(webxProject);
		fireProjectEvent(webxProject, EVENT_ADDED);
	}

	private void fireProjectEvent(final JejuProject project, byte b) {

		if (projectListeners.isEmpty())
			return;

		List<IWebXProjectLifecycleListener> clone = new ArrayList<IWebXProjectLifecycleListener>();
		clone.addAll(projectListeners);
		for (IWebXProjectLifecycleListener listener : clone) {

			try {
				if (b == EVENT_ADDED)
					listener.projectAdded(project);
				else if (b == EVENT_CHANGED)
					listener.projectChanged(project);
				else if (b == EVENT_REMOVED)
					listener.projectRemoved(project);
				else
					listener.reloadProjects();
			} catch (Exception e) {

			}
		}

	}

	public JejuProject findWebXProject(String id) {
		return projectIdMap.get(id);
	}

}
