/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-4-27
 * $Id: GeneralUtils.java 122347 2011-11-04 06:06:07Z zhihua.hanzh $
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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.regex.Pattern;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;

import webx.studio.projectcreation.ui.project.ModuleProjectInformation;
import webx.studio.projectcreation.ui.structure.AbstractStructureNodeModel;

/**
 * TODO Comment of Utils
 * 
 * @author zhihua.hanzh
 */
public abstract class GeneralUtils {

	public static final String JAVA_PACKAGE_PATTERN_STRING = "[a-z]+(\\.[a-z]+)*";

	public static final Pattern PACKAGE_PATTERN = Pattern
			.compile(JAVA_PACKAGE_PATTERN_STRING);

	private final static String TOP_ARTIFACT_ID_SUFFER = ".all";

	public static final Pattern VERSION_PATTERN = Pattern
			.compile("(([0-9]+\\.)+[0-9]+)");

	public final static String SVN_DIRECTORY = ".svn";

	public static Control createEmptySpace(Composite parent) {
		return createEmptySpace(parent, 1);
	}

	private static Control createEmptySpace(Composite parent, int span) {
		Label label = new Label(parent, SWT.LEFT);
		GridData gd = new GridData();
		gd.horizontalAlignment = GridData.BEGINNING;
		gd.grabExcessHorizontalSpace = false;
		gd.horizontalSpan = span;
		gd.horizontalIndent = 0;
		gd.widthHint = 0;
		gd.heightHint = 0;
		label.setLayoutData(gd);
		return label;
	}

	protected static IWorkbenchWindow getActiveWorkbenchWindow() {
		return PlatformUI.getWorkbench().getActiveWorkbenchWindow();
	}

	public static IProject getEclipseProject(String name) {
		return getEclipseWorkspaceRoot().getProject(name);
	}

	public static IWorkspaceRoot getEclipseWorkspaceRoot() {
		return ResourcesPlugin.getWorkspace().getRoot();
	}

	public static String getResourcePath(File templateFile) throws IOException {
		return templateFile.getCanonicalPath().substring(
				getVelocityResourceRoot().length());
	}

	public static String getVelocityResourceRoot() throws IOException {
		URL url = FileLocator.resolve(ProjectCreationPlugin.getInstallURL());
		File root = new File(url.getFile());
		return root.getCanonicalPath();
	}

	public static String nameAsModulePath(String modelName) {
		return "./" + modelName.replace('.', '/');
	}

	public static void processTemplate(File srcDir, File destDir,
			final Properties context, final List<String> templateNameList,
			NameRule nameRule) throws Exception {
		processTemplate(srcDir, destDir, context, templateNameList, nameRule,
				new ArrayList<ModuleProjectInformation>(), null);
	}

	public static void processTemplate(File srcDir, File destDir,
			final Properties context, final List<String> templateNameList,
			NameRule nameRule,
			List<ModuleProjectInformation> webModuleProjectList,
			ModuleProjectInformation deployModuleProjectInformation)
			throws Exception {
		if (srcDir == null || destDir == null || !srcDir.exists())
			return;
		if (destDir.exists()) {
			if (destDir.isDirectory() == false) {
				throw new IOException("Destination '" + destDir
						+ "' exists but is not a directory");
			}
		} else {
			if (destDir.mkdirs() == false) {
				throw new IOException("Destination '" + destDir
						+ "' directory cannot be created");
			}
		}
		File[] files = srcDir.listFiles();
		for (File file : files) {
			if (SVN_DIRECTORY.equals(file.getName())) {
				continue;
			}
			File outFile = new File(destDir, file.getName());
			if (file.isDirectory()) {
				processTemplate(file, outFile, context, templateNameList,
						nameRule, webModuleProjectList,
						deployModuleProjectInformation);
			} else {
				if (templateNameList.contains(file.getName())) {
					if (ProjectCreationConstants.SPECIAL_TEMPLATE_FILE
							.equalsIgnoreCase(file.getName())) {
						for (ModuleProjectInformation info : webModuleProjectList) {
							outFile = new File(destDir,
									getWebxDescriptionFileName(info
											.getInputName()));
							formatContext(context, nameRule, info
									.getInputName());
							processTemplateFile(outFile, context, file);
						}
					} else if (ProjectCreationConstants.HELLOWORLD_TEMPLATE_FILE
							.equalsIgnoreCase(file.getName())) {
						for (ModuleProjectInformation info : webModuleProjectList) {
							String pkg = getScreenPackage(nameRule, info
									.getInputName());
							File outDir = getPackageDir(info.getProjectDir(),
									pkg);
							outFile = new File(
									outDir,
									ProjectCreationConstants.HELLOWORLD_TEMPLATE_FILE);
							context.put(ProjectCreationConstants.PACKAGE_KEY,
									pkg);
							context.put(ProjectCreationConstants.MESSAGE_KEY,
									"HelloWorld comes from "
											+ info.getArtfiactId());
							processTemplateFile(outFile, context, file);
						}
					} else if (ProjectCreationConstants.HELLOWORLD_VM_FILE
							.equalsIgnoreCase(file.getName())) {
						for (ModuleProjectInformation info : webModuleProjectList) {
							outFile = new File(
									context
											.getProperty(ProjectCreationConstants.TEMPLATE_ROOT_KEY)
											+ File.separator
											+ info
													.getInputName()
													.substring(
															info
																	.getInputName()
																	.lastIndexOf(
																			".") + 1)
											+ File.separator + "screen");
							outFile.mkdirs();
							FileUtils.copyFileToDirectory(file, outFile);
							// FileUtils.c
						}
					} else {
						processTemplateFile(outFile, context, file);
					}
				} else if (StringUtils.contains(file.getAbsolutePath(),
						"deploy")
						&& (StringUtils.endsWith(file.getName(), ".vm") || StringUtils
								.equals(file.getName(), "auto-config.xml"))) {
					String content = org.apache.commons.io.FileUtils
							.readFileToString(file);
					content = StringUtils.replace(content, "@appName@",
							nameRule.getProjectName());
					FileUtils.writeStringToFile(outFile, content);
				} else {
					FileUtils.copyFile(file, outFile);
				}
			}
		}
	}

	private static void processTemplateFile(File outFile, Properties context,
			File templateFile) throws Exception {

		Velocity.setProperty(Velocity.FILE_RESOURCE_LOADER_PATH,
				getVelocityResourceRoot());

		Velocity.setProperty(Velocity.RUNTIME_LOG_LOGSYSTEM_CLASS,
				org.apache.velocity.runtime.log.SystemLogChute.class.getName());

		Velocity.init();
		StringWriter stringWriter = new StringWriter();

		Velocity.getTemplate(getResourcePath(templateFile), "utf-8").merge(
				new VelocityContext(context), stringWriter);

		Writer writer = new OutputStreamWriter(new FileOutputStream(outFile),
				"utf-8");

		writer.write(org.codehaus.plexus.util.StringUtils
				.unifyLineSeparators(stringWriter.toString()));

		writer.flush();

	}

	private static String getWebxDescriptionFileName(String name) {
		StringBuilder sb = new StringBuilder();
		if (StringUtils.isBlank(name))
			return "";
		if (!name.startsWith("webx-"))
			sb.append("webx-");
		sb.append(name.substring(name.lastIndexOf(".") + 1));
		sb.append(".xml");
		return sb.toString();
	}

	public static String getRootModulePackage(NameRule nameRule) {
		return nameRule.getPackagePrefix() + "web.common.module.*";
	}

	private static void formatContext(Properties context, NameRule nameRule,
			String name) {
		String packagePrefix = nameRule.getPackagePrefix();
		if (!name.startsWith("web.")) {
			name = "web." + name;
		}
		context.put(ProjectCreationConstants.COMPONENT_KEY, name.substring(name
				.lastIndexOf(".") + 1));
		context.put(ProjectCreationConstants.COMPONENT_MODULE_PACKAGE_KEY,
				packagePrefix + name + ".module.*");
	}

	private static File getPackageDir(File project, String packageName) {
		File pkgFile = new File(project, AbstractStructureNodeModel.JAVA
				.getPath()
				+ File.separator + packageName.replace(".", File.separator));
		pkgFile.mkdirs();
		return pkgFile;
	}

	private static String getScreenPackage(NameRule nameRule, String name) {
		String packagePrefix = nameRule.getPackagePrefix();
		if (!name.startsWith("web.")) {
			name = "web." + name;
		}
		return packagePrefix + name + ".module.screen";
	}

	public static String getTemplatesRoot(
			ModuleProjectInformation deployModuleProjectInformation) {
		File file = new File(deployModuleProjectInformation.getProjectDir(),
				"templates");
		file.mkdir();
		try {
			return file.getCanonicalPath();
		} catch (Exception e) {
			return file.getPath();
		}

	}
}
