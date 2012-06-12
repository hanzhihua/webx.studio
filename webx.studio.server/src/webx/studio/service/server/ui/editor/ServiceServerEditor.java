package webx.studio.service.server.ui.editor;

import java.util.Arrays;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.viewsupport.ProjectTemplateStore;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.help.IWorkbenchHelpSystem;
import org.eclipse.ui.part.EditorPart;

import webx.studio.ImageResource;
import webx.studio.server.ContextIds;
import webx.studio.server.core.Server;
import webx.studio.server.ui.cnf.ServerLabelProvider;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.ui.wizard.ProjectListFieldEditor;
import webx.studio.utils.PortUtil;

public class ServiceServerEditor extends EditorPart {

	private Text serviceServerName, confText, appNameText, appPortText,
			argText, vmArgText,projectNameText;
	private Combo appTypeCombo;
	private ProjectListFieldEditor plfe;
	private ServiceServer serviceServer;
	private StringBuffer sb = new StringBuffer();

	private boolean isDirty = false;

	public ServiceServerEditor() {
		// TODO Auto-generated constructor stub
	}


	public void doSave(IProgressMonitor monitor) {
		if (serviceServer.getServerState() != ServiceServer.STATE_STOPPED) {
			MessageDialog.openError(getEditorSite().getShell(), getPartName(),
					"The boort server must be stopped before you change .");
			return;
		}
		if (!validate()) {
			return;
		}
		if (serviceServer != null) {
			changeServiceServer();
			serviceServer.save();
		}
		setDirty(false);
		setPartName(serviceServer.getName());
	}

	private boolean validate() {
		String name = serviceServerName.getText();
		if (StringUtils.isBlank(name)) {
			MessageDialog.openError(getEditorSite().getShell(), getPartName(),
					"Please input a boort server name!");
			serviceServerName.forceFocus();
			return false;
		}
		String projectName = projectNameText.getText();
		if (StringUtils.isBlank(projectName)) {
			MessageDialog.openError(getEditorSite().getShell(), getPartName(),
					"Please choose one project!");
			projectNameText.forceFocus();
			return false;
		}
		String appName = appNameText.getText();
		if (StringUtils.isBlank(appName)) {
			MessageDialog.openError(getEditorSite().getShell(), getPartName(),
					"Please input a app name!");
			appNameText.forceFocus();
			return false;
		}

		String appPort = appPortText.getText();
		if (StringUtils.isBlank(appPort)) {
			MessageDialog.openError(getEditorSite().getShell(), getPartName(),
					"Please input a app name!");
			appPortText.forceFocus();
			return false;
		} else if (PortUtil.isInvalidatServiceProvide(appPort)) {
			MessageDialog.openError(getEditorSite().getShell(), getPartName(),
					"App port is invalidate!");
			appPortText.forceFocus();
			return false;
		}
		return true;
	}

	private void changeServiceServer() {
		serviceServer.setName(serviceServerName.getText());
		serviceServer.setConf(confText.getText());
		serviceServer.setArg(argText.getText());
		serviceServer.setVmArg(vmArgText.getText());
		serviceServer.setAppName(appNameText.getText());
		serviceServer.setAppPort(appPortText.getText());
		serviceServer.setAppType(appTypeCombo.getText());
		serviceServer.setServiceProjects(Arrays.asList(projectNameText.getText()));
	}


	public void doSaveAs() {
		// TODO Auto-generated method stub

	}


	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		if (input instanceof ServiceServerEditorInput) {
			ServiceServerEditorInput ssei = (ServiceServerEditorInput) input;
			this.serviceServer = ssei.getServiceServer();
		}

		if (this.serviceServer != null) {

			setPartName(this.serviceServer.getName());
			setTitleToolTip(this.serviceServer.getId());
		} else {
			setPartName("-");
		}

	}


	public void createPartControl(Composite parent) {
		ManagedForm managedForm = new ManagedForm(parent);
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
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(form.getBody(), ContextIds.SERVER_SERVICE_MODEL);
		whs.setHelp(managedForm.getForm().getContent(), ContextIds.SERVER_SERVICE_MODEL);

	}

	protected void createGeneralSection(Composite leftColumnComp,
			FormToolkit toolkit) {
		Section section = toolkit.createSection(leftColumnComp,
				ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText("General Information");
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
		createLabel(toolkit, composite, "Boort Server name:");
		serviceServerName = toolkit.createText(composite, "");
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		serviceServerName.setLayoutData(data);
		serviceServerName.setText(StringUtils.trimToEmpty(serviceServer
				.getName()));
		serviceServerName.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}

		});



		Composite container = toolkit.createComposite(composite);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 3;
		container.setLayoutData(data);
		plfe = new ProjectListFieldEditor("projectName", "Project:", container);
		projectNameText = plfe.getTextControl(container);
		projectNameText.setEditable(false);
		projectNameText.setText(StringUtils.trimToEmpty(serviceServer.getServiceProjects()[0]));
		projectNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});



		createLabel(toolkit, composite, "Spring config:");
		confText = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		confText.setLayoutData(data);
		confText.setText(StringUtils.trimToEmpty(serviceServer.getConf()));
		confText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				// server.setName(serverName.getText());
				setDirty(true);
			}

		});

		createLabel(toolkit, composite, "App Name:");
		appNameText = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		appNameText.setLayoutData(data);
		appNameText
				.setText(StringUtils.trimToEmpty(serviceServer.getAppName()));
		appNameText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}

		});

		createLabel(toolkit, composite, "App Port:");
		appPortText = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		appPortText.setLayoutData(data);
		appPortText
				.setText(StringUtils.trimToEmpty(serviceServer.getAppPort()));
		appPortText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}

		});

		createLabel(toolkit, composite, "App Type:");
		appTypeCombo = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		appTypeCombo.setLayoutData(data);
		appTypeCombo.setItems(new String[] { ServiceServer.DEFAULT_APP_TYPE });
		appTypeCombo
				.setText(StringUtils.trimToEmpty(serviceServer.getAppType()));
		appTypeCombo.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}

		});

		createLabel(toolkit, composite, "Program arguments:");
		argText = toolkit.createText(composite, "", SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		data.heightHint = 40;
		argText.setLayoutData(data);
		argText.setText(StringUtils.trimToEmpty(serviceServer.getArg()));
		argText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				// server.setName(serverName.getText());
				setDirty(true);
			}

		});

		createLabel(toolkit, composite, "VM arguments:");
		vmArgText = toolkit.createText(composite, "", SWT.MULTI | SWT.WRAP
				| SWT.BORDER | SWT.V_SCROLL);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		data.heightHint = 40;
		vmArgText.setLayoutData(data);
		vmArgText.setText(StringUtils.trimToEmpty(serviceServer.getVmArg()));
		vmArgText.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				// server.setName(serverName.getText());
				setDirty(true);
			}

		});
	}

	protected Label createLabel(FormToolkit toolkit, Composite parent,
			String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}


	public boolean isDirty() {
		// TODO Auto-generated method stub
		return this.isDirty;
	}


	public boolean isSaveAsAllowed() {
		// TODO Auto-generated method stub
		return false;
	}


	public void setFocus() {
		// TODO Auto-generated method stub

	}

	private void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		firePropertyChange(PROP_DIRTY);
	}

}
