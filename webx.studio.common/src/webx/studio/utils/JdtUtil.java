package webx.studio.utils;

import java.io.File;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.SystemUtils;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaModel;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.launching.JavaRuntime;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public class JdtUtil {

	public static final String JAVA_FILE_EXTENSION = ".java";

	public static final String CLASS_FILE_EXTENSION = ".class";

	private static final String CLASSPATH_FILENAME = ".classpath";

	public static List<IJavaProject> getAllDependingJavaProjects(
			IJavaProject project) throws JavaModelException {
		List<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
		IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
				.getRoot());
		if (model != null) {
			String[] names = project.getRequiredProjectNames();
			IJavaProject[] projects = model.getJavaProjects();
			for (int index = 0; index < projects.length; index++) {
				for (int offset = 0; offset < names.length; offset++) {
					String name = projects[index].getProject().getName();
					if (name.equals(names[offset])) {
						javaProjects.add(projects[index]);
					}
				}
			}
		}
		return javaProjects;
	}

	public static List<IJavaProject> getAllJavaProjects(
			List<String> filterOutProjects) throws JavaModelException {
		if (filterOutProjects == null)
			filterOutProjects = new ArrayList<String>();
		List<IJavaProject> javaProjects = new ArrayList<IJavaProject>();
		IJavaModel model = JavaCore.create(ResourcesPlugin.getWorkspace()
				.getRoot());
		if (model != null) {
			IJavaProject[] projects = model.getJavaProjects();
			for (IJavaProject project : projects) {
				String projectName = project.getProject().getName();
				if (filterOutProjects.contains(projectName))
					continue;
				javaProjects.add(project);
			}
		}
		return javaProjects;
	}

	public static IJavaProject getJavaProject(IProject project)
			throws CoreException {
		if (project.isAccessible()) {
			if (project.hasNature(JavaCore.NATURE_ID)) {
				return (IJavaProject) project.getNature(JavaCore.NATURE_ID);
			}
		}
		return null;
	}

	public static IJavaProject getJavaProject(IResource config) {
		IJavaProject project = JavaCore.create(config.getProject());
		return project;
	}

	public static IClasspathEntry getJreVariableEntry() {
		return JavaRuntime.getDefaultJREContainerEntry();
	}

	public static boolean isClassPathFile(IResource resource) {
		String classPathFileName = resource.getProject().getFullPath()
				.append(CLASSPATH_FILENAME).toString();
		return resource.getFullPath().toString().equals(classPathFileName);
	}

	private static final String JVM_DLL_ORIGNAL = "jvm.dll.changeby.jeju";
	private static final String JVM_DLL_RELOAD = "jvm.dll.reload";
	private static final SecureRandom random = new SecureRandom();

	public static void enableReloadJVM(File jvmHome, String reloadJVMDLL)
			throws Exception {
		if (!SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_WINDOWS_7)
			return;
		if (jvmHome == null || !jvmHome.exists() || !jvmHome.isDirectory()) {
			jvmHome = JavaRuntime.getDefaultVMInstall().getInstallLocation();
		}
		String jvmdllInstallPath = getJVMDLLPath(jvmHome);
		if (StringUtils.isBlank(jvmdllInstallPath)) {
			return;
		}
		File jvmdll = new File(jvmdllInstallPath, "jvm.dll");
		File origJvmdll = new File(jvmdllInstallPath, JVM_DLL_ORIGNAL);
		File reloadJvmdll = new File(jvmdllInstallPath, JVM_DLL_RELOAD);
		File tmp = deleteAllTmpFile(new File(jvmdllInstallPath));
		if (!origJvmdll.exists()) {
			FileUtils.copyFile(jvmdll, origJvmdll);
		}
		if (!reloadJvmdll.exists()) {
			FileUtils.copyFile(new File(reloadJVMDLL), reloadJvmdll);
		}
		jvmdll.renameTo(tmp);
		FileUtils.copyFile(reloadJvmdll, jvmdll);
	}

	public static void disableReloadJVM(File jvmHome) throws Exception {
		if (!SystemUtils.IS_OS_WINDOWS || SystemUtils.IS_OS_WINDOWS_7)
			return;
		if (jvmHome == null || !jvmHome.exists() || !jvmHome.isDirectory()) {
			jvmHome = JavaRuntime.getDefaultVMInstall().getInstallLocation();
		}
		String jvmdllInstallPath = getJVMDLLPath(jvmHome);
		if (StringUtils.isBlank(jvmdllInstallPath)) {
			return;
		}
		File origJvmdll = new File(jvmdllInstallPath, JVM_DLL_ORIGNAL);
		File jvmdll = new File(jvmdllInstallPath, "jvm.dll");
		File tmp = deleteAllTmpFile(new File(jvmdllInstallPath));
		if (!origJvmdll.exists())
			return;

		jvmdll.renameTo(tmp);
		FileUtils.copyFile(origJvmdll, jvmdll);
	}

	private static File deleteAllTmpFile(File dir) {
		File returnFile = null;
		Collection files = FileUtils.listFiles(dir, new String[] { "tmp" },
				false);
		for (Object file : files) {
			if (FileUtils.deleteQuietly((File) file) && returnFile == null) {
				returnFile = (File) file;
			}
		}

		if (returnFile == null) {
			long n = random.nextLong();
			if (n == Long.MIN_VALUE) {
				n = 0; // corner case
			} else {
				n = Math.abs(n);
			}
			returnFile = new File(dir, "jvm.dll" + n + ".tmp");
		}
		return returnFile;
	}

	private static String getJVMDLLPath(File file) {
		String path = file.getAbsolutePath() + "/bin/client";
		File f = new File(path, "jvm.dll");
		if (f.exists()) {
			return path;
		}
		path = file.getAbsolutePath() + "/jre/bin/client";
		f = new File(path, "jvm.dll");
		if (f.exists()) {
			return path;
		}

		// path = file.getAbsolutePath() + "/bin/server";
		// f = new File(path, "jvm.dll");
		// if (f.exists()) {
		// return path;
		// }
		// path = file.getAbsolutePath() + "/jre/bin/server";
		// f = new File(path, "jvm.dll");
		// if (f.exists()) {
		// return path;
		// }
		return null;
	}

	public static IType getJavaType(String projectName, String className)
	throws CoreException {
		IProject project = ProjectUtil.getProject(projectName);
		return getJavaType(project, className);
	}

	public static IType getJavaType(IProject project, String className)
			throws CoreException {
		IJavaProject javaProject = getJavaProject(project);
		if (className != null) {
			int pos = className.lastIndexOf('$');
			if (pos > 0) {
				className = className.replace('$', '.');
			}
			IType type = null;
			if (javaProject != null) {
				type = javaProject.findType(className,
						new NullProgressMonitor());
				if (type != null) {
					return type;
				}
				for (IProject refProject : project.getReferencedProjects()) {
					IJavaProject refJavaProject = getJavaProject(refProject);
					if (refJavaProject != null) {
						type = refJavaProject.findType(className);
						if (type != null) {
							return type;
						}
					}
				}
			}
		}
		return null;
	}

	public static ICompilationUnit createClass(IJavaProject javaProject,String packageName,String className,String content){
		IPackageFragment pack;
		try {
			pack = javaProject.getPackageFragmentRoots()[0].createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(className+".java", content, false, null);
			return cu;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static ICompilationUnit createClass(IPackageFragmentRoot root,String packageName,String className,String content){
		IPackageFragment pack;
		try {
			pack = root.createPackageFragment(packageName, false, null);
			ICompilationUnit cu = pack.createCompilationUnit(className+".java", content, false, null);
			return cu;
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		return null;

	}

	public static CompilationUnit getParserASTRoot(
            ICompilationUnit compilationUnit, IProgressMonitor monitor) {
        ASTParser parser = ASTParser.newParser(AST.JLS3);
        parser.setSource(compilationUnit);
        parser.setResolveBindings(true);
        return (CompilationUnit) parser.createAST(monitor);
    }

}
