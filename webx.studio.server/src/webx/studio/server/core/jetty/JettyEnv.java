package webx.studio.server.core.jetty;

import java.util.Arrays;
import java.util.List;

public class JettyEnv {

	public final static String PRODUCT_JAVA_MEM_OPTS = "-Xms128m -Xmx512m -XX:PermSize=128m";

	public final static String PRODUCT_JAVA_OPTS_EXT = " -Djava.awt.headless=true -Djava.net.preferIPv4Stack=true " +
			"-Dapplication.codeset=GBK -Dmonitor.enable=true -Dmonitor.exclog.enable=true" +
			" -Dmonitor.biz.interval=120 -Dmonitor.debug=false" ;

	public final static String PRODCUT_DATABASE_OPTS = " -Ddatabase.codeset=ISO-8859-1 -Ddatabase.logging=false";

//	public final static String PRODCUT_URI_ENCODE = " -Dorg.eclipse.jetty.util.URI.charset=GBK";

	public final static String JAVA_OPTS = PRODUCT_JAVA_MEM_OPTS + PRODUCT_JAVA_OPTS_EXT ;

//	public final static String JAVA_OPTS = PRODUCT_JAVA_MEM_OPTS;

	public final static List<String> JAVA_OPTS_LIST  = Arrays.asList(JAVA_OPTS.split(" "));
}
