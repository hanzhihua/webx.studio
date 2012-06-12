package webx.studio.server.jetty;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.management.ManagementFactory;
import java.net.DatagramSocket;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import javax.management.MBeanServer;

import org.eclipse.jetty.jmx.MBeanContainer;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandlerCollection;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerCollection;
import org.eclipse.jetty.server.nio.SelectChannelConnector;
import org.eclipse.jetty.server.ssl.SslSocketConnector;
import org.eclipse.jetty.webapp.WebAppContext;

/**
 * @author zhihua.hanzh
 *
 */
public class Bootstrap {

	private static Server server;

	public static void main(String[] args) throws Exception {

//		AutoconfigBootstrap.main(args);

		System.err.println("Running Jetty 7.2.2.v20101205");
		final Configs configs = new Configs();
		configs.validation();

		System.setProperty(".DEBUG", "false");
		System.setProperty("org.eclipse.jetty.util.log.class", "org.eclipse.jetty.util.log.StdErrLog");
		System.setProperty("org.eclipse.jetty.util.log.DEBUG", "false");
		server = new Server();
		initConnnector(server, configs);
		initWebappContext(server,configs);
		initEclipseListener(configs);
		initCommandListener(configs);
		setupJmx();
		try {
			server.start();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
		return;
	}

	private static void setupJmx(){
		MBeanServer mBeanServer = ManagementFactory.getPlatformMBeanServer();
	    MBeanContainer mBeanContainer = new MBeanContainer(mBeanServer);
	    server.getContainer().addEventListener(mBeanContainer);
	    try {
			mBeanContainer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void initConnnector(Server server, Configs configObj) {

		SelectChannelConnector connector = new SelectChannelConnector();
		connector.setPort(configObj.getPort());

		if (configObj.getEnablessl() && configObj.getSslport() != null) {
			if (!available(configObj.getSslport())) {
				throw new IllegalStateException("SSL port :"
						+ configObj.getSslport() + " already in use!");
			}
			connector.setConfidentialPort(configObj.getSslport());
		}

		server.addConnector(connector);

		if (configObj.getEnablessl() && configObj.getSslport() != null)
			initSSL(server, configObj.getSslport(), configObj.getKeystore(),
					configObj.getPassword(), configObj.getKeyPassword(),
					configObj.getNeedClientAuth());

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

	private static void initSSL(Server server, int sslport, String keystore,
			String password, String keyPassword, boolean needClientAuth) {

		if (keystore == null) {
			throw new IllegalStateException(
					"you need to provide argument -Djeju.jetty.keystore with -Djeju.jetty.sslport");
		}
		if (password == null) {
			throw new IllegalStateException(
					"you need to provide argument -Djeju.jetty.password with -Djeju.jetty.sslport");
		}
		if (keyPassword == null) {
			throw new IllegalStateException(
					"you need to provide argument -Djeju.jetty.keypassword with -Djeju.jetty.sslport");
		}

		SslSocketConnector sslConnector = new SslSocketConnector();
		sslConnector.setKeystore(keystore);
		sslConnector.setPassword(password);
		sslConnector.setKeyPassword(keyPassword);

		if (needClientAuth) {
			System.err.println("Enable NeedClientAuth.");
			sslConnector.setNeedClientAuth(needClientAuth);
		}
		sslConnector.setMaxIdleTime(30000);
		sslConnector.setPort(sslport);

		server.addConnector(sslConnector);
	}

	private static void initWebappContext(Server server, Configs configs)
			throws IOException, URISyntaxException {

		if (configs.getParentLoaderPriority()) {
			System.err.println("ParentLoaderPriority enabled");
		}
		System.err.println("Use ["+ configs.getClasspathProvider() +"] to generate webapp's classpath!");
		HandlerCollection handlers = new HandlerCollection();
        ContextHandlerCollection contexts = new ContextHandlerCollection();
		for(WebApp webapp:configs.getWebapps()){
			WebAppContext web = new WebAppContext();
			web.setContextPath(webapp.contextPath);
			web.setWar(webapp.webappDir);
			if (configs.getParentLoaderPriority()) {
				web.setParentLoaderPriority(true);
			}

			web.setInitParameter("org.eclipse.jetty.servlet.Default.useFileMappedBuffer",
					"false");

			System.err.println("Context path:" + webapp.contextPath);
			System.err.println("Webapp root:"+webapp.webappDir);

			ProjectClassLoader loader = new ProjectClassLoader(web,
					webapp.classpath,
					configs);
			web.setClassLoader(loader);
			contexts.addHandler(web);

		}

        List<Handler> handlerls = new ArrayList<Handler>();
        handlerls.add(contexts);
        handlerls.add(new DefaultHandler());

        handlers.setHandlers(handlerls.toArray(new Handler[] {}));
        server.setHandler(handlers);
	}

	private static void initEclipseListener(final Configs configs){
		if(configs.getEclipseListenerPort() != -1 ){
			Thread eclipseListener = new Thread(){
				public void run() {
					try {
						while(true){
							Thread.sleep(5000L);
							Socket sock = new Socket("127.0.0.1", configs.getEclipseListenerPort());
							byte[] response = new byte[4];
							sock.getInputStream().read(response);
							if(response[0] ==1 && response[1] ==2){
								//it's ok!
							}else{
								shutdownServer();
							}

						}

					} catch (Exception e) {
						System.err.println("lost connection with Eclipse , shutting down.");
						e.printStackTrace();
						shutdownServer();
					}
				};
			};
			eclipseListener.start();
		}

	}

	private static void shutdownServer(){
		try {
			server.stop();
			System.exit(-1);
		} catch (Exception e1) {
			e1.printStackTrace();
		}

	}

	private static void initCommandListener(final Configs configs){
		Thread commandListener = new Thread(){
			public void run() {
				try {
					BufferedReader input = new BufferedReader(new InputStreamReader(System.in));
					while(true){
						String inputStr = input.readLine();
						if(inputStr != null){
							inputStr = inputStr.trim();

							if("exit".equalsIgnoreCase(inputStr) ||
									"quit".equalsIgnoreCase(inputStr) ||
									"q".equalsIgnoreCase(inputStr)
							){
								System.err.println("shutting down");
								shutdownServer();
							}else if("r".equalsIgnoreCase(inputStr)
									|| "restart".equalsIgnoreCase(inputStr)
							){}
						}
					}

				} catch (Exception e) {
					e.printStackTrace();
					shutdownServer();
				}
			};
		};
		commandListener.start();

	}

}
