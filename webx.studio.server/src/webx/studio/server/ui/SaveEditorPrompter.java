package webx.studio.server.ui;

import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.PlatformUI;

public class SaveEditorPrompter {

	public void saveAllEditors() {
		IWorkbench w = PlatformUI.getWorkbench();
		String saveBeforeLaunch = DebugUITools
				.getPreferenceStore()
				.getString(
						org.eclipse.debug.internal.ui.IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH);
		if (saveBeforeLaunch
				.equalsIgnoreCase(org.eclipse.jface.dialogs.MessageDialogWithToggle.ALWAYS)) {
			Display d = PlatformUI.getWorkbench().getDisplay();
			d.syncExec(new SaveAllEditorsRunnable(w, false));
		} else if (saveBeforeLaunch
				.equalsIgnoreCase(org.eclipse.jface.dialogs.MessageDialogWithToggle.PROMPT)) {
			Display d = PlatformUI.getWorkbench().getDisplay();
			d.syncExec(new SaveAllEditorsRunnable(w, true));
		}
	}

	private class SaveAllEditorsRunnable implements Runnable {
		IWorkbench w;
		boolean confirm;

		public SaveAllEditorsRunnable(IWorkbench w, boolean confirm) {
			this.w = w;
			this.confirm = confirm;
		}

		public void run() {
			w.saveAllEditors(confirm);
		}
	}
	
//	public static boolean isRunningGUIMode() {
//		
//		
//		switch (UIContextDetermination.getCurrentContext()) {
//		case UIContextDetermination.UI_CONTEXT:
//			isGui = true;
//			break;
//		case UIContextDetermination.HEADLESS_CONTEXT:
//		default:
//			isGui = false;
//		}
//
//		
//	}
	
	public String setDebugSaveBeforeLaunching(String newValue){
		IPreferenceStore debugPrefs = DebugUITools.getPreferenceStore();
		String oldValue = debugPrefs.getString(org.eclipse.debug.internal.ui.IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH);
		debugPrefs.setValue(org.eclipse.debug.internal.ui.IInternalDebugUIConstants.PREF_SAVE_DIRTY_EDITORS_BEFORE_LAUNCH, newValue);
		return oldValue; 
	}
	
	private String cachedSaveBeforeLaunch;
	
	public void setDebugNeverSave(){
		cachedSaveBeforeLaunch = setDebugSaveBeforeLaunching(org.eclipse.jface.dialogs.MessageDialogWithToggle.NEVER);
	}
	
	public void setDebugOriginalValue(){
		if (cachedSaveBeforeLaunch == null){
			cachedSaveBeforeLaunch = org.eclipse.jface.dialogs.MessageDialogWithToggle.PROMPT;
		}
		setDebugSaveBeforeLaunching(cachedSaveBeforeLaunch);
	}

}
