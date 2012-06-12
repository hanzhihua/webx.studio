package webx.studio.server.ui.event;

import java.util.ArrayList;


import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IDecoratorManager;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;

import webx.studio.server.core.Server;
import webx.studio.service.server.core.ServiceServer;

public class ServerDecoratorsHandler {

	final static String NAVIGATOR_DECORATOR_ID = "jeju.server.ui.navigatorDecorator2";

	protected static ArrayList<String> UIDecoratorsIDs = new ArrayList<String>();

	static {
		UIDecoratorsIDs.add(NAVIGATOR_DECORATOR_ID);
	}
	protected static IDecoratorManager decoratorManager = null;

	protected static IDecoratorManager getDecoratorManager() {
		if (decoratorManager == null) {
			decoratorManager = PlatformUI.getWorkbench().getDecoratorManager();
		}
		return decoratorManager;
	}

	/**
	 * Used to refresh the Server Decorators previously added and set selection
	 * after that. Triggers the decoration event of each Decorator.
	 * */
	public static void refresh(final CommonViewer tableViewer,
			final Server server) {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IDecoratorManager dm = PlatformUI.getWorkbench()
						.getDecoratorManager();
				ArrayList<String> UIDecoratorsIDsClone = (ArrayList<String>) UIDecoratorsIDs
						.clone();
				for (String decoratorId : UIDecoratorsIDsClone) {
					dm.update(decoratorId);
				}
				if (server != null) {
					tableViewer.refresh(server, true);
				}
				if (tableViewer != null) {
					tableViewer.setSelection(tableViewer.getSelection());
				}
				tableViewer.refresh();
			}
		});
	}

	public static void refresh(final CommonViewer tableViewer,
			final ServiceServer server) {

		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				IDecoratorManager dm = PlatformUI.getWorkbench()
						.getDecoratorManager();
				ArrayList<String> UIDecoratorsIDsClone = (ArrayList<String>) UIDecoratorsIDs
						.clone();
				for (String decoratorId : UIDecoratorsIDsClone) {
					dm.update(decoratorId);
				}
				if (server != null) {
					tableViewer.refresh(server, true);
				}
				if (tableViewer != null) {
					tableViewer.setSelection(tableViewer.getSelection());
				}
				tableViewer.refresh();
			}
		});
	}

	// /**
	// * Used to refresh the Server Decorators previously added.
	// *
	// * @param server
	// * */
	// public static void refresh() {
	// refresh(null);
	// }

	/**
	 * Remove a UI Decorator from the Decorator Handler.
	 *
	 * @param decoratorID
	 */
	public static void removeUIDecoratorsID(String decoratorID) {
		synchronized (UIDecoratorsIDs) {
			UIDecoratorsIDs.remove(decoratorID);
		}
	}

	/**
	 * Adds a new UI Decorator from the Decorator Handler.
	 *
	 * @param decoratorID
	 */
	public static void addUIDecoratorsIDs(String decoratorID) {
		synchronized (UIDecoratorsIDs) {
			if (!UIDecoratorsIDs.contains(decoratorID)) {
				UIDecoratorsIDs.add(decoratorID);
			}
		}
	}
}
