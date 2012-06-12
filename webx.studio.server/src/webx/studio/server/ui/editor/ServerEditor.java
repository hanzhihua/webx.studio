package webx.studio.server.ui.editor;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;

import webx.studio.ImageResource;
import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerCore;
import webx.studio.server.core.ServerUtil;
import webx.studio.server.ui.cnf.IServerLifecycleListener;
import webx.studio.server.ui.cnf.ServerLabelProvider;

public class ServerEditor extends org.eclipse.ui.part.EditorPart {

	public final static String SPEA_CHAR = "::";

	private String serverId;
	private Server server;
	private ManagedForm managedForm;
	private Text serverName;
	private Text serverPort;
	private Text host;
	// private
	protected Table webxProjectTable;

	protected Button _addProjectButton;
	protected Button _removeButton;
	protected Button _editButton;

	private Button openBrowser;
	private Button withoutAutoconfig;
	private Button withoutReloadFuction;

	private Combo uriCharset;

	private boolean isDirty = false;
	protected boolean updating;

	protected int _selection = -1;

	private List<String> tmpWebxProjects = null;

	@Override
	public void doSave(IProgressMonitor monitor) {
		if (server.getServerState() != Server.STATE_STOPPED) {
			MessageDialog.openError(getEditorSite().getShell(), getPartName(),
					"The server must be stopped before you change .");
			return;
		}
		if (server != null){
			changeServer();
			server.save();
		}
		setDirty(false);
		setPartName(new ServerLabelProvider().getText(server));
	}

	private void changeServer() {
		server.setName(serverName.getText());
		server.setPort(serverPort.getText());
		server.setHost(host.getText());
		server.setOpenBrowser(openBrowser.getSelection());
		server.setWithoutAutoconfig(withoutAutoconfig.getSelection());
		server.setWithoutReloadfunction(withoutReloadFuction.getSelection());
		server.setUriCharset(uriCharset.getText());
		if (tmpWebxProjects != null){
			server.setWebXProjects(tmpWebxProjects);
//			tmpWebxProjects = null;
		}

	}

	@Override
	public void doSaveAs() {
		 if (server != null)
		 server.save();
		 setDirty(false);
	}

	@Override
	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		if (input instanceof IFileEditorInput) {
			IFileEditorInput fei = (IFileEditorInput) input;
			IFile file = fei.getFile();
		} else if (input instanceof IServerEditorInput) {
			IServerEditorInput sei = (IServerEditorInput) input;
			serverId = sei.getServerId();
			server = (Server) ServerCore.findServer(serverId);
		}
		ServerLabelProvider labelProvider = new ServerLabelProvider();
		if (server != null) {

			setPartName(labelProvider.getText(server));
			setTitleImage(labelProvider.getImage(server));
			setTitleToolTip(serverId);
		} else {
			setPartName("-");
		}
		labelProvider.dispose();
		labelProvider = null;

		resourceListener = new LifecycleListener();
		ServerCore.addServerLifecycleListener(resourceListener);
	}

	@Override
	public boolean isDirty() {
		return this.isDirty;
	}

	@Override
	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void createPartControl(Composite parent) {
		managedForm = new ManagedForm(parent);
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		toolkit.decorateFormHeading(form.getForm());
		form.setText("Overview");
		form.setImage(ImageResource.getImage(ImageResource.IMG_GENERAL_SERVER));
		form.getBody().setLayout(new GridLayout());

		Composite columnComp = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		columnComp.setLayout(layout);
		columnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));

		Composite leftColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		leftColumnComp.setLayout(layout);
		leftColumnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));
		createGeneralSection(leftColumnComp, toolkit);

		Composite rightColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		rightColumnComp.setLayout(layout);
		rightColumnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));
		createWebXProjectSection(rightColumnComp, toolkit);

		form.reflow(true);

	}

	protected void createWebXProjectSection(Composite rightColumnComp,
			FormToolkit toolkit) {
		ScrolledForm form = toolkit.createScrolledForm(rightColumnComp);
		GridLayout layout = new GridLayout();
		form.getBody().setLayout(layout);

		Section section = toolkit.createSection(form.getBody(),
				ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText("WebX Projects");
		section.setLayoutData(new GridData(GridData.FILL_BOTH));

		Composite composite = toolkit.createComposite(section);
		layout = new GridLayout();
		layout.numColumns = 2;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		webxProjectTable = toolkit.createTable(composite, SWT.V_SCROLL
				| SWT.SINGLE | SWT.FULL_SELECTION);
		webxProjectTable.setHeaderVisible(true);
		webxProjectTable.setLinesVisible(true);
		TableLayout tableLayout = new TableLayout();

		TableColumn col = new TableColumn(webxProjectTable, SWT.NONE);
		col.setText("WebX Project Name");
		ColumnWeightData colData = new ColumnWeightData(8, 85, true);
		tableLayout.addColumnData(colData);

		TableColumn col2 = new TableColumn(webxProjectTable, SWT.NONE);
		col2.setText("Context Path");
		colData = new ColumnWeightData(13, 135, true);
		tableLayout.addColumnData(colData);

		webxProjectTable.setLayout(tableLayout);
		webxProjectTable.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				selectWebApp();
			}
		});

		// webxProjectTable.add

		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.widthHint = 450;
		data.heightHint = 120;
		webxProjectTable.setLayoutData(data);

		Composite rightPanel = toolkit.createComposite(composite);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		rightPanel.setLayout(layout);
		data = new GridData();
		rightPanel.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_BEGINNING));

		_addProjectButton = toolkit.createButton(rightPanel,
				"Add WebX Project...", SWT.PUSH);
		data = new GridData(GridData.FILL_HORIZONTAL);
		_addProjectButton.setLayoutData(data);
		_addProjectButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				WebXProjectDialog dialog = new WebXProjectDialog(
						getEditorSite().getShell(), getTmpWebxProjects());
				dialog.open();
				if (dialog.getReturnCode() == IDialogConstants.OK_ID
						&& !StringUtils.isBlank(dialog.getNewWebXProjectText())) {
					addWebXProject(dialog.getNewWebXProjectText());
					setDirty(true);
					initializeTable();
				}
			}
		});

		_editButton = toolkit.createButton(rightPanel, "Edit...", SWT.PUSH);
		data = new GridData(GridData.FILL_HORIZONTAL);
		_editButton.setLayoutData(data);
		_editButton.setEnabled(false);
		_editButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (_selection < 0)
					return;

				TableItem selectItem = webxProjectTable.getItem(_selection);
				String contextPath;
				String webxProjectName;
				try {
					webxProjectName = selectItem.getText(0);
					contextPath = selectItem.getText(1);
				} catch (Exception exception) {
					ServerPlugin.logError(exception);
					webxProjectName = selectItem.getText();
					contextPath = webxProjectName;
				}
				WebXProjectDialog dialog = new WebXProjectDialog(
						getEditorSite().getShell(), webxProjectName,
						contextPath);
				dialog.open();
				if (dialog.getReturnCode() == IDialogConstants.OK_ID
						&& !StringUtils.isBlank(dialog.getNewWebXProjectText())) {

//					String[] webxProjectNames = server.getWebXProjectNames();
//					webxProjectNames[_selection] = dialog
//							.getNewWebXProjectText();
//					webxProjects = Arrays.asList(webxProjectNames);
					// server.setWebXProjects(Arrays.asList(webxProjectNames));
					tmpWebxProjects.set(_selection, dialog.getNewWebXProjectText());
					setDirty(true);
					initializeTable();
				}

			}
		});

		_removeButton = toolkit.createButton(rightPanel, "Remove", SWT.PUSH);
		data = new GridData(GridData.FILL_HORIZONTAL);
		_removeButton.setLayoutData(data);
		_removeButton.setEnabled(false);
		_removeButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				if (_selection < 0)
					return;
				TableItem item = webxProjectTable.getItem(_selection);
				String webxProjectName = (String) item.getData();
//				webxProjects = ServerUtil.removeWebXProjectFromServer(server,
//						webxProjectName);
				removeWebXProject(webxProjectName);

				_removeButton.setEnabled(false);
				_editButton.setEnabled(false);
				_selection = -1;
				setDirty(true);
				initializeTable();
			}
		});

		form.setContent(section);
		form.reflow(true);

		initializeTable();
	}

	private void addWebXProject(String webxProject){
		getTmpWebxProjects().add(webxProject);
	}

	private List<String> getTmpWebxProjects(){
		if(tmpWebxProjects ==null){
			tmpWebxProjects = new ArrayList<String>();
			for(String webxProject:server.getAttribute(Server.WEBX_PROJECT_LIST,new ArrayList<String>())){
				tmpWebxProjects.add(webxProject);
			}
		}
		return tmpWebxProjects;
	}
	private void removeWebXProject(String webxProject){

		for (String name : getTmpWebxProjects()) {
			if (name.equals(webxProject)
					|| name.startsWith(webxProject + "::")){
				tmpWebxProjects.remove(name);
				return;
			}

		}
	}
	protected void initializeTable() {
		if (webxProjectTable == null)
			return;

		webxProjectTable.removeAll();

		for(String webxProjectName:getTmpWebxProjects()){
			TableItem item = new TableItem(webxProjectTable, SWT.NONE);
			String[] strs = webxProjectName.split(SPEA_CHAR);
			if (strs.length == 2) {
				item.setText(new String[] { strs[0],
						ServerUtil.formatContextPath(strs[1]) });
				item.setData(webxProjectName);

			} else {
				item.setText(new String[] { strs[0],
						ServerUtil.formatContextPathDefault(strs[0]) });
				item.setData(webxProjectName);
			}
		}
	}

	protected void createGeneralSection(Composite leftColumnComp,
			FormToolkit toolkit) {
		Section section = toolkit.createSection(leftColumnComp,
				ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText("General Information");
		// section.setDescription(Messages.serverEditorOverviewGeneralDescription);
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		int decorationWidth = FieldDecorationRegistry.getDefault()
				.getMaximumDecorationWidth();
		createLabel(toolkit, composite, "Server name:");
		serverName = toolkit.createText(composite, "");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		serverName.setLayoutData(data);
		serverName.setText(StringUtils.trimToEmpty(server.getName()));
		serverName.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// server.setName(serverName.getText());
				setDirty(true);
			}

		});

		createLabel(toolkit, composite, "Server port:");
		serverPort = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		serverPort.setLayoutData(data);
		serverPort.setText(StringUtils.trimToEmpty(server.getPort()));
		serverPort.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// server.setPort(serverPort.getText());
				setDirty(true);
			}

		});

		createLabel(toolkit, composite, "Host name:");
		host = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		host.setLayoutData(data);
		host.setText(StringUtils.trimToEmpty(server.getHost()));
		host.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				// server.setPort(serverPort.getText());
				setDirty(true);
			}

		});

		openBrowser = createLabeledCheck(
				"Open browser after server start",
				server.isOpenBrowser(), composite, toolkit);
		openBrowser.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				// server.setWithoutAutoconfig(withoutAutoconfig.getSelection());
				setDirty(true);

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		withoutAutoconfig = createLabeledCheck(
				"Server start without autoconfig",
				server.isWithoutAutoconfig(), composite, toolkit);
		withoutAutoconfig.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				// server.setWithoutAutoconfig(withoutAutoconfig.getSelection());
				setDirty(true);

			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});

		withoutReloadFuction = createLabeledCheck(
				"Server start without the Reload Function", server
						.isWithoutReloadfunction(), composite, toolkit);
		withoutReloadFuction.addSelectionListener(new SelectionListener() {

			public void widgetSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				// server.setWithoutReloadfunction(withoutReloadFuction.getSelection());
				setDirty(true);
			}

			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub

			}
		});
//		if()
//		leftColumnComp.s

		createLabel(toolkit, composite, "Request URI charset:");
		uriCharset = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		uriCharset.setLayoutData(data);
		uriCharset.setItems(Server.DEFAULT_URI_CHARSETS);
		uriCharset.setText(StringUtils.trimToEmpty(server.getUriCharset()));
		uriCharset.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});
	}

	private Button createLabeledCheck(String title, boolean value,
			Composite parent, FormToolkit toolkit) {
		Label label;
		Button button;
		if (toolkit == null) {
			label = new Label(parent, SWT.NONE);
			label.setText(title);
			button = new Button(parent, SWT.CHECK);
		} else {
			createFormLabel(title, parent, toolkit);
			button = toolkit.createButton(parent, null, SWT.CHECK);
		}

		GridData gridData = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
		gridData.horizontalSpan = 2;
		button.setLayoutData(gridData);
		button.setSelection(value);
		return button;
	}

	private static Label createFormLabel(String title, Composite parent,
			FormToolkit toolkit) {
		Label label;
		label = toolkit.createLabel(parent, title);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	@Override
	public void setFocus() {
		// TODO Auto-generated method stub

	}

	protected Label createLabel(FormToolkit toolkit, Composite parent,
			String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	protected void selectWebApp() {
		try {
			_selection = webxProjectTable.getSelectionIndex();
			_removeButton.setEnabled(true);
			_editButton.setEnabled(true);
		} catch (Exception e) {
			_selection = -1;
			_removeButton.setEnabled(false);
			_editButton.setEnabled(false);
		}
	}

	private void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		firePropertyChange(PROP_DIRTY);
	}

	protected void closeEditor() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getEditorSite().getPage().closeEditor(ServerEditor.this, false);
			}
		});
	}

	private LifecycleListener resourceListener;

	class LifecycleListener implements IServerLifecycleListener {
		public void serverAdded(Server oldServer) {
			// do nothing
		}

		public void serverChanged(Server oldServer) {
			// do nothing
		}

		public void serverRemoved(Server oldServer) {
			if (oldServer.equals(server))
				closeEditor();
		}
	}

	public void dispose() {
		if (resourceListener != null)
			ServerCore.removeServerLifecycleListener(resourceListener);
		tmpWebxProjects = null;
	}

}
