package webx.studio.projectcreation.ui.editor;

import org.eclipse.ui.IEditorInput;

public interface IProjectEditorInput   extends IEditorInput {

	public static final String EDITOR_ID = "jeju.projectcreation.ui.editor";


	public String getProjectId();
}
