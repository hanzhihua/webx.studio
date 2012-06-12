package webx.studio.utils;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.part.FileEditorInput;
import org.osgi.framework.Bundle;

import webx.studio.StudioCommonPlugin;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public abstract class ProjectUtil {

	public static String MAVEN_NATURE_ID = "org.maven.ide.eclipse.maven2Nature";

	public static IResource getSelectedResource(ISelection selection) {

		if (selection instanceof TreeSelection) {// could be project explorer
			Object first = ((TreeSelection) selection).getFirstElement();
			if (first instanceof IResource)
				return (IResource) first;
			else if (first instanceof IJavaProject)
				return ((IJavaProject) first).getResource();
			else if (first instanceof IProject) {
				try {
					return ((IProject) first).members()[0];
				} catch (CoreException e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return null;
	}

	public static IResource getSelectedResource(IWorkbenchWindow window) {

		ISelection selection = window.getSelectionService().getSelection();

		IResource ir = getSelectedResource(selection);
		if (ir != null)
			return ir;

		IEditorInput editorinput = window.getActivePage().getActiveEditor()
				.getEditorInput();
		FileEditorInput fileEditorInput = (FileEditorInput) editorinput
				.getAdapter(FileEditorInput.class);
		if (fileEditorInput == null || fileEditorInput.getFile() == null) {
			return null;
		}
		return fileEditorInput.getFile();
	}

	public static IFile getFile(IEditorInput editorinput) {
		FileEditorInput fileEditorInput = (FileEditorInput) editorinput
				.getAdapter(FileEditorInput.class);

		if (fileEditorInput == null || fileEditorInput.getFile() == null) {
			return null;
		}
		return fileEditorInput.getFile();
	}

	public static IProject getProject(IWorkbenchWindow window) {
		IEditorInput editorinput = window.getActivePage().getActiveEditor()
				.getEditorInput();
		FileEditorInput fileEditorInput = (FileEditorInput) editorinput
				.getAdapter(FileEditorInput.class);

		if (fileEditorInput == null || fileEditorInput.getFile() == null) {
			return null;
		}
		return fileEditorInput.getFile().getProject();
	}

	public static IProject getProject(IEditorInput editorinput) {
		FileEditorInput fileEditorInput = (FileEditorInput) editorinput
				.getAdapter(FileEditorInput.class);

		if (fileEditorInput == null || fileEditorInput.getFile() == null) {
			return null;
		}
		return fileEditorInput.getFile().getProject();
	}

	private static String[] DEFAULT_WEBAPP_DIR_SET = new String[] {
			"WebContent", "src/main/webapp","src/webroot","WebRoot","src/webapp" };

	public static IContainer getWebappFolder(IProject project, String webappdir) {

		IContainer folder = null;
		if ("/".equals(webappdir))
			folder = project;
		else
			folder = project.getFolder(webappdir);

		return folder;
	}

	public static boolean isMavenProject(IProject project) {
		try {
			return project != null && project.hasNature(MAVEN_NATURE_ID);
		} catch (CoreException e) {
			return false;
		}
	}

	public static IRuntimeClasspathEntry[] getLibs(Bundle bundle)
			throws Exception {
		List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();
		URL url = bundle.getEntry("/");
		String path = FileLocator.resolve(url).getPath();
		List<String> classpath = new ArrayList<String>();
		File parentDir = new File(path, "lib");
		if (parentDir.exists()) {
			for (File file : parentDir.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					entries.add(JavaRuntime
							.newArchiveRuntimeClasspathEntry(new Path(file
									.getAbsolutePath())));
				}
			}
		}
		File binFolder = new File(path, "bin");
		if (binFolder.exists()) {
			entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(
					binFolder.getAbsolutePath())));
		} else {
			entries.add(JavaRuntime.newArchiveRuntimeClasspathEntry(new Path(
					binFolder.getParentFile().getAbsolutePath())));
		}
		return entries.toArray(new IRuntimeClasspathEntry[0]);
	}

	public static IRuntimeClasspathEntry[] getAutoConfigLibs()throws Exception {
		List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();
		File installDir = StudioCommonPlugin.getInstallOsFile();
		if(installDir != null && installDir.exists() && installDir.isDirectory()){
			File parentDir = new File(installDir, "jars");
			if(parentDir.exists() && parentDir.isDirectory()){
				entries.add(JavaRuntime
						.newArchiveRuntimeClasspathEntry(new Path(new File(parentDir,"antx-common-1.0.jar")
								.getAbsolutePath())));
				entries.add(JavaRuntime
						.newArchiveRuntimeClasspathEntry(new Path(new File(parentDir,"antx-config-1.0.jar")
								.getAbsolutePath())));
			}

		}
		return entries.toArray(new IRuntimeClasspathEntry[0]);

	}

	public static String getReloadClasspath(Bundle bundle) throws Exception {
		List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();
		URL url = bundle.getEntry("/");
		String path = FileLocator.resolve(url).getPath();
		File parentDir = new File(path, "reload");
		String reloadClasspath ="";
		if (parentDir.exists()) {
			for (File file : parentDir.listFiles()) {
				if (file.getName().endsWith(".jar")) {
					if(StringUtils.isNotBlank(reloadClasspath)){
						reloadClasspath+=File.pathSeparatorChar;
					}
					reloadClasspath += file.getAbsolutePath();
				}
			}
		}
		return reloadClasspath;
	}

	public static String getReloadJVM(Bundle bundle) throws Exception {
		String path = FileLocator.resolve(bundle.getEntry("/")).getPath();
		File parentDir = new File(path, "reload");
		if (parentDir.exists()) {
			for (File file : parentDir.listFiles()) {
				if (StringUtils.equals(file.getName(), "jvm.dll")) {
					return file.getAbsolutePath();
				}
			}
		}
		return "";
	}

	public static IRuntimeClasspathEntry[] getLibs(Bundle bundle,
			String[] filelist) throws MalformedURLException, URISyntaxException {

		List<IRuntimeClasspathEntry> entries = new ArrayList<IRuntimeClasspathEntry>();
		URL installUrl = bundle.getEntry("/lib/");

		try {
			for (String filepath : filelist) {
				URL fileUrl = FileLocator.toFileURL(new URL(installUrl,
						filepath));
				entries.add(JavaRuntime
						.newArchiveRuntimeClasspathEntry(new Path(fileUrl
								.getPath())));
			}
			if (entries.size() == 0) {
				throw new IllegalStateException("RJR finding jar failed");
			}
			URL rootUrl = bundle.getEntry("/");
			String path = FileLocator.resolve(rootUrl).getPath();
			File binFolder = new File(path, "bin");
			if (binFolder.exists()) {
				entries.add(JavaRuntime
						.newArchiveRuntimeClasspathEntry(new Path(binFolder
								.getAbsolutePath())));
			} else {
				entries.add(JavaRuntime
						.newArchiveRuntimeClasspathEntry(new Path(binFolder
								.getParentFile().getAbsolutePath())));
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		return entries.toArray(new IRuntimeClasspathEntry[0]);
	}

	public static String detectDefaultWebappdir(String projectName) {
		return detectDefaultWebappdir(getProject(projectName));
	}

	public static String detectDefaultWebappdir(IProject project) {
		if (project != null) {
			for (String path : DEFAULT_WEBAPP_DIR_SET) {
				IFolder file = project.getFolder(new Path(path + "/WEB-INF"));
				if (file.exists()) {
					// return path.toOSString();
					return project.getFolder(new Path(path)).getLocation()
							.toOSString();
				}
			}
		}
		return "";
	}

	public static String detectDefaultWebappdir(IProject project,String webappDirs) {
		if (project != null) {
			String[] paths = StringUtils.split(webappDirs,",");
			if(paths == null)
				return "";
			for (String path : paths) {
				IFolder file = project.getFolder(new Path(path + "/WEB-INF"));
				if (file.exists()) {
					// return path.toOSString();
					return project.getFolder(new Path(path)).getLocation()
							.toOSString();
				}
			}
		}
		return "";
	}

	public static IProject getProject(String projectName) {
		IWorkspace workspace = ResourcesPlugin.getWorkspace();
		IProject project = null;
		IStatus status = workspace.validateName(projectName, IResource.PROJECT);
		if (status.isOK()) {
			project = ResourcesPlugin.getWorkspace().getRoot()
					.getProject(projectName);
			if (!project.exists()) {
				return null;
			}
			if (!project.isOpen()) {
				return null;
			}
		}
		return project;
	}

	public static void addProjectNature(IProject project, String nature,
			IProgressMonitor monitor) throws CoreException {
		if (project != null && nature != null) {
			if (!project.hasNature(nature)) {
				IProjectDescription desc = project.getDescription();
				String[] oldNatures = desc.getNatureIds();
				String[] newNatures = new String[oldNatures.length + 1];
				newNatures[0] = nature;
				if (oldNatures.length > 0) {
					System.arraycopy(oldNatures, 0, newNatures, 1,
							oldNatures.length);
				}
				desc.setNatureIds(newNatures);
				project.setDescription(desc, monitor);
			}
		}
	}

	public static void addProjectNatures(IProject project,
			String[] newNatureIds, IProgressMonitor monitor)
			throws CoreException {
		if (null == project || null == newNatureIds) {
			return;
		}

		IProjectDescription description = project.getDescription();

		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length
				+ newNatureIds.length];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

		int i = prevNatures.length;
		for (String natureId : newNatureIds) {
			if (project.hasNature(natureId)) {
				continue;
			}
			newNatures[i] = natureId;
			i = i + 1;
		}

		description.setNatureIds(newNatures);
		project.setDescription(description, monitor);
	}

	public static void removeProjectNature(IProject project, String nature,
			IProgressMonitor monitor) throws CoreException {
		if (project != null && nature != null) {
			if (project.exists() && project.hasNature(nature)) {

				// now remove project nature
				IProjectDescription desc = project.getDescription();
				String[] oldNatures = desc.getNatureIds();
				String[] newNatures = new String[oldNatures.length - 1];
				int newIndex = oldNatures.length - 2;
				for (int i = oldNatures.length - 1; i >= 0; i--) {
					if (!oldNatures[i].equals(nature)) {
						newNatures[newIndex--] = oldNatures[i];
					}
				}
				desc.setNatureIds(newNatures);
				project.setDescription(desc, monitor);
			}
		}
	}

	public static boolean hasNature(IResource resource, String natureId) {
		if (resource != null && resource.isAccessible()) {
			IProject project = resource.getProject();
			if (project != null) {
				try {
					return project.hasNature(natureId);
				} catch (CoreException e) {
				}
			}
		}
		return false;
	}





}
