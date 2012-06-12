package webx.studio.utils;

import java.io.File;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public class PathUtil {

	public static File getFile(String pathPattern, String basedirSysProp,
			String altLocationSysProp) {

		String path = System.getProperty(altLocationSysProp);

		if (StringUtils.isEmpty(path)) {
			String basedir = System.getProperty(basedirSysProp);
			if (basedir == null) {
				basedir = System.getProperty("user.dir");
			}

			basedir = basedir.replaceAll("\\\\", "/");
			basedir = basedir.replaceAll("\\$", "\\\\\\$");

			path = pathPattern.replaceAll("\\$\\{" + basedirSysProp + "\\}",
					basedir);
			path = path.replaceAll("\\\\", "/");

			return new File(path).getAbsoluteFile();
		} else {
			return new File(path).getAbsoluteFile();
		}

	}

	public static String getLocation(IPath path ){
		IWorkspaceRoot root = ResourcesPlugin.getWorkspace().getRoot();
		IFolder folder = root.getFolder(path);
		return folder.getLocation().toOSString();
	}

}
