package webx.studio.utils;

import java.net.MalformedURLException;
import java.net.URL;

import org.eclipse.core.runtime.ILog;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.browser.IWebBrowser;

/**
 *
 *
 *  @author zhihua.han@alibaba-inc.com
 */
public class BrowserUtil {

	public static void checkedCreateInternalBrowser(String url,
			String browserId, String pluginId, ILog log) {
		try {
			openUrl(url, PlatformUI.getWorkbench().getBrowserSupport()
					.createBrowser(browserId), pluginId, log);
		} catch (PartInitException e) {
			Object[] messageArguments = { url };
			IStatus errorStatus = StatusUtils.getErrorStatus(pluginId,
					"Could not open browser for url \"{0}\".", e,
					messageArguments);
			log.log(errorStatus);
		}
	}

	public static void checkedCreateExternalBrowser(String url,
			String pluginId, ILog log) {
		try {
			openUrl(url, PlatformUI.getWorkbench().getBrowserSupport()
					.getExternalBrowser(), pluginId, log);
		} catch (PartInitException e) {
			Object[] messageArguments = { url };
			IStatus errorStatus = StatusUtils.getErrorStatus(pluginId,
					"Could not open browser for url \"{0}\".", e,
					messageArguments);
			log.log(errorStatus);
		}
	}

	public static void openUrl(String url, IWebBrowser browser,
			String pluginId, ILog log) {
		Object[] messageArguments = { url };
		try {
			browser.openURL(new URL(url));
		} catch (PartInitException e) {
			IStatus errorStatus = StatusUtils.getErrorStatus(pluginId,
					"Could not open browser for url \"{0}\".", e,
					messageArguments);
			log.log(errorStatus);
		} catch (MalformedURLException e) {
			IStatus errorStatus = StatusUtils.getErrorStatus(pluginId,
					"Could not display malformed url \"{0}\".", e,
					messageArguments);
			log.log(errorStatus);
		}
	}
}
