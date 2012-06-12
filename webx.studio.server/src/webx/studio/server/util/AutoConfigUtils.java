package webx.studio.server.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IProject;

public abstract class AutoConfigUtils {

	public static List<String> getDestDirs(IProject project) {

		List<String> result = new ArrayList<String>();
		findFolders(project.getLocation().toString(), result);
		return result;
	}

	public static  void findFolders(String path, List<String> result) {
		File file = new File(path);
		if (!needIgnore(file)) {
			File[] childrens = file.listFiles();
			for (int i = 0; i < childrens.length; i++) {
				File children = childrens[i];
				if (children.isDirectory() && children.list().length >= 1) {
					if(isAutoConfigParentDir(children)){
						File antoConfig = new File(children,"auto-config.xml");
						if(!antoConfig.exists() || !antoConfig.isFile()){
							continue;
						}
						File tmpFile = children.getParentFile();
						if (tmpFile.getName().equals("META-INF")) {
							result.add(tmpFile.getParentFile()
									.getAbsolutePath());
						} else {
							result.add(tmpFile.getAbsolutePath());
						}
						break;
					} else {
						findFolders(children.getAbsolutePath(), result);
					}
				} else {
					continue;
				}
			}
		}
	}

	public static boolean isAutoConfigParentDir(File dir){
		if(dir == null)
			return false;
		String dirName = dir.getName();
		for(String tmp:AutoConfigConstants.AUTOCONF_PARENT_NAMES){
			if(StringUtils.endsWithIgnoreCase(dirName, tmp))
				return true;
		}
		return false;
	}

	public static boolean needIgnore(File dir){
		if(dir == null)
			return true;
		String dirName = dir.getName();
		for(String tmp:AutoConfigConstants.IGNORE_DIRS_WHEN_SCAN){
			if(StringUtils.endsWithIgnoreCase(dirName, tmp))
				return true;
		}
		return false;
	}

}
