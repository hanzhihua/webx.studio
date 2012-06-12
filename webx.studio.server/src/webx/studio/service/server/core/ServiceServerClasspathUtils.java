package webx.studio.service.server.core;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;


import org.eclipse.core.runtime.IPath;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.JavaRuntime;

import webx.studio.server.ServerPlugin;

public abstract class ServiceServerClasspathUtils {

	public static final String __LIB_FOLDER = "lib";
	public static final String __JAR_EXT = "jar";

	public static Collection<IRuntimeClasspathEntry> getRuntimeClasspath(
			IPath homePath) {
		Collection<IRuntimeClasspathEntry> cp = new ArrayList<IRuntimeClasspathEntry>();
		File homeDir = homePath.toFile();
		if (homeDir.exists()) {
			String[] jars = homeDir.list();
			for (int i = 0; i < jars.length; i++) {
				if (jars[i].endsWith(__JAR_EXT)) {
					IPath path = homePath.append(jars[i]);
					cp.add(JavaRuntime.newArchiveRuntimeClasspathEntry(path));
				}
			}

		}
		return cp;

	}


	public static Collection<String> getRuntimeClasspath(String home){
		File homeDir = new File(home);
		Collection<String> cp = new ArrayList<String>();
		if (homeDir.exists()) {
			String[] jars = homeDir.list();
			for (int i = 0; i < jars.length; i++) {
				if (jars[i].endsWith(__JAR_EXT)) {
					try {
						cp.add(new File(homeDir,jars[i]).getCanonicalPath());
					} catch (IOException e) {
						ServerPlugin.logError(e);
					}
				}
			}

		}
		return cp;
	}
}
