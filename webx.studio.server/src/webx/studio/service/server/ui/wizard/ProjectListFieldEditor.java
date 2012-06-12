package webx.studio.service.server.ui.wizard;

import java.util.Arrays;


import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.preference.StringButtonFieldEditor;
import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.dialogs.ListDialog;

import webx.studio.ImageResource;
import webx.studio.server.ServerPlugin;
import webx.studio.utils.JdtUtil;

public class ProjectListFieldEditor extends StringButtonFieldEditor{

	public ProjectListFieldEditor(String name, String labelText, Composite parent) {
        init(name, labelText);
        setChangeButtonText(JFaceResources.getString("openBrowse"));//$NON-NLS-1$
        setValidateStrategy(VALIDATE_ON_FOCUS_LOST);
        createControl(parent);
    }


	@Override
	protected String changePressed() {
		ListDialog dialog = new ListDialog(ServerPlugin.getActiveWorkbenchShell());
		dialog.setTitle("Add Projects");
		dialog.setMessage("Please choose one java project");
		dialog.setLabelProvider(createDialogLabelProvider());
		dialog.setContentProvider(new ArrayContentProvider());
		try {
			dialog.setInput(JdtUtil.getAllJavaProjects(null));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		if(dialog.open() == Window.OK){
			Object[] objs = dialog.getResult();
			if(objs == null || objs.length != 1)
				return null;
			IJavaProject javaProject = (IJavaProject)objs[0];
			return javaProject.getProject().getName();
		}
		return null;
	}

	private ILabelProvider createDialogLabelProvider() {
		return new LabelProvider() {
			public Image getImage(Object element) {
				if(element instanceof IJavaProject){
					return ImageResource.getImage(ImageResource.IMG_JAVA_PROJECT);
				}
				return null;
			}
			public String getText(Object element) {
				if(element instanceof IJavaProject){
					return ((IJavaProject)element).getProject().getName();
				}
				return ObjectUtils.toString(element);
			}
		};
	}

}
