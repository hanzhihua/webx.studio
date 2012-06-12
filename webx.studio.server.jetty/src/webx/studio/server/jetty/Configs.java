package webx.studio.server.jetty;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Configs {

	private Integer port;
	private Integer sslport;
	private String keystore;
	private String password;
	private String keyPassword;
	private Integer scanIntervalSeconds;
	private Boolean enablescanner;
	private Boolean scanWEBINF;
	private Boolean parentLoaderPriority;
	private Boolean enablessl;
	private Boolean needClientAuth;
	private Boolean enableJNDI;
	private Boolean debugMode;
	private String reloadClasspath;
	private String classpathProvider;
	private Boolean providerReloadFunction;

	public String getClasspathProvider() {
		return classpathProvider;
	}



	public String getReloadClasspath() {
		return reloadClasspath;
	}



	public Boolean getDebugMode() {
		return debugMode;
	}

	private String configurationClasses;
	private String resourceMapping;
	private String excludedclasspath;
	private int eclipseListener;

	private List<WebApp> webapps = new ArrayList<WebApp>();

	private static boolean debug = false;

	public Configs() {

		debug = getBooleanProp(Constants.SHOW_DETAIL, false);
		initWebApps();

		eclipseListener = getIntProp(Constants.ECLIPSE_LISTENER, -1);
		port = getIntProp(Constants.PORT);
		sslport = getIntProp(Constants.SSL_PORT);
		keystore = getProp(Constants.KEY_STORE);
		password = getProp(Constants.PASSWORD);

		excludedclasspath = getProp(Constants.EXCLUDED_CLASSPATH);
		try {
			if (excludedclasspath != null) {
				Pattern.compile(excludedclasspath);
			}
		} catch (PatternSyntaxException ex) {
			System.err
					.println("Excluded classpath setting occur regex syntax error, skipped. \n(Error Message:"
							+ ex.getMessage() + ")");
			excludedclasspath = null;
		}

		keyPassword = getProp(Constants.KEY_PASSWORD);
		scanIntervalSeconds = getIntProp(Constants.SCAN_INTERVAL_SECONDS);

		enablescanner = getBooleanProp(Constants.ENABLE_SCANNER);

		scanWEBINF = getBooleanProp(Constants.SCAN_WEBINF);

		parentLoaderPriority = getBooleanProp(Constants.PARENT_LOADER_PRIORITY, true);

		enablessl = getBooleanProp(Constants.ENABLE_SSL);

		needClientAuth = getBooleanProp(Constants.NEED_CLIENT_AUTH);

		enableJNDI = getBooleanProp(Constants.ENABLE_JNDI);

		configurationClasses = getProp(Constants.CONFIGURATION_CLASSES, "");

		resourceMapping = trimQuote(getProp(Constants.RESOURCE_MAPPING, ""));

		debugMode = getBooleanProp(Constants.DEBUG_MODE);
		reloadClasspath = getProp(Constants.RELOAD_CLASSPATH,"");
		
		classpathProvider = getProp(Constants.CLASSPATH_PROVIDER);
		
		providerReloadFunction = getBooleanProp(Constants.PROVIDER_RELOAD_FUNCTION);
		
		

	}



	public Boolean getProviderReloadFunction() {
		return providerReloadFunction;
	}



	private static String getProp(String key) {
		printSystemProperty(key);
		return System.getProperty(key);
	}

	private static String getProp(String key, String def) {
		printSystemProperty(key);
		return System.getProperty(key, def);
	}

	private static Integer getIntProp(String key) {
		printSystemProperty(key);
		return Integer.getInteger(key);
	}

	private static Integer getIntProp(String key, int def) {
		printSystemProperty(key);
		return Integer.getInteger(key, def);
	}

	private static Boolean getBooleanProp(String key) {
		printSystemProperty(key);
		return Boolean.getBoolean(key);
	}

	// debug tool
	public static void printSystemProperty(String key) {
		if (!debug)
			return;
		String result = System.getProperty(key);
		if (result != null) {
			System.out.println("-D" + key + "=" + result + " ");
		}
	}

	public Integer getPort() {
		return port;
	}

	public Integer getSslport() {
		return sslport;
	}

	public String getKeystore() {
		return keystore;
	}

	public String getPassword() {
		return password;
	}

	public String getKeyPassword() {
		return keyPassword;
	}

	public Integer getScanIntervalSeconds() {
		return scanIntervalSeconds;
	}

	public Boolean getEnablescanner() {
		return enablescanner;
	}

	public Boolean getParentLoaderPriority() {
		return parentLoaderPriority;
	}

	public Boolean getEnablessl() {
		return enablessl;
	}

	public Boolean getNeedClientAuth() {
		return needClientAuth;
	}

	public Boolean getEnableJNDI() {
		return enableJNDI;
	}

	public String getConfigurationClasses() {
		return configurationClasses;
	}

	public List<String> getConfigurationClassesList() {
		ArrayList<String> configuration = new ArrayList<String>();
		return configuration;
	}

	public void validation() {
		if (getPort() == null && getSslport() == null) {
			throw new IllegalStateException(
					"you need to provide argument -Djeju.server.jetty.port and/or -Djeju.server.jetty.sslport");
		}

		if (!available(port)) {
			throw new IllegalStateException("port :" + port
					+ " already in use!");
		}

//		if (getEnablessl() && getSslport() != null) {
//			if (!available(sslport)) {
//				throw new IllegalStateException("SSL port :" + sslport
//						+ " already in use!");
//			}
//		}
	}

	public String getResourceMapping() {
		return resourceMapping;
	}

	public static String trimQuote(String str) {
		if (str != null && str.startsWith("\"") && str.endsWith("\"")) {
			return str.substring(1, str.length() - 1);
		}
		return str;
	}

	public Map<String, String> getResourceMap() {
		String[] resources = resourceMapping.split(";");

		HashMap<String, String> map = new HashMap<String, String>();

		for (String resource : resources) {
			if (resource == null || "".equals(resource.trim()))
				continue;
			String[] tokens = resource.split("=");
			map.put(tokens[0], tokens[1]);
		}
		return map;
	}

	private static boolean available(int port) {
		if (port <= 0) {
			throw new IllegalArgumentException("Invalid start port: " + port);
		}

		ServerSocket ss = null;
		DatagramSocket ds = null;
		try {
			ss = new ServerSocket(port);
			ss.setReuseAddress(true);
			ds = new DatagramSocket(port);
			ds.setReuseAddress(true);
			return true;
		} catch (IOException e) {
		} finally {
			if (ds != null) {
				ds.close();
			}

			if (ss != null) {
				try {
					ss.close();
				} catch (IOException e) {
					/* should not be thrown */
				}
			}
		}

		return false;
	}

	private static String resovleWebappClasspath(String classpath) {

		if (classpath != null && classpath.startsWith("file://")) {
			try {
				String filePath = classpath.substring(7);

				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(filePath), "UTF-8"));
				StringBuffer sb = new StringBuffer();
				String str = br.readLine();
				while (str != null) {
					sb.append(str);
					str = br.readLine();
				}
				return sb.toString();

			} catch (IOException e) {
				System.err.println("read classpath failed!");
				throw new RuntimeException(" read classpath failed ", e);
			}
		}

		return classpath;
	}

	private static Boolean getBooleanProp(String propertiesKey, boolean def) {
		printSystemProperty(propertiesKey);
		String val = System.getProperty(propertiesKey);

		Boolean ret = def;
		if (val != null) {
			try {
				ret = Boolean.parseBoolean(val);
			} catch (Exception e) {

			}

		}
		return ret;

	}

	public int getEclipseListenerPort() {
		return eclipseListener;
	}

	public Boolean getScanWEBINF() {
		return scanWEBINF;
	}

	public String getExcludedclasspath() {
		return excludedclasspath;
	}

	private void initWebApps(){
		String webappKeys = getProp(Constants.WEBAPPS_KEY);
		if(webappKeys==null|| webappKeys.trim().length() == 0)
			return;
		String[] webappKeyArray = webappKeys.split(",");
		for(String webappKey:webappKeyArray){
			initWebApp(webappKey);
		}
	}

	private void initWebApp(String webappKey){
		if(webappKey==null|| webappKey.trim().length() == 0)
			return;
		String[] webappItemKey = webappKey.split("___");
		if(webappItemKey.length == 3){
			WebApp app = new WebApp();
			app.contextPath = getProp(webappItemKey[0]);
			app.classpath = resovleWebappClasspath(getProp(webappItemKey[1]));
			app.webappDir = getProp(webappItemKey[2]);
			webapps.add(app);
		}
	}

	public List<WebApp> getWebapps(){
		return this.webapps;
	}



}
