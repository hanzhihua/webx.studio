package webx.studio.server.core;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;


import org.eclipse.ui.browser.IWebBrowser;
import org.eclipse.ui.browser.IWorkbenchBrowserSupport;

import webx.studio.server.ServerPlugin;
import webx.studio.server.Trace;

/**
 * @author zhihua.hanzh
 * 
 */
public class PingThread {

	private static final int __PING_DELAY = 2000;
	private static final int __PING_INTERVAL = 250;

	private int maxPings;
	private boolean stop = false;
	private String url;
	private Server server;

	public PingThread(Server server, String url, int maxPings) {
		this.server = server;
		this.url = url;
		this.maxPings = maxPings;

		Thread t = new Thread("Server Ping Thread") {
			public void run() {
				ping();
			}
		};
		t.setDaemon(true);
		t.start();
	}

	protected void ping() {
		int count = 0;
		try {
			Thread.sleep(__PING_DELAY);
		} catch (Exception e) {
			Trace.traceError(e);
		}
		while (!stop) {
			try {
				if (count == maxPings) {
				}
				count++;

				URL pingUrl = new URL(url);
				URLConnection conn = pingUrl.openConnection();
				((HttpURLConnection) conn).getResponseCode();

				if (!stop) {
					Thread.sleep(200);
					server.setServerState(Server.STATE_STARTED);
					if (server.isOpenBrowser()) {
						IWorkbenchBrowserSupport browserSupport = ServerPlugin
								.getInstance().getWorkbench()
								.getBrowserSupport();
						IWebBrowser browser = browserSupport
								.createBrowser(
										IWorkbenchBrowserSupport.LOCATION_BAR
												| IWorkbenchBrowserSupport.NAVIGATION_BAR,
										null, null, null);
						browser.openURL(pingUrl);
					}
				}
				stop = true;
			} catch (Exception e) {
				if (!stop) {
					try {
						Thread.sleep(__PING_INTERVAL);
					} catch (InterruptedException e2) {
					}
				}
			}
		}
	}

	public void stop() {
		stop = true;
	}

}
