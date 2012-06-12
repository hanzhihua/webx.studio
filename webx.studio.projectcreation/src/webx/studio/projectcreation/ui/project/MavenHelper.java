/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-15
 * $Id: MavenHelper.java 111142 2011-09-12 14:13:21Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.project;

import hidden.org.codehaus.plexus.util.xml.Xpp3DomBuilder;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Plugin;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.classworlds.ClassWorld;
import org.codehaus.plexus.util.cli.CommandLineUtils;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.osgi.util.NLS;

import webx.studio.maven.MavenApi;
import webx.studio.maven.MavenExecuteException;
import webx.studio.projectcreation.ui.GeneralUtils;
import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.structure.AbstractStructureNodeModel;

/**
 * TODO Comment of MavenHelper
 *
 * @author zhihua.hanzh
 */
public abstract class MavenHelper {

	private final static MavenApi api = new MavenApi();

	public static Dependency genWebx3Core(String version) {

		Dependency dependency = new Dependency();
		dependency.setArtifactId("webx3.core");
		dependency.setGroupId("com.alibaba.platform.shared");
		if (version != null)
			dependency.setVersion(version);
		return dependency;
	}

	// public static Dependency genWebx3Reload(String version) {
	//
	// Dependency dependency = new Dependency();
	// dependency.setArtifactId("webx3.extension.reload");
	// dependency.setGroupId("com.alibaba.platform.shared");
	// if (version != null)
	// dependency.setVersion(version);
	// return dependency;
	// }

	public static Dependency genWebx3Compat(String version) {
		Dependency dependency = new Dependency();
		dependency.setArtifactId("webx3.compat");
		dependency.setGroupId("com.alibaba.platform.shared");
		if (version != null)
			dependency.setVersion(version);
		return dependency;
	}

	public static Dependency genWebx3Test(String version) {
		Dependency dependency = new Dependency();
		dependency.setArtifactId("webx3.test");
		dependency.setGroupId("com.alibaba.platform.shared");
		if (version != null)
			dependency.setVersion(version);
		dependency.setScope("test");
		return dependency;
	}

	public static void createParentPom(File pomFile, Model model)
			throws Exception {

		File parent = pomFile.getParentFile();
		if (parent != null && parent.exists() == false) {
			if (parent.mkdirs() == false) {
				throw new IOException("File '" + pomFile
						+ "' could not be created");
			}
		}
		FileWriter sw = new FileWriter(pomFile);
		new MavenXpp3Writer().write(sw, model);
	}

	public static void createPom(IFile pomFile, Model model,
			List<? extends AbstractStructureNodeModel> dependencyNodeModelList)
			throws Exception {
		for (AbstractStructureNodeModel nodeModel : dependencyNodeModelList) {
			model.addDependency(nodeModel.selfAsDependency());
		}
		String pomFileName = pomFile.getLocation().toString();
		if (pomFile.exists()) {
			String msg = NLS.bind("POM {0} already exists", pomFileName);
			throw new IOException(msg);
		}

		StringWriter sw = new StringWriter();
		new MavenXpp3Writer().write(sw, model);
		pomFile.create(new ByteArrayInputStream(sw.toString().getBytes()),
				true, new NullProgressMonitor());
	}

	public static String getLocalRepository() {

		return api.getLocalRepositoryPath();
	}

	public static void generateEclipse(final String baseDir,
			String settingFilePath, IProgressMonitor monitor) throws Exception {

		monitor.beginTask("Configure all generated projects", 1);

		String userDir = System.getProperty("user.dir");
		System.setProperty("user.dir", baseDir);
		System.setProperty("maven.test.skip", "true");
		try {
			List<String> goals = new ArrayList<String>();
			goals.add("install");
			goals.add("eclipse:eclipse");
			if (StringUtils.isEmpty(settingFilePath)) {
				api.setSettingFile(null);
			} else {
				api.setSettingFile(new File(settingFilePath));
			}
			api.execute(goals);
		} catch (Exception e) {
			throw new MavenExecuteException(e);
		} finally {
			System.setProperty("user.dir", userDir);
			try {
				GeneralUtils.getEclipseWorkspaceRoot().refreshLocal(
						IResource.DEPTH_INFINITE, monitor);
			} finally {
				monitor.done();
			}

		}
	}

	public static Build genBuildWithJetty() throws XmlPullParserException,
			IOException {
		Build build = new Build();
		Plugin jettyPlugin = new Plugin();
		jettyPlugin.setGroupId("org.mortbay.jetty");
		jettyPlugin.setArtifactId("maven-jetty-plugin");
		jettyPlugin.setVersion("${jetty-version}");
		String content = "<configuration><contextPath>/</contextPath>    "
				+ "                <connectors>                "
				+ "        <connector implementation=\"org.mortbay.jetty.nio.SelectChannelConnector\">                  "
				+ "          <port>8080</port>                            <maxIdleTime>60000</maxIdleTime>             "
				+ "           </connector>                    </connectors>                   "
				+ " <requestLog implementation=\"org.mortbay.jetty.NCSARequestLog\">               "
				+ "         <filename>target/access.log</filename>               "
				+ "         <retainDays>90</retainDays>                        <append>false</append>        "
				+ "                <extended>false</extended>                        <logTimeZone>GMT+8:00</logTimeZone>       "
				+ "             </requestLog>                    <systemProperties>                        <systemProperty>             "
				+ "               <name>productionMode</name>                            <value>false</value>                 "
				+ "       </systemProperty>                    </systemProperties>                </configuration>";

		jettyPlugin.setConfiguration(Xpp3DomBuilder.build(new StringReader(
				content)));
		build.addPlugin(jettyPlugin);
		return build;

	}

	public static Build genBuildWithEclipsePluginForParentPom()
			throws XmlPullParserException, IOException {
		Build build = new Build();
		Plugin eclipsePlugin = new Plugin();
		eclipsePlugin.setGroupId("com.alibaba.org.apache.maven.plugins");
		eclipsePlugin.setArtifactId("maven-eclipse-plugin");
		eclipsePlugin.setVersion("2.5.1-alibaba-0");
		String content = "<configuration>	"
				+ "				<addVersionToProjectName>true</addVersionToProjectName>				"
				+ "	<downloadSources>true</downloadSources>				"
				+ "	<projectTextFileEncoding>UTF-8</projectTextFileEncoding>			"
				+ "	</configuration>";

		eclipsePlugin.setConfiguration(Xpp3DomBuilder.build(new StringReader(
				content)));
		build.addPlugin(eclipsePlugin);
		return build;

	}


	public static Model getModel(File file) throws Exception {
		MavenXpp3Reader reader = new MavenXpp3Reader();
		FileInputStream stream = new FileInputStream(file);
		return reader.read(stream);
	}

}
