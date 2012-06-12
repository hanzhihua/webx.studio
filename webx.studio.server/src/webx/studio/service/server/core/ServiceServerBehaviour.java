package webx.studio.service.server.core;

import org.eclipse.debug.core.DebugEvent;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.IDebugEventSetListener;
import org.eclipse.debug.core.model.IProcess;

public class ServiceServerBehaviour {

	protected transient IDebugEventSetListener processListener;
	private final ServiceServer server;

	public ServiceServerBehaviour(ServiceServer server){
		this.server = server;
	}

	void addProcessListener(final IProcess newProcess) {
		if (processListener != null || newProcess == null)
			return;

		processListener = new IDebugEventSetListener() {
			public void handleDebugEvents(DebugEvent[] events) {
				if (events != null) {
					int size = events.length;
					for (int i = 0; i < size; i++) {
						if (newProcess != null
								&& newProcess.equals(events[i].getSource())
								&& events[i].getKind() == DebugEvent.TERMINATE) {
							stop();
						}
					}
				}
			}
		};

		DebugPlugin.getDefault().addDebugEventListener(processListener);
	}

	void stop() {
		if (processListener != null) {
			DebugPlugin.getDefault().removeDebugEventListener(processListener);
			processListener = null;
		}
		if(server != null)
			server.stopWithoutTerminate();
	}

}
