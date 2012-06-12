package webx.studio.projectcreation.ui.views;

import webx.studio.projectcreation.ui.core.JejuProject;

public class WebxProjectChildNode {

	private String name;
	private JejuProject webxProject;

	public WebxProjectChildNode(JejuProject webxProject,String name){
		this.name = name;
		this.webxProject = webxProject;
	}

	public String getName() {
		return name;
	}

	public JejuProject getWebxProject() {
		return webxProject;
	}



}
