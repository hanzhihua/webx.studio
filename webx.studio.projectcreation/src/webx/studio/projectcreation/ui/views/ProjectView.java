package webx.studio.projectcreation.ui.views;

import java.util.Iterator;


import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import webx.studio.projectcreation.ui.actions.OpenProjectAction;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;

public class ProjectView extends CommonNavigator {

	private static final String PROJECTS_VIEW_CONTEXT = "jeju.projectcreation.viewScope";

	protected CommonViewer tableViewer;

	protected IWebXProjectLifecycleListener listener;

	public ProjectView() {
		// TODO Auto-generated constructor stub
	}

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		tableViewer = getCommonViewer();
		IContextService contextSupport = (IContextService) getSite()
				.getService(IContextService.class);
		contextSupport.activateContext(PROJECTS_VIEW_CONTEXT);
		addListener();
	}

	protected void addListener() {
		listener = new IWebXProjectLifecycleListener() {

			public void projectRemoved(JejuProject project) {
				removeProject(project);

			}

			public void projectChanged(JejuProject project) {
				refreshProjectsContent(project);

			}

			public void projectAdded(JejuProject project) {
				addProject(project);

			}

			public void reloadProjects() {
				changeInput();

			}
		};
		JejuProjectCore.addWebXProjectLifecycleListener(listener);
	}

	protected void refreshProjectsContent(final JejuProject project) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!tableViewer.getTree().isDisposed())
					tableViewer.refresh(project, true);
			}
		});
	}

	protected void addProject(final JejuProject project) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.add(tableViewer.getInput(), project);
				tableViewer.setSelection( new StructuredSelection( project ), true );
			}
		});
	}

	protected void removeProject(final JejuProject project) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.remove(project);
			}
		});
	}

	protected void changeInput(){
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.setInput(new Object());
			}
		});
	}

	protected void handleDoubleClick(DoubleClickEvent anEvent) {
		IStructuredSelection selection = (IStructuredSelection) anEvent
				.getSelection();
		Object element = selection.getFirstElement();

		JejuProject project = null;
		if (selection != null && !selection.isEmpty()) {
			Iterator iterator = selection.iterator();
			Object obj = iterator.next();
			if (obj instanceof JejuProject)
				project = (JejuProject) obj;
			if (iterator.hasNext()) {
				project = null;
			}
		}
		if(project != null){
			OpenProjectAction.open(project);
		}else{
			super.handleDoubleClick(anEvent);
		}

	}

	public void select(JejuProject jp){
		if(jp == null || tableViewer == null)
			return;
		tableViewer.setSelection( new StructuredSelection( jp ), true );
		OpenProjectAction.open(jp);
	}
}
