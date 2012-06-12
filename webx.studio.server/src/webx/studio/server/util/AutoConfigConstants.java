package webx.studio.server.util;

public class AutoConfigConstants {


	public final static String MAVEN_PROJECT_BASEDIR = "${project.basedir}";

	public final static String[] AUTOCONF_DEFAULT_DESCRIPTOR_PATTERNS = {"conf/**/auto-config.xml", "META-INF/**/auto-config.xml"};

	public final static String[] AUTOCONF_PARENT_NAMES = {"autoconf","auto-conf","autoconfig","auto-config" };

	public final static String[] IGNORE_DIRS_WHEN_SCAN = {".svn","target","classes","lib","deploy"};

}
