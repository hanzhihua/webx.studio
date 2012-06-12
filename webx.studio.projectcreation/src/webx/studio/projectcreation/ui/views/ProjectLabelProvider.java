package webx.studio.projectcreation.ui.views;


import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.swt.graphics.Image;

import webx.studio.ImageResource;
import webx.studio.projectcreation.ui.core.JejuProject;

public class ProjectLabelProvider  extends ColumnLabelProvider {

	public String getText(Object element) {
		if(element instanceof JejuProject)
			return ((JejuProject)element).getName();
		if(element instanceof WebxProjectChildNode)
			return ((WebxProjectChildNode)element).getName();
		return "";
	}

	public Image getImage(Object element) {

		if (element instanceof JejuProject) {
			return ImageResource.getImage("jeju_project");
		}else if(element instanceof WebxProjectChildNode){
			return ImageResource.getImage("java_project");
		}

		return null;
	}
}
