package webx.studio.server;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;


import org.apache.commons.io.FileUtils;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.ILogListener;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import webx.studio.server.ui.SaveEditorPrompter;
import webx.studio.utils.PortUtil;

/**
 * @author zhihua.hanzh
 *
 */
public class ServerPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "jeju.server"; //$NON-NLS-1$
	private static ServerPlugin plugin;

	private int controlPort = -1;
	private boolean listenerEnabled = false;
	private ILogListener logListener;

	public ServerPlugin() {
		super();
		plugin = this;
	}

	public void start(BundleContext context) throws Exception {
		super.start(context);
		Trace.trace(Trace.CONFIG,
				"----->-----Jeju Server plugin startup ----->-----");

		enableListenter();

		File file = new File(LogConstants.LOG_FILE);
		if(!file.exists())
			FileUtils.touch(file);
		logListener = new JejuLogWriter(new JejuLog(new File(LogConstants.LOG_FILE)));
		Platform.addLogListener(logListener);
	}

	public void stop(BundleContext context) throws Exception {
		Trace.trace(Trace.CONFIG,
				"-----<-----Jeju Server plugin shutdown -----<-----");
		plugin = null;
		Platform.removeLogListener(logListener);
		logListener = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static ServerPlugin getDefault() {
		return plugin;
	}

	public static ServerPlugin getInstance() {
		return plugin;
	}

	public static void logThrowable(Throwable exception) {
		getDefault().getLog().log(
				createErrorStatus("Internal Error", exception));
	}

	public static IStatus createErrorStatus(String message, Throwable exception) {
		if (message == null) {
			message = "";
		}
		return new Status(IStatus.ERROR, PLUGIN_ID, 0, message, exception);
	}

	static public void logError(Exception e) {
		ILog log = plugin.getLog();
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		String msg = stringWriter.getBuffer().toString();
		Status status = new Status(IStatus.ERROR, getDefault().getBundle()
				.getSymbolicName(), IStatus.ERROR, msg, null);
		log.log(status);
	}

	static public void logError(String msg) {
		ILog log = plugin.getLog();
		Status status = new Status(IStatus.ERROR, getDefault().getBundle()
				.getSymbolicName(), IStatus.ERROR, msg + "\n", null);
		log.log(status);
	}

	public void enableListenter() {
		if (!listenerEnabled) {
			listenerEnabled = true;
			controlPort = PortUtil.findAAvailablePort(50000, 60000);

			if (controlPort != -1) {
				Thread runnable = new Thread() {
					public void run() {

						try {
							ServerSocket server = new ServerSocket(controlPort);
							while (true) {
								try {
									Socket sock = server.accept();
									sock.getOutputStream().write(
											new byte[] { 1, 2 });
									sock.getOutputStream().close();
									Thread.sleep(5000L);
								} catch (Exception er) {
									Trace.trace(Trace.WARNING, er.getMessage(),
											er);
								}
							}

						} catch (IOException e) {
							Trace.trace(Trace.WARNING, e.getMessage(), e);
						}

					}
				};
				runnable.start();
			}
		}
	}

	public int getListenerPort() {
		return controlPort;
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static Shell getActiveWorkbenchShell() {
		return getActiveWorkbenchWindow().getShell();
	}

	private static SaveEditorPrompter saveEditorPrompter;

	public static synchronized SaveEditorPrompter getSaveEditorHelper() {
		if (saveEditorPrompter == null) {
			saveEditorPrompter = new SaveEditorPrompter();
		}
		return saveEditorPrompter;
	}

}
