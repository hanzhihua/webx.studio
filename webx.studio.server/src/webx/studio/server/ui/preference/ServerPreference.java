package webx.studio.server.ui.preference;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import webx.studio.server.ServerPlugin;

public class ServerPreference extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ServerPreference(){
		super(GRID);
		setPreferenceStore(ServerPlugin.getDefault()
				.getPreferenceStore());
		setDescription("General Server Settings");
		setTitle("Server Preference");
		initializeDefaults();
	}

	private void initializeDefaults() {
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
	}



}
