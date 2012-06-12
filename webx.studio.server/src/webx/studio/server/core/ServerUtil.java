package webx.studio.server.core;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.internal.launching.DefaultProjectClasspathEntry;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.server.ServerPlugin;
import webx.studio.utils.PathUtil;
import webx.studio.utils.ProjectUtil;

public abstract class ServerUtil {

	public final static String SPEA_CHAR = "::";

	public static Server getServer(ILaunchConfiguration configuration)
			throws CoreException {
		String serverId = configuration.getAttribute(Server.ATTR_SERVER_ID,
				(String) null);

		if (serverId != null)
			return ServerCore.findServer(serverId);
		return null;
	}

	public static List<IProject> getAllRelativeProjects(Server server) {

		List<IProject> projects = new ArrayList<IProject>();

		String[] names = server.getWebXProjectNames();

		for (String name : names) {
			JejuProject webxProject = JejuProjectCore.getWebXProject(name);
			if (webxProject == null)
				continue;
			for (String eclipseProjectName : webxProject.getProjectNames()) {
				projects.add(ProjectUtil.getProject(eclipseProjectName));
			}
		}

		return projects;
	}

	public static Map<String, IProject> getAllRelativeWarProjects(Server server) {

		Map<String, IProject> projectMap = new HashMap<String, IProject>();

		String[] names = ((Server) server).getOnlyWebXProjectNames();

		for (String name : names) {
			JejuProject webxProject = JejuProjectCore.getWebXProject(name);
			if (webxProject == null)
				continue;
			projectMap.put(name,
					ProjectUtil.getProject(webxProject.getWarProjectName()));
		}

		return projectMap;
	}

	public static List<WebModule> getWebModules(Server server) {

		List<WebModule> modules = new ArrayList<WebModule>();
		for (String webxProjectName : server.getWebXProjectNames()) {
			WebModule module = new WebModule();
			String[] strs = webxProjectName.split(SPEA_CHAR);
			if (strs.length == 2) {
				module.webxProjectName = strs[0];
				module.contextPath = formatContextPath(strs[1]);
			} else {
				module.webxProjectName = strs[0];
				module.contextPath = formatContextPathDefault(strs[0]);
			}
			JejuProject webxProject = JejuProjectCore
					.getWebXProject(module.webxProjectName);
			if (webxProject != null && webxProject.getWarProjectName() != null
					&& webxProject.getWarProjectName().trim().length() > 0) {
				String webRoot = webxProject.getWebRoot();
				if (StringUtils.isNotBlank(webRoot) && new File(webRoot).exists()) {
					module.webappdir = new File(webRoot).getAbsolutePath();
				} else {
					module.webappdir = ProjectUtil
							.detectDefaultWebappdir(webxProject
									.getWarProjectName());
				}
			}
			modules.add(module);
		}
		return modules;

	}

	public static String formatContextPath(String str) {
		if (str == null || str.trim().length() == 0)
			return "/";
		if (!str.startsWith("/"))
			str = "/" + str;
		return str;
//		return "/";
	}

	public static String formatContextPathDefault(String str) {
		return "/";
	}

	private final static String WEBMODULE_PREFIX = "webmodule.";

	public static String getWebmoduleContextKey(String webxProject) {
		return WEBMODULE_PREFIX + webxProject + ".context";
	}

	public static String getWemoduleWebappdirKey(String webxProject) {
		return WEBMODULE_PREFIX + webxProject + ".webappdir";
	}

	public static String getWebmoduleClasspathKey(String webxProject) {
		return WEBMODULE_PREFIX + webxProject + ".classpath";
	}

	private final static String AUTOCONFIG_PREFIX = "autoconfig.";

	public static String getAutoconfigDestsKey(String webxProject) {
		return AUTOCONFIG_PREFIX + webxProject + ".dests";
	}

	public static String getAutoconfigCharsetKey(String webxProject) {
		return AUTOCONFIG_PREFIX + webxProject + ".charset";
	}

	public static String getAutoconfigUserPropertiesKey(String webxProject) {
		return AUTOCONFIG_PREFIX + webxProject + ".userproperties";
	}

	public static File getTmpServerDir(Server server, String webxProject) {
		File dir = new File(System.getProperty("java.io.tmpdir"),
				server.getName() + "_" + webxProject + ".rar");
		if (!dir.exists())
			dir.mkdir();
		return dir;
	}

	public static String getWebappClasspath(Server server, String webxProject,
			List<String> filtedPaths) throws Exception {

		JejuProject jejuProject = JejuProjectCore.getWebXProject(webxProject);
		if (jejuProject == null)
			return "";

		File tmpDir = getTmpServerDir(server, webxProject);
		List<String> locations = new ArrayList<String>();
		for (String projectName : jejuProject.getProjectNames()) {
			IProject project = ProjectUtil.getProject(projectName);
			if (project == null)
				continue;
			IJavaProject javaProject = JavaCore.create(project);
			if (javaProject == null)
				continue;
			locations
					.add(PathUtil.getLocation(javaProject.getOutputLocation()));
		}
		if (filtedPaths != null) {
			for (String path : filtedPaths) {
				locations.add(path);
			}
		}
		String[] webAppClasspathArray = (String[]) locations
				.toArray(new String[locations.size()]);
		String webAppClasspath = "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < webAppClasspathArray.length; i++) {
			String path = webAppClasspathArray[i];
			File jarFile = null;
			if (StringUtils.isNotBlank(path)
					&& (path.endsWith("test-classes")
							|| path.endsWith("test-classes\\") || path
							.endsWith("test-classes/")))
				continue;
			boolean isContinue = false;
			if (path.endsWith(".jar")) {
				String[] tmps = org.apache.commons.lang.StringUtils.split(path,
						File.separator);
				String tmp = tmps[tmps.length - 1];
				for (String projectName : jejuProject.getProjectNames()) {
					if (tmp.startsWith(projectName)) {
						isContinue = true;
						break;
					}
				}
				jarFile = new File(path);
			}
			if (isContinue)
				continue;
			if (sb.length() > 0) {
				sb.append(File.pathSeparator);
				sb.append(getConvertedPath(path, tmpDir,server.isWithoutAutoconfig()));
			} else {
				sb.append(getConvertedPath(path, tmpDir,server.isWithoutAutoconfig()));
			}

		}
		webAppClasspath = sb.toString();

		if (webAppClasspath.length() > 1024) {
			File f = prepareClasspathFile(server.getName() + "_" + webxProject,
					webAppClasspath);
			webAppClasspath = "file://" + f.getAbsolutePath();
		}

		return webAppClasspath;

	}

	private static String getConvertedPath(String path, File tmpDir) {
		return getConvertedPath(path, tmpDir,false);
	}

	private static String getConvertedPath(String path, File tmpDir,boolean changeExist) {
		if (StringUtils.isBlank(path))
			return "";
		File jarFile = new File(path);
		if (jarFile != null && jarFile.exists() && jarFile.isFile()
				&& tmpDir != null && tmpDir.isDirectory()) {
			try {
				File newFile = new File(tmpDir, jarFile.getName());
				if (!newFile.exists() || changeExist)
					FileUtils.copyFileToDirectory(jarFile, tmpDir);
				return newFile.getAbsolutePath();
			} catch (IOException e) {
				ServerPlugin.logError(e);
				return path;
			}

		} else {
			return path;
		}
	}

	public static void copyClasspath(Server server, String webxProject)
			throws Exception {
		File tmpDir = getTmpServerDir(server, webxProject);
		Map<String, IProject> warProjectMap = getAllRelativeWarProjects(server);
		IProject warProject = warProjectMap.get(webxProject);
		IJavaProject proj = JavaCore.create(warProject);
		IRuntimeClasspathEntry[] entries = JavaRuntime
				.computeUnresolvedRuntimeClasspath(proj);

		List<IRuntimeClasspathEntry> entryList = new ArrayList<IRuntimeClasspathEntry>(
				entries.length);
		IRuntimeClasspathEntry projJreEntry = JavaRuntime.computeJREEntry(proj);

		for (int i = 0; i < entries.length; i++) {
			IRuntimeClasspathEntry entry = entries[i];
			if (entry.equals(projJreEntry))
				continue;
			entryList.add(entry);
		}
		entries = entryList.toArray(new IRuntimeClasspathEntry[0]);

		Set<String> locations = new LinkedHashSet<String>();
		for (int i = 0; i < entries.length; i++) {
			IRuntimeClasspathEntry entry = entries[i];
			if (entry instanceof DefaultProjectClasspathEntry) {
				IRuntimeClasspathEntry[] tmpEntries = JavaRuntime
						.resolveRuntimeClasspathEntry(entry, proj);
				for (IRuntimeClasspathEntry tmpEntry : tmpEntries) {
					locations.add(tmpEntry.getLocation());
				}

			}
			if (entry.getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
				String location = entry.getLocation();
				if (location != null) {
					locations.add(location);
				}
			}
		}
		for (String location : locations) {
			File jarFile = new File(location);
			if (jarFile != null && jarFile.exists() && jarFile.isFile()
					&& tmpDir != null && tmpDir.isDirectory()) {
				FileUtils.copyFileToDirectory(jarFile, tmpDir);
			}
		}
	}

	public static String getWebappClasspathByEclipse(
			ILaunchConfiguration configuration, Server server,
			String webxProject) throws CoreException {
		Map<String, IProject> warProjectMap = getAllRelativeWarProjects(server);
		IProject warProject = warProjectMap.get(webxProject);
		if (warProject == null)
			return "";
		File tmpDir = getTmpServerDir(server, webxProject);
		IJavaProject proj = JavaCore.create(warProject);
		IRuntimeClasspathEntry[] entries = JavaRuntime
				.computeUnresolvedRuntimeClasspath(proj);
		List<IRuntimeClasspathEntry> entryList = new ArrayList<IRuntimeClasspathEntry>(
				entries.length);
		IRuntimeClasspathEntry projJreEntry = JavaRuntime.computeJREEntry(proj);

		for (int i = 0; i < entries.length; i++) {
			IRuntimeClasspathEntry entry = entries[i];
			if (entry.equals(projJreEntry))
				continue;
			entryList.add(entry);
		}
		entries = entryList.toArray(new IRuntimeClasspathEntry[0]);

		Set<String> locations = new LinkedHashSet<String>();
		for (int i = 0; i < entries.length; i++) {
			IRuntimeClasspathEntry entry = entries[i];
			if (entry instanceof DefaultProjectClasspathEntry) {
				IRuntimeClasspathEntry[] tmpEntries = JavaRuntime
						.resolveRuntimeClasspathEntry(entry, configuration);
				for (IRuntimeClasspathEntry tmpEntry : tmpEntries) {
					locations.add(tmpEntry.getLocation());
				}

			}
			if (entry.getClasspathProperty() == IRuntimeClasspathEntry.USER_CLASSES) {
				String location = entry.getLocation();
				if (location != null) {
					locations.add(location);
				}
			}
		}
		String[] webAppClasspathArray = (String[]) locations
				.toArray(new String[locations.size()]);
		String webAppClasspath = "";

		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < webAppClasspathArray.length; i++) {
			String path = webAppClasspathArray[i];
			if (StringUtils.isNotBlank(path)
					&& (path.endsWith("test-classes")
							|| path.endsWith("test-classes\\") || path
							.endsWith("test-classes/")))
				continue;
			if (sb.length() > 0) {
				sb.append(File.pathSeparator);
				sb.append(getConvertedPath(path, tmpDir,server.isWithoutAutoconfig()));
			} else {
				sb.append(getConvertedPath(path, tmpDir,server.isWithoutAutoconfig()));
			}
		}
		webAppClasspath = sb.toString();

		if (webAppClasspath.length() > 1024) {
			File f = prepareClasspathFile(server.getName() + "_" + webxProject,
					webAppClasspath);
			webAppClasspath = "file://" + f.getAbsolutePath();
		}

		return webAppClasspath;
	}

	private static File prepareClasspathFile(String key, String classpath) {
		IPath path = ServerPlugin.getDefault().getStateLocation()
				.append(key + ".classpath");
		File f = path.toFile();
		try {
			BufferedWriter out = new BufferedWriter(new OutputStreamWriter(
					new FileOutputStream(f, false), "UTF8"));
			out.write(classpath);
			out.close();
			return f;
		} catch (IOException e) {
			return null;
		}

	}

	public static String getWebXProjectName(String nameAndPath) {
		if (nameAndPath == null)
			return "";
		int index = nameAndPath.indexOf("::");
		if (index == -1) {
			return nameAndPath;
		}
		return nameAndPath.substring(0, index);
	}

	public static String getContextPath(String nameAndPath) {
		if (nameAndPath == null)
			return "";
		int index = nameAndPath.indexOf("::");
		if (index == -1) {
			return formatContextPathDefault(nameAndPath);
		}
		return nameAndPath.substring(index + 2);
	}

	public static List<String> getCanBeDeployedWebXProjects(Server server) {
		List<String> canBeDeployedWebXProjects = new ArrayList<String>();
		List<JejuProject> totalWebXProjects = JejuProjectCore
				.getWebXProjectList();
		List<String> alreadyBeDeployedWebXProjects = Arrays.asList(server
				.getOnlyWebXProjectNames());
		for (JejuProject project : totalWebXProjects) {
			if (alreadyBeDeployedWebXProjects.contains(project.getName()))
				continue;

			canBeDeployedWebXProjects.add(project.getName());
		}
		return canBeDeployedWebXProjects;

	}

	public static List<String> getCanBeDeployedWebXProjects(
			List<String> webxProjects) {
		if (webxProjects == null)
			return new ArrayList<String>();
		List<String> canBeDeployedWebXProjects = new ArrayList<String>();
		List<JejuProject> totalWebXProjects = JejuProjectCore
				.getWebXProjectList();
		for (JejuProject project : totalWebXProjects) {
			boolean canBeDeploy = true;
			for (String webxProject : webxProjects) {
				if (org.apache.commons.lang.StringUtils.equals(
						project.getName(), getWebXProjectName(webxProject))) {
					canBeDeploy = false;
					break;
				}
			}
			if (canBeDeploy)
				canBeDeployedWebXProjects.add(project.getName());
		}
		return canBeDeployedWebXProjects;

	}

	public static List<String> addWebXProjectToServer(Server server,
			String webxProjectAndPath) {
		String[] webxProjectNames = server.getWebXProjectNames();
		String[] newArray = new String[webxProjectNames.length + 1];
		System.arraycopy(webxProjectNames, 0, newArray, 0,
				webxProjectNames.length);
		newArray[webxProjectNames.length] = webxProjectAndPath;
		server.setWebXProjects(Arrays.asList(newArray));
		return Arrays.asList(newArray);
	}

	public static List<String> removeWebXProjectFromServer(Server server,
			String webxProjectName) {
		String[] webxProjectNames = server.getWebXProjectNames();
		List<String> list = new ArrayList<String>();
		for (String name : webxProjectNames) {
			if (name.endsWith(webxProjectName)
					|| name.startsWith(webxProjectName + "::"))
				continue;
			list.add(name);
		}
		server.setWebXProjects(list);
		return list;

	}

}
