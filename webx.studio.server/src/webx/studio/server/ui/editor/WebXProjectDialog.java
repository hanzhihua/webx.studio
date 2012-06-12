package webx.studio.server.ui.editor;

import java.util.List;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import webx.studio.ImageResource;
import webx.studio.server.core.ServerUtil;

public class WebXProjectDialog extends Dialog {

	private String webxProjectName;
	private String contextPath;

	private Text webxProjectNameText;
	private Text path;
	private String newcontent = "";

	private boolean isAddButton;
	protected Table _projTable;
//	private Server server;
	private List<String> webxProjects;

	protected WebXProjectDialog(Shell parentShell, String webxProjectName,
			String contextPath) {
		super(parentShell);
		this.webxProjectName = webxProjectName;
		this.contextPath = contextPath;
		newcontent = webxProjectNameText + ServerEditor.SPEA_CHAR + contextPath;
	}

	protected WebXProjectDialog(Shell parentShell, List<String> webxProjects) {
		super(parentShell);
//		this.server = server;
		this.webxProjects = webxProjects;
		this.isAddButton = true;
	}

	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_MARGIN);
		layout.marginWidth = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_MARGIN);
		layout.verticalSpacing = convertVerticalDLUsToPixels(IDialogConstants.VERTICAL_SPACING);
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(IDialogConstants.HORIZONTAL_SPACING);
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		composite.setFont(parent.getFont());

		if (isAddButton) {
			Label l = new Label(composite, SWT.NONE);
			l.setText("WebX Projects");
			GridData data = new GridData(GridData.VERTICAL_ALIGN_BEGINNING);
			l.setLayoutData(data);

			_projTable = new Table(composite, SWT.BORDER | SWT.V_SCROLL
					| SWT.H_SCROLL | SWT.SINGLE);
			data = new GridData();
			data.widthHint = 150;
			data.heightHint = 75;
			_projTable.setLayoutData(data);

			for (String str : ServerUtil.getCanBeDeployedWebXProjects(webxProjects)) {
				TableItem item = new TableItem(_projTable, SWT.NONE);
				item.setText(0, ServerUtil.getWebXProjectName(str));
				item.setImage(0, ImageResource.getImage(ImageResource.IMG_JEJU_PROJECT));
				item.setData(ServerUtil.getWebXProjectName(str));
			}
			new Label(composite, SWT.NONE).setText(" ");

		} else {

			GridData data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
			data.widthHint = 150;

			new Label(composite, SWT.NONE).setText("WebX Project Name:");
			webxProjectNameText = new Text(composite, SWT.BORDER);
			webxProjectNameText.setLayoutData(data);
			webxProjectNameText.setText(this.webxProjectName);
			webxProjectNameText.setEditable(false);
			webxProjectNameText.addModifyListener(new ModifyListener() {
				public void modifyText(ModifyEvent e) {
					modifyContent();
				}
			});
			new Label(composite, SWT.NONE).setText(" ");
		}

		new Label(composite, SWT.NONE).setText("Context Path:");
		path = new Text(composite, SWT.BORDER);

		path.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		if (isAddButton) {
			path.setText("");
		} else {
			path.setText(this.contextPath);
		}
		path.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				modifyContent();
			}
		});
		new Label(composite, SWT.NONE).setText("");

		if (isAddButton) {
			_projTable.addSelectionListener(new SelectionAdapter() {
				public void widgetSelected(SelectionEvent event) {
					String webxProjectName = (String) _projTable.getSelection()[0]
							.getData();
					path.setText(ServerUtil.getContextPath(webxProjectName));
				}
			});
		}

		Dialog.applyDialogFont(composite);
		return composite;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		if (!isAddButton)
			newShell.setText("Edit WebX Project");
		else
			newShell.setText("Add WebX Project");
	}

	public String getNewWebXProjectText() {
		return this.newcontent;

	}

	private void modifyContent() {
		if (!isAddButton)
			this.newcontent = webxProjectNameText.getText()
					+ ServerEditor.SPEA_CHAR + path.getText();
		else
			this.newcontent = (String) _projTable.getSelection()[0].getData()
					+ ServerEditor.SPEA_CHAR + path.getText();
	}

}
