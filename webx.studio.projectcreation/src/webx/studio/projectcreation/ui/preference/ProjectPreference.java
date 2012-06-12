package webx.studio.projectcreation.ui.preference;


import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.StringFieldEditor;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;

public class ProjectPreference  extends FieldEditorPreferencePage implements
		IWorkbenchPreferencePage {

	public ProjectPreference(){
		super(GRID);
		setPreferenceStore(ProjectCreationPlugin.getDefault()
				.getPreferenceStore());
		setDescription("General Project Settings");
		setTitle("Project Preference");
		initializeDefaults();
	}

	private void initializeDefaults() {
		IPreferenceStore store = getPreferenceStore();
		store.setDefault(PreferenceConstants.WEB_ROOT_ARRAY, "WebContent,src/main/webapp,src/webroot,WebRoot");
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected void createFieldEditors() {
		addField(
				new StringFieldEditor(PreferenceConstants.WEB_ROOT_ARRAY, "Web root:", getFieldEditorParent()));
	}



}
