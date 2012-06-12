package webx.studio.server.ui.launchtab;

import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.CommonTab;
import org.eclipse.debug.ui.EnvironmentTab;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;
import org.eclipse.debug.ui.sourcelookup.SourceLookupTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaClasspathTab;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaJRETab;

public class JettyLaunchConfigurationTabGroup extends
		AbstractLaunchConfigurationTabGroup {

	public JettyLaunchConfigurationTabGroup() {

	}

	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {

		JavaClasspathTab jct = new JavaClasspathTab(){
			public void initializeFrom(ILaunchConfiguration configuration) {
				super.initializeFrom(configuration);
				fClasspathViewer.expandToLevel(3);
			}
		};

		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new JettyRunTab(),new WebappTab(), new JavaArgumentsTab(), new JavaJRETab(),
				jct, new SourceLookupTab(),
				new EnvironmentTab(), new CommonTab() };
		setTabs(tabs);

	}

}
