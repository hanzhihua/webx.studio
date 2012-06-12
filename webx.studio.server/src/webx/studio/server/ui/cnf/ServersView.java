package webx.studio.server.ui.cnf;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.projectcreation.ui.views.ProjectView;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerChild;
import webx.studio.server.core.ServerCore;
import webx.studio.server.ui.actions.OpenAction;
import webx.studio.server.ui.event.IServerListener;
import webx.studio.server.ui.event.ServerDecoratorsHandler;
import webx.studio.server.ui.event.ServerEvent;

public class ServersView extends CommonNavigator {

	private static final String SERVERS_VIEW_CONTEXT = "jeju.server.serverViewScope";

	private CommonViewer tableViewer;

	private IServerLifecycleListener serverResourceListener;

	private IServerListener serverListener;

	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		IContextService contextSupport = (IContextService) getSite()
				.getService(IContextService.class);
		contextSupport.activateContext(SERVERS_VIEW_CONTEXT);

		deferInitialization();
	}

	private void deferInitialization() {

		Job job = new Job("Initializing Servers view") {
			public IStatus run(IProgressMonitor monitor) {
				deferredInitialize();
				return Status.OK_STATUS;
			}
		};

		job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule();
	}

	protected void deferredInitialize() {
		addListener();
		tableViewer = getCommonViewer();
		getSite().setSelectionProvider(tableViewer);
	}

	protected void addServer(final Server server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.add(tableViewer.getInput(), server);
				tableViewer.setSelection( new StructuredSelection( server ), true );
			}
		});
	}

	private void addListener() {

		serverResourceListener = new IServerLifecycleListener() {
			public void serverAdded(Server server) {
				addServer(server);
				server.addListener(serverListener);
			}

			public void serverChanged(Server server) {
				refreshServerContent(server);
			}

			public void serverRemoved(Server server) {
				removeServer(server);
				server.removeListener(serverListener);
			}
		};

		ServerCore.addServerLifecycleListener(serverResourceListener);

		serverListener = new IServerListener() {
			public void serverChanged(ServerEvent event) {
				refreshServerState(event.getServer());
			}

		};

		Server[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				((Server) servers[i]).addListener(serverListener);

			}
		}

	}

	private void refreshServerContent(final Server server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!tableViewer.getTree().isDisposed())
					tableViewer.refresh(server, true);
				tableViewer.setSelection( new StructuredSelection( server ), true );
			}
		});
	}

	private void removeServer(final Server server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.remove(server);
			}
		});
	}

	private void refreshServerState(Server server) {
		ServerDecoratorsHandler.refresh(tableViewer,server);
	}

	@Override
	protected void handleDoubleClick(DoubleClickEvent anEvent) {
		IStructuredSelection selection = (IStructuredSelection) anEvent
				.getSelection();
		Object element = selection.getFirstElement();

		if (element instanceof Server) {
			OpenAction.open((Server) element);
		} else if(element instanceof ServerChild){
			ServerChild sc = (ServerChild)element;
			JejuProject webxProject = JejuProjectCore
			.getWebXProject(sc.getName());
			if(webxProject == null)
				return;
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow() ;
			if (window != null) {
				IWorkbenchPage page = window.getActivePage();
				if (page != null) {
					IWorkbenchPart part = page.findView("jeju.projectcreation.view");
					if (part == null) {
						try {
							part = page.showView("jeju.projectcreation.view");
						} catch (PartInitException e) {
							ProjectCreationPlugin.logThrowable(e);
						}
					}
					if (part != null) {
						page.activate(part);
						if(part instanceof ProjectView){
							((ProjectView)part).select(webxProject);
						}

					}
				}
			}
		}else {
			super.handleDoubleClick(anEvent);
		}
	}

	@Override
	public void dispose() {
		ServerCore.removeServerLifecycleListener(serverResourceListener);
		Server[] servers = ServerCore.getServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				servers[i].removeListener(serverListener);
			}
		}
	}

}
