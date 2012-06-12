package webx.studio.server.core;

public class WebModule {

	String webxProjectName;
	String contextPath;
	String webappdir;
	String classpath;

	public String getClasspath() {
		return classpath;
	}
	public void setClasspath(String classpath) {
		this.classpath = classpath;
	}
	public String getWebxProjectName() {
		return webxProjectName;
	}
	public void setWebxProjectName(String webxProjectName) {
		this.webxProjectName = webxProjectName;
	}
	public String getContextPath() {
		return contextPath;
	}
	public void setContextPath(String contextPath) {
		this.contextPath = contextPath;
	}
	public String getWebappdir() {
		return webappdir;
	}
	public void setWebappdir(String webappdir) {
		this.webappdir = webappdir;
	}

}
