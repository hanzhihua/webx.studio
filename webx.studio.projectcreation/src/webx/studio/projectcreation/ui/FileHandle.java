package webx.studio.projectcreation.ui;


import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.filesystem.EFS;
import org.eclipse.core.filesystem.IFileStore;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;

import webx.studio.projectcreation.ui.core.JejuProjectResourceManager;

public class FileHandle extends AbstractHandler {

	public Object execute(ExecutionEvent event) throws ExecutionException {
		String id = event.getCommand().getId();
		if(id.endsWith("edit")){
			IPath path = ProjectCreationPlugin.getDefault().getStateLocation()
			.append(JejuProjectResourceManager.WEBX_PROJECT_METADATA_FILE);
			IFileStore fileStore = EFS.getLocalFileSystem().getStore(path);
			 try {
				IDE.openEditorOnFileStore(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage(), fileStore);
			} catch (PartInitException e) {
//				Trace.traceError(e);
			}
			return null;
		}
		return null;
	}



}
