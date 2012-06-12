package webx.studio.service.server.ui.cnf;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.navigator.CommonViewer;

import webx.studio.server.ui.event.ServerDecoratorsHandler;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerResourceManager;
import webx.studio.service.server.core.ServiceServerUtil;
import webx.studio.service.server.ui.actions.OpenAction;
import webx.studio.service.server.ui.event.IServiceServerListener;
import webx.studio.service.server.ui.event.ServiceServerEvent;

public class ServiceServersView extends CommonNavigator {

	private CommonViewer tableViewer;
	private static final String SERVICE_SERVERS_VIEW_CONTEXT = "jeju.service.server.serverViewScope";

	private IServiceServerLifecycleListener serviceServerResourceListener;
	private IServiceServerListener serverListener;

	public ServiceServersView() {
		deferInitialization();
	}

	private void deferInitialization() {

		Job job = new Job("Initializing Service server view") {
			public IStatus run(IProgressMonitor monitor) {
				deferredInitialize();
				return Status.OK_STATUS;
			}
		};

		job.setSystem(true);
		job.setPriority(Job.SHORT);
		job.schedule();
//		deferredInitialize();
	}

	protected void deferredInitialize() {
		addListener();

	}

	private void addListener() {

		serviceServerResourceListener = new IServiceServerLifecycleListener() {


			public void serviceServerAdded(ServiceServer serviceServer) {
				addServiceServer(serviceServer);
				serviceServer.addListener(serverListener);

			}


			public void serviceServerChanged(ServiceServer serviceServer) {
				refreshServerContent(serviceServer);

			}


			public void serviceServerRemoved(ServiceServer serviceServer) {
				removeServiceServer(serviceServer);
				serviceServer.removeListener(serverListener);

			}

		};

		ServiceServerResourceManager.getInstance()
				.addServiceServerLifecycleListener(
						serviceServerResourceListener);

		serverListener = new IServiceServerListener() {


			public void serviceServerChanged(ServiceServerEvent event) {
				refreshServerState(event.getServiceServer());

			}

		};

		ServiceServer[] serviceServers = ServiceServerUtil.getServiceServers();
		if (serviceServers != null) {
			int size = serviceServers.length;
			for (int i = 0; i < size; i++) {
				((ServiceServer) serviceServers[i]).addListener(serverListener);

			}
		}

	}


	public void createPartControl(Composite parent) {
		super.createPartControl(parent);
		tableViewer = getCommonViewer();
		getSite().setSelectionProvider(tableViewer);
		IContextService contextSupport = (IContextService) getSite()
				.getService(IContextService.class);
		contextSupport.activateContext(SERVICE_SERVERS_VIEW_CONTEXT);

	}


	public void setFocus() {
		// TODO Auto-generated method stub

	}

	protected void addServiceServer(final ServiceServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.add(tableViewer.getInput(), server);
				tableViewer.setSelection(new StructuredSelection(server), true);
			}
		});
	}

	private void removeServiceServer(final ServiceServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				tableViewer.remove(server);
			}
		});
	}

	private void refreshServerContent(final ServiceServer server) {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				if (!tableViewer.getTree().isDisposed())
					tableViewer.refresh(server, true);
				StructuredSelection obj = new StructuredSelection(server);
				tableViewer.setSelection(obj, true);
				tableViewer.expandToLevel(server, 2);
			}
		});
	}

	private void refreshServerState(ServiceServer server) {
		ServerDecoratorsHandler.refresh(tableViewer, server);
	}


	protected void handleDoubleClick(DoubleClickEvent anEvent) {
		IStructuredSelection selection = (IStructuredSelection) anEvent
				.getSelection();
		Object element = selection.getFirstElement();
		if (element instanceof ServiceServer) {
			OpenAction.open((ServiceServer) element);
		}else {
			super.handleDoubleClick(anEvent);
		}
	}


	public void dispose() {
		ServiceServerResourceManager.getInstance().removeServiceServerLifecycleListener(serviceServerResourceListener);
		ServiceServer[] servers = ServiceServerUtil.getServiceServers();
		if (servers != null) {
			int size = servers.length;
			for (int i = 0; i < size; i++) {
				servers[i].removeListener(serverListener);
			}
		}
	}

}
