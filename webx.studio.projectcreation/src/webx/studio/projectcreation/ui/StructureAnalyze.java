package webx.studio.projectcreation.ui;

import java.io.File;


import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Model;

import webx.studio.projectcreation.ui.project.MavenHelper;

public class StructureAnalyze {

	public File getWarFile() {
		return warFile;
	}

	public File getTotalPomFile() {
		return totalPomFile;
	}

	public Model getModel() {
		return model;
	}

	public String getWebxVersion() {
		return webxVersion;
	}

	public boolean isStandard() {
		return isStandard;
	}

	private final File warFile;
	private File totalPomFile;
	private File topFile;
	private Model model;
	private String webxVersion;
	private String pomVersion;
	private boolean isStandard;

	public StructureAnalyze(String warPath) {
		this.warFile = new File(warPath);
	}

	public StructureAnalyze(File warFile) {
		this.warFile = warFile;
	}

	public boolean analyzeFileStructure() throws Exception {
		if (warFile == null || !warFile.exists() || warFile.isFile()) {
			isStandard = false;
		} else if (!checkWarFileName()) {
			isStandard = false;
		} else {
			try {
				checkTotalPomFile();
			} catch (Exception e) {
				isStandard = false;
				throw e;
			}
		}
		return isStandard;
	}

	private boolean checkWarFileName() {
		File pomFile = new File(warFile, "pom.xml");
		if (!pomFile.exists() || pomFile.isDirectory())
			return false;
		if ("war".equalsIgnoreCase(warFile.getName()) || "web".equalsIgnoreCase(warFile.getName())) {
			File parentFile = warFile.getParentFile();
			if (parentFile.exists() && parentFile.getName().equals("bundle")) {
				topFile = parentFile.getParentFile();
				if (topFile.exists()) {
					totalPomFile = new File(topFile, "pom.xml");
					if (totalPomFile.exists() && totalPomFile.isFile())
						return true;
				}
			}

		} else if ("bundle".equalsIgnoreCase(warFile.getName())) {
			topFile = warFile.getParentFile();
			if (topFile.exists()) {
				totalPomFile = new File(topFile, "pom.xml");
				if (totalPomFile.exists() && totalPomFile.isFile())
					return true;
			}
		} else {
			topFile = warFile.getParentFile();
			if (topFile.exists()) {
				totalPomFile = new File(topFile, "pom.xml");
				if (totalPomFile.exists() && totalPomFile.isFile())
					return true;
			}
		}
		return false;

	}

	public File getTopFile() {
		return topFile;
	}

	private boolean checkTotalPomFile() throws Exception {
		if (totalPomFile == null || !totalPomFile.exists()
				|| totalPomFile.isDirectory())
			return false;
		model = MavenHelper.getModel(totalPomFile);
		pomVersion = model.getVersion();
		webxVersion = model.getProperties().getProperty("webx3-version");
		if (StringUtils.isBlank(webxVersion)) {
			webxVersion = model.getProperties().getProperty("webx3_version");
		}
		if (StringUtils.isBlank(webxVersion)) {
			webxVersion = model.getProperties().getProperty("webx-version");
		}
		if (StringUtils.isBlank(webxVersion)) {
			webxVersion = model.getProperties().getProperty("webx_version");
		}
//		if (StringUtils.isNotBlank(webxVersion)) {
//			isStandard = true;
//			return true;
//		} else {
//			isStandard = false;
//			return false;
//		}
		isStandard = true;
		return true;
	}

	public String getDeployEclipseProjectName(String name) {
		if (isStandard) {
			return name + ".deploy" + "-" + pomVersion;
		}
		return "";
	}

	public String[] guessDeployEclipseProjectName(String name) {
		return new String[] { name + ".deploy" + "-" + pomVersion,
				name + ".deploy", name + ".deploy.web", StringUtils.split(name, ".")[0] + ".deploy"+ "-" + pomVersion,StringUtils.split(name, ".")[0] + ".deploy",name + "-deploy" };
	}

}
