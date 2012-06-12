package webx.studio.server.ui.editor;

import org.eclipse.ui.IEditorInput;

public interface IServerEditorInput  extends IEditorInput {

	public static final String EDITOR_ID = "jeju.server.ui.editor";


	public String getServerId();
}
