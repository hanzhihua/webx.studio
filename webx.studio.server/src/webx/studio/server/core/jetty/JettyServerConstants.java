package webx.studio.server.core.jetty;

import webx.studio.server.jetty.Bootstrap;


public class JettyServerConstants {

	public final static String PREFIX = "jeju.jetty";

	public static final String BOOTSTRAP_CLASS_NAME = Bootstrap.class.getName();

	public final static String ATTR_WEBAPPDIR = PREFIX + ".WEBAPPDIR_ATTR";

	public static final String ATTR_CONTEXT = PREFIX + ".CONTEXT_ATTR";

	public static final String ATTR_WEBMODULE_PREFIX = PREFIX + ".WEBMODULE.";
	public static final String ATTR_WEBMODULE_CONTEXT_SUFFIX = ".CONTEXT";
	public static final String ATTR_WBBMODULE_WEBAPPDIR_SUFFIX = ".WEBAPPDIR";
	public static final String ATTR_WEBMODULE_CLASSPATH_SUFFIX = ".CLASSPATH";

	public static final String ATTR_WEBMODULE_KEY = PREFIX+".WEBMODULE.KEY";

	public static final String ATTR_EXCLUDE_CLASSPATH = PREFIX
			+ ".EXCLUDE_CLASSPATH_ATTR";

	public static final String ATTR_PORT = PREFIX + ".PORT_ATTR";

	public static final String ATTR_ENABLE_SSL = PREFIX + ".ENABLE_SSL_ATTR";

	public static final String ATTR_ENABLE_NEED_CLIENT_AUTH = PREFIX
			+ ".ENABLE_NEED_CLIENT_AUTH_ATTR";

	/** configuration attribute for the SSL port to run Jetty on. */
	public static final String ATTR_SSL_PORT = PREFIX + ".SSL_PORT_ATTR";

	/** configuration attribute for the location of the keystore. */
	public static final String ATTR_KEYSTORE = PREFIX + ".KEYSTORE_ATTR";

	/** configuration attribute for the SSL port to run Jetty on. */
	public static final String ATTR_KEY_PWD = PREFIX + ".KEY_PWD_ATTR";

	/** configuration attribute for the SSL port to run Jetty on. */
	public static final String ATTR_PWD = PREFIX + ".PWD_ATTR";
	/** configuration attribute for the scan interval seconds. */
	public static final String ATTR_SCANINTERVALSECONDS = PREFIX
			+ ".SCANINTERVALSECONDS_ATTR";
	/** configuration attribute for the scan interval seconds. */
	public static final String ATTR_ENABLE_SCANNER = PREFIX
			+ ".ENABLE_SCANNER_ATTR";

	public static final String ATTR_SCANNER_SCAN_WEBINF = PREFIX
			+ ".ENABLE_SCANNER_SCAN_WEBINF_ATTR";

	public static final String ATTR_ENABLE_MAVEN_TEST_CLASSES = PREFIX
			+ ".ENABLE_MAVEN_TEST_CLASSES_ATTR";

	public static final String ATTR_SELECTED_JETTY_VERSION = PREFIX
			+ ".SELECTED_JETTY_VERSION_ATTR";

	public static final String ATTR_ENABLE_PARENT_LOADER_PRIORITY = PREFIX
			+ ".ENABLE_PARENT_LOADER_PRIORITY_ATTR";

	public static final String CONTAINER_JEJU_JETTY = "JEJUJetty";
	public static final String CONTAINER_RJR_JETTY_JNDI = "RJRJetty6JNDI";

	public static final String ATTR_ENABLE_JNDI = PREFIX + ".ENABLE_JNDI_ATTR";

	public static final String IPROVIDER_ID = "runjettyrun.jetty.providers";

	public static final String ATTR_SHOW_ADVANCE = PREFIX
			+ ".SHOW_ADVANCE_ATTR";
}
