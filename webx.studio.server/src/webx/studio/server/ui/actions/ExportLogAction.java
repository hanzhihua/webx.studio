package webx.studio.server.ui.actions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;


import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.IWorkbenchWindowActionDelegate;
import org.osgi.framework.FrameworkUtil;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.core.JejuProjectResourceManager;
import webx.studio.server.LogConstants;
import webx.studio.server.ServerPlugin;
import webx.studio.server.core.ServerResourceManager;

/**
 * @author zhihua.hanzh
 *
 */
public class ExportLogAction implements IWorkbenchWindowActionDelegate {


	public void run(IAction action) {
		FileDialog dialog = new FileDialog(ServerPlugin
				.getActiveWorkbenchShell(), SWT.SAVE);
		dialog.setFilterExtensions(new String[] { "*.log" });
		dialog.setFileName("jeju");
		String path = dialog.open();

		if (path != null) {
			if (path.indexOf('.') == -1 && !path.endsWith(".log")) //$NON-NLS-1$
				path += ".log"; //$NON-NLS-1$
			File outputFile = new Path(path).toFile();
			if (outputFile.exists()) {
				String message = NLS
						.bind(
								"File \" {0}\" exists.  Would you like to overwrite it?",
								outputFile.toString());
				if (!MessageDialog.openQuestion(ServerPlugin
						.getActiveWorkbenchShell(), "Export Log", message))
					;
				return;
			}

			try {

				String content = FileUtils.readFileToString(new File(
						LogConstants.LOG_FILE),"UTF-8")+SystemUtils.LINE_SEPARATOR;
				content += "===============jeju model===================="+SystemUtils.LINE_SEPARATOR;
				content += "Jeju Version:"+FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getVersion()+SystemUtils.LINE_SEPARATOR;
				content += "===============jeju servers=================="+SystemUtils.LINE_SEPARATOR;
				File serversFile = new File(ServerPlugin.getInstance()
						.getStateLocation().append(
								ServerResourceManager.SERVER_DATA_FILE)
						.toOSString());
				if(!serversFile.exists()){
					content +="Servers File hasn't exist yet! "+SystemUtils.LINE_SEPARATOR;
				}else{
					content +=FileUtils
					.readFileToString(serversFile,"UTF-8")+SystemUtils.LINE_SEPARATOR;
				}

				content += "===============jeju projects================="+SystemUtils.LINE_SEPARATOR;
				File projectsFile = new File(ProjectCreationPlugin.getDefault()
						.getStateLocation().append(
								JejuProjectResourceManager.WEBX_PROJECT_METADATA_FILE)
						.toOSString());
				if(!projectsFile.exists()){
					content +="Projects File hasn't exist yet! "+SystemUtils.LINE_SEPARATOR;
				}else{
					content += FileUtils.readFileToString(projectsFile,"UTF-8")+SystemUtils.LINE_SEPARATOR;
				}
				FileUtils.writeStringToFile(outputFile, content);
			} catch (Exception e) {
				try {
					FileUtils.writeStringToFile(
							new File(LogConstants.LOG_FILE), ExceptionUtils
									.getFullStackTrace(e));
				} catch (IOException e1) {
					// ignore
					ServerPlugin.logError(e1);
				}

			}
		}

	}

	private void copy(Reader input, Writer output) {
		BufferedReader reader = null;
		BufferedWriter writer = null;
		try {
			reader = new BufferedReader(input);
			writer = new BufferedWriter(output);
			String line;
			while (reader.ready() && ((line = reader.readLine()) != null)) {
				writer.write(line);
				writer.newLine();
			}
		} catch (IOException e) { // do nothing
		} finally {
			try {
				if (reader != null)
					reader.close();
				if (writer != null)
					writer.close();
			} catch (IOException e1) {
				// do nothing
			}
		}
	}


	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub

	}


	public void dispose() {
		// TODO Auto-generated method stub

	}


	public void init(IWorkbenchWindow window) {
		// TODO Auto-generated method stub

	}

}
