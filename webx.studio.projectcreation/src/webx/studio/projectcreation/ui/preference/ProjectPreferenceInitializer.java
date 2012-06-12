package webx.studio.projectcreation.ui.preference;


import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;
import org.eclipse.core.runtime.preferences.DefaultScope;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.IScopeContext;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;

public class ProjectPreferenceInitializer extends AbstractPreferenceInitializer {

	public ProjectPreferenceInitializer() {
		// TODO Auto-generated constructor stub
	}

	
	public void initializeDefaultPreferences() {
		IEclipsePreferences store = ((IScopeContext) new DefaultScope()).getNode(ProjectCreationPlugin.PLUGIN_ID);
		store.put(PreferenceConstants.WEB_ROOT_ARRAY, "WebContent,src/main/webapp,src/webroot,WebRoot");

	}

}
