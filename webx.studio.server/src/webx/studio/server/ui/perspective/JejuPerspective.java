package webx.studio.server.ui.perspective;

import org.eclipse.debug.ui.IDebugUIConstants;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;

public class JejuPerspective implements org.eclipse.ui.IPerspectiveFactory {

	public static final String ID_PROJECT_EXPLORER= "org.eclipse.ui.navigator.ProjectExplorer";
	private static final String ID_JEJU_SERVER_VIEWER = "jeju.server.ui.ServersView";
	private static final String ID_JEJU_PROJECT_VIEWER = "jeju.projectcreation.view";
	private static final String ID_JEJU_SERVICE_SERVER_VIEWER = "jeju.service.server.ui.ServiceServersView";

	public void createInitialLayout(IPageLayout layout) {
		defineLayout(layout);
		defineActions(layout);
	}

	public void defineActions(IPageLayout layout) {
		layout.addActionSet("org.eclipse.jdt.ui.JavaActionSet");
		layout.addActionSet(IDebugUIConstants.LAUNCH_ACTION_SET);
		layout.addActionSet(IDebugUIConstants.DEBUG_ACTION_SET);

		layout.addActionSet(IPageLayout.ID_NAVIGATE_ACTION_SET);
//		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.JavaProjectWizard");
//		layout.addNewWizardShortcut(ID_JEJU_SERVER_VIEWER);
//		layout.addNewWizardShortcut(ID_JEJU_PROJECT_VIEWER);
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewPackageCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewClassCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewInterfaceCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewEnumCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewAnnotationCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSourceFolderCreationWizard");	 //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewSnippetFileCreationWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.jdt.ui.wizards.NewJavaWorkingSetWizard"); //$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.folder");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.wizards.new.file");//$NON-NLS-1$
		layout.addNewWizardShortcut("org.eclipse.ui.editors.wizards.UntitledTextFileWizard");//$NON-NLS-1$
	}

	public void defineLayout(IPageLayout layout) {
		String editorArea = layout.getEditorArea();
		IFolderLayout topLeft = layout.createFolder("topLeft", IPageLayout.LEFT, 0.25f, editorArea);//$NON-NLS-1$
		topLeft.addView(JavaUI.ID_PACKAGES);
		topLeft.addView(JavaUI.ID_TYPE_HIERARCHY);
		topLeft.addPlaceholder(IPageLayout.ID_RES_NAV);
		topLeft.addPlaceholder(ID_PROJECT_EXPLORER);

		IFolderLayout bottomRight = layout.createFolder("bottomRight", IPageLayout.BOTTOM, 0.7f, editorArea);//$NON-NLS-1$
		bottomRight.addView(ID_JEJU_SERVER_VIEWER);
		bottomRight.addView(ID_JEJU_PROJECT_VIEWER);
		bottomRight.addView(ID_JEJU_SERVICE_SERVER_VIEWER);
		bottomRight.addView(IPageLayout.ID_PROBLEM_VIEW);
		bottomRight.addView(JavaUI.ID_JAVADOC_VIEW);
		bottomRight.addView(JavaUI.ID_SOURCE_VIEW);

		IFolderLayout topRight = layout.createFolder("topRight", IPageLayout.RIGHT, 0.7f, editorArea);//$NON-NLS-1$
		topRight.addView(IPageLayout.ID_OUTLINE);
	}

}
