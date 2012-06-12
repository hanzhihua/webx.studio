package webx.studio.server.jetty;

import java.io.File;
import java.io.IOException;

import org.eclipse.jetty.util.resource.Resource;
import org.eclipse.jetty.webapp.WebAppClassLoader;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author zhihua.hanzh
 *
 */
public class ProjectClassLoader extends WebAppClassLoader {

	private boolean initialized = false;

	public ProjectClassLoader(WebAppContext context, String projectClassPath,
			Configs configs) throws IOException {
		this(context, projectClassPath, configs, false);
	}

	public ProjectClassLoader(WebAppContext context, String projectClassPath,
			Configs configs, boolean logger) throws IOException {
		super(context);

		if(configs.getProviderReloadFunction()){
			System.err.println("Reload function enable!");
			String[] tokens = configs.getReloadClasspath().split(String
					.valueOf(File.pathSeparatorChar));
			for (String entry : tokens) {
				super.addClassPath(entry);
			}
		}

		if (projectClassPath != null) {
			String[] tokens = projectClassPath.split(String
					.valueOf(File.pathSeparatorChar));
			for (String entry : tokens) {
				if (configs.getExcludedclasspath() != null && entry.matches(configs.getExcludedclasspath())) {
					System.err.println("ProjectClassLoader excluded entry="
							+ entry);
				} else {
					if (logger)
						System.err
								.println("ProjectClassLoader: entry=" + entry);
					super.addClassPath(entry);
				}
			}
		}

		initialized = true;
	}

	public Class loadClass(String name) throws ClassNotFoundException {
//		try {
//			return loadClass(name, false);
//		} catch (NoClassDefFoundError e) {
//			throw new ClassNotFoundException(name);
//		}
		return loadClass(name, false);
	}

	public void addClassPath(String classPath) throws IOException {

		if (initialized) {
			if (!classPath.endsWith("WEB-INF/classes/"))
				return;
		}
		super.addClassPath(classPath);
		return;
	}

	public void addJars(Resource lib) {
		if (initialized) {
			return;
		}
		super.addJars(lib);
		return;
	}
}