package webx.studio.service.server.ui.actions;

import java.util.Arrays;


import org.apache.commons.lang.ObjectUtils;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.dialogs.ListDialog;

import webx.studio.ImageResource;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerUtil;
import webx.studio.utils.JdtUtil;

public class AddAction extends AbstractServerAction {

	public AddAction(ISelectionProvider selectionProvider, String text) {
		super(selectionProvider, text);
	}

	public AddAction(Shell shell, ISelectionProvider selectionProvider,
			String text) {
		super(shell, selectionProvider, text);
	}

	@Override
	public void perform(ServiceServer serviceServer) {
		// TODO Auto-generated method stub
		ListDialog dialog = new ListDialog(shell);
		dialog.setTitle("Add Projects");
		dialog.setMessage("Please choose one java project");
		dialog.setLabelProvider(createDialogLabelProvider());
		dialog.setContentProvider(new ArrayContentProvider());
		try {
			dialog.setInput(JdtUtil.getAllJavaProjects(Arrays.asList(serviceServer.getServiceProjects())));
		} catch (JavaModelException e) {
			e.printStackTrace();
		}
		if(dialog.open() == Window.OK){
			Object[] objs = dialog.getResult();
			if(objs == null || objs.length != 1)
				return;
			IJavaProject javaProject = (IJavaProject)objs[0];
			ServiceServerUtil.addJavaProjectToServiceServer(serviceServer, javaProject.getProject().getName());
		}
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
