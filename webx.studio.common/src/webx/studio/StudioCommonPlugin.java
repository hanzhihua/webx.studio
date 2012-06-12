package webx.studio;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

public class StudioCommonPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "jeju.common";
	private static StudioCommonPlugin instance;

	public void start(BundleContext context) throws Exception {
		super.start(context);
		instance = this;
	}

	public void stop(BundleContext context) throws Exception {
		instance = null;
		super.stop(context);
	}

	public static StudioCommonPlugin getDefault() {
		return instance;
	}

	public static StudioCommonPlugin getInstance() {
		return instance;
	}

	static public void logError(Exception e) {
		ILog log = instance.getLog();
		StringWriter stringWriter = new StringWriter();
		e.printStackTrace(new PrintWriter(stringWriter));
		String msg = stringWriter.getBuffer().toString();
		Status status = new Status(IStatus.ERROR, getDefault().getBundle()
				.getSymbolicName(), IStatus.ERROR, msg, null);
		log.log(status);
	}

	static public void logError(String msg) {
		ILog log = instance.getLog();
		Status status = new Status(IStatus.ERROR, getDefault().getBundle()
				.getSymbolicName(), IStatus.ERROR, msg + "\n", null);
		log.log(status);
	}

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static Shell getActiveWorkbenchShell() {
		return getActiveWorkbenchWindow().getShell();
	}

	public static IWorkbenchPage getActiveWorkbenchPage() {
		return getActiveWorkbenchWindow().getActivePage();
	}
	
	public static File getMavenSetting() throws IOException {
		return new File(FileLocator
				.resolve(
						Platform.getBundle((PLUGIN_ID)).getEntry(
								"/maven/settings.xml")).getFile());
	}
	
	public static URL getInstallURL() {
		return Platform.getBundle((PLUGIN_ID)).getEntry("/");
	}
	
	public static File getInstallOsFile() throws IOException{
		return new File(FileLocator.resolve(getInstallURL()).getFile());
	}


}
