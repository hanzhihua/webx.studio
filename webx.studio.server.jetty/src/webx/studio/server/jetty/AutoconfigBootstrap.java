package webx.studio.server.jetty;

import java.io.File;

public class AutoconfigBootstrap {

	private static boolean debug = false;

	static {
		try {
			debug = Boolean.parseBoolean(System
					.getProperty(Constants.SHOW_DETAIL));
		} catch (Exception e) {

		}
	}

	public static void main(String[] args) {

//		if (isSkip()) {
//			System.err.println("Skip autoconfig...");
//			return;
//		}
//
//		System.err.println("Autoconfig start...");
//
//		for (String webxProject : getWebxProjects()) {
//			
//			String[] str = webxProject.split("___");
//			
//			System.err.println("WebX Project ["+ webxProject + "] auto config begin !");
//
//			ConfigRuntimeImpl impl = new ConfigRuntimeImpl(System.in,
//					System.out, System.err, getCharset(str[1]));
//
//			impl
//					.setDescriptorPatterns(
//							"META-INF/autoconf/auto-config.xml,autoconf/auto-config.xml",
//							new String());
//			impl.setDests(getDests(str[0]));
//
//			if (getUserProperties(str[2]).length() >0 ) {
//				impl
//						.setUserPropertiesFile(getUserProperties(str[2]),
//								null);
//			}
//
//			impl.start();
//			
//			System.err.println("WebX Project ["+ webxProject + "] auto config done !");
//		}
//
//		System.err.println("Autoconfig was done...");
	}

	public static String[] getDests(String key) {
		return getProp(key, "").split(" ");
	}

	public static String getCharset(String key) {
		return getProp(key, "");
	}

	public static String getUserProperties(String key) {
		return getProp(key, "");
	}

	public static boolean isSkip() {
		String skip = getProp(Constants.AUTOCINFIG_SKIP, "true");
		return Boolean.valueOf(skip).booleanValue();
	}

	public static String[] getWebxProjects() {
		String webxProjects = getProp(
				Constants.AUTOCONFIG_WEBX_PROJECTS_KEY, "");
		return webxProjects.split(File.pathSeparator);
	}

	private static String getProp(String key, String def) {
		printSystemProperty(key);
		return System.getProperty(key, def);
	}
	
	private static String getProp(String key) {
		printSystemProperty(key);
		return System.getProperty(key);
	}

	public static void printSystemProperty(String key) {
		if (!debug)
			return;
		String result = System.getProperty(key);
		if (result != null) {
			System.out.println("-D" + key + "=" + result + " ");
		}
	}

}
