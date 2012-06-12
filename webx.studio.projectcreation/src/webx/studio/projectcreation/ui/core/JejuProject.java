package webx.studio.projectcreation.ui.core;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;

public class JejuProject {

	
	public String toString() {
		return "JejuProject [name=" + name + ", projectNames=" + projectNames
				+ ", warProjectName=" + warProjectName + ", settingFile="
				+ settingFile + ", webxVersion=" + webxVersion
				+ ", antxPropertiesFile=" + antxPropertiesFile
				+ ", autoconfigCharset=" + autoconfigCharset
				+ ", antxDestFiles=" + antxDestFiles
				+ ", antxIncludeDescriptorPatterns="
				+ antxIncludeDescriptorPatterns + ", serverId=" + serverId
				+ ", id=" + id + ", webRoot=" + webRoot + ", isConvert="
				+ isConvert + "]";
	}

	String name;
	List<String> projectNames = new ArrayList<String>();
	String warProjectName;
	String settingFile;
	String webxVersion;
	String antxPropertiesFile;
	String autoconfigCharset;
	String antxDestFiles;
	String antxIncludeDescriptorPatterns;
	String serverId;

	public String getAntxDestFiles() {
		return antxDestFiles;
	}

	public String getServerId() {
		return serverId;
	}

	public void setServerId(String serverId) {
		this.serverId = serverId;
	}

	public void setAntxDestFiles(String antxDestFiles) {
		this.antxDestFiles = antxDestFiles;
	}

	public String getAntxIncludeDescriptorPatterns() {
		return antxIncludeDescriptorPatterns;
	}

	public void setAntxIncludeDescriptorPatterns(String antxIncludeDescriptorPattern) {
		this.antxIncludeDescriptorPatterns = antxIncludeDescriptorPattern;
	}

	String id;
	String webRoot;

	public String getWebRoot() {
		return webRoot;
	}

	public void setWebRoot(String webRoot) {
		this.webRoot = webRoot;
	}

	public String getId() {
		return id;
	}

	public void setName(String name) {
		if (StringUtils.isBlank(id)) {
			id = name;
		}
		this.name = name;
	}

	
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	public String getAutoconfigCharset() {
		return autoconfigCharset;
	}

	public void setAutoconfigCharset(String autoconfigCharset) {
		this.autoconfigCharset = autoconfigCharset;
	}

	
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		JejuProject other = (JejuProject) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	public Boolean isConvert() {
		return isConvert;
	}

	public void setConvert(Boolean isConvert) {
		this.isConvert = isConvert;
	}

	Boolean isConvert = false;

	public String getSettingFile() {
		return settingFile;
	}

	public void setSettingFile(String settingFile) {
		this.settingFile = settingFile;
	}

	public String getWebxVersion() {
		return webxVersion;
	}

	public void setWebxVersion(String webxVersion) {
		this.webxVersion = webxVersion;
	}

	public String getAntxPropertiesFile() {
		return antxPropertiesFile;
	}

	public void setAntxPropertiesFile(String antxPropertiesFile) {
		this.antxPropertiesFile = antxPropertiesFile;
	}

	public JejuProject(String name) {
		setName(name);
	}

	JejuProject(String id, String name) {
		this.id = id;
		if (StringUtils.isBlank(this.id)) {
			this.id = name;
		}
		this.name = name;
	}

	public void setWarProjectName(String warProjectName) {
		this.warProjectName = warProjectName;
	}

	public String getName() {
		return name;
	}

	public List<String> getProjectNames() {
		return projectNames;
	}

	public void setProjectNames(List<String> projectNames) {
		this.projectNames = projectNames;
	}

	public void addProjectName(String projectName) {
		this.projectNames.add(projectName);
	}

	public String getWarProjectName() {
		if (warProjectName != null && warProjectName.trim().length() > 0)
			return warProjectName;
		for (String str : projectNames) {
			if (str.indexOf(".bundle.war") != -1)
				return str;
		}
		return "";
	}

	public boolean isWarProject(String projectName) {
		if (projectName == null)
			return false;
		if (projectName.equals(warProjectName))
			return true;
		return projectName.indexOf(".bundle.war") != -1;

	}

	public boolean isDeployProject(String projectName) {
		if (projectName == null)
			return false;
		return projectName.indexOf(".deploy") != -1;
	}

	public boolean contain(String projectName) {
		return projectNames.contains(projectName);
	}

	public boolean removeProject(String projectName) {
		return projectNames.remove(projectName);
	}

	public void save() {
		JejuProjectResourceManager.getInstance().addWebXProject(this);
	}

	public void delete() {
		JejuProjectResourceManager.getInstance().removeWebXProject(this);
	}

}
