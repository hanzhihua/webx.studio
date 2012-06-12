/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-4-25
 * $Id: ProjectCreationPlugin.java 111095 2011-09-10 10:58:19Z zhihua.hanzh $
 *
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package webx.studio.projectcreation.ui;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

/**
 *
 * @author zhihua.hanzh
 *
 */
public class ProjectCreationPlugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "jeju.projectcreation.ui";

	private static ProjectCreationPlugin plugin;

	public ProjectCreationPlugin() {
		plugin = this;
	}

	public static ProjectCreationPlugin getDefault() {
		return plugin;
	}

	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}

	public static void log(String message, Throwable exception) {
		getDefault().getLog().log(createErrorStatus(message, exception));
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

	public static IWorkbenchWindow getActiveWorkbenchWindow() {
		return getDefault().getWorkbench().getActiveWorkbenchWindow();
	}

	public static Shell getActiveWorkbenchShell() {
		return getActiveWorkbenchWindow().getShell();
	}

	public static IWorkbenchHelpSystem getHelpSystem() {
		return getActiveWorkbenchWindow().getWorkbench().getHelpSystem();
	}

	@Override
	public void start(BundleContext context) throws Exception {
		super.start(context);
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		super.stop(context);
		plugin = null;
	}

	public static boolean isDebug(String option) {
		String value = Platform.getDebugOption(PLUGIN_ID+"/"+option);
		return StringUtils.equalsIgnoreCase(value, "true");
	}

	public static URL getInstallURL() {
		return Platform.getBundle((PLUGIN_ID)).getEntry("/");
	}

	public static URL getTemplate(String type) {
		return Platform.getBundle((PLUGIN_ID)).getEntry("/templates/" + type);
	}

	public static URL getSimpleTemplate() {
		return Platform.getBundle((PLUGIN_ID)).getEntry("/templates/simple");
	}

	public static File getRootResource() throws IOException {
		return new File(FileLocator.resolve(
				Platform.getBundle((PLUGIN_ID)).getEntry("/")).getFile());
	}

	public static File getNormalSimpleTemplate() throws IOException {
		return new File(FileLocator.resolve(
				Platform.getBundle((PLUGIN_ID)).getEntry("/templates/simple"))
				.getFile());
	}

	public static File getWarTemplate() throws IOException {
		return new File(FileLocator.resolve(
				Platform.getBundle((PLUGIN_ID)).getEntry("/templates/war"))
				.getFile());
	}

	public static File getDeployTemplate() throws IOException {
		return new File(FileLocator.resolve(
				Platform.getBundle((PLUGIN_ID)).getEntry("/templates/deploy"))
				.getFile());
	}

	
	@Override
	protected void initializeImageRegistry(ImageRegistry registry) {
		ProjectCreationUIImages.initializeImageRegistry(registry);
	}

}
