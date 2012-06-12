package webx.studio.service.server.ui.wizard;

import java.io.File;
import java.util.Arrays;


import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.help.IWorkbenchHelpSystem;

import webx.studio.ImageResource;
import webx.studio.server.ContextIds;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerUtil;
import webx.studio.utils.PortUtil;

public class ServiceServerInformationWizardPage extends WizardPage {

	protected ServiceServerInformationWizardPage(String pageName) {
		super(pageName);
		this.setTitle("Define a boort server");
		this.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_NEW_SERVER_WIZ));
	}

	private Text nameText, confText, argText, vmArgText, appNameText,
			appPortText, projectNameText;
	private Combo appTypeCombo;
	private ProjectListFieldEditor plfe;
	private final ModifyListener modifyListener = new InternalModifyListener();

	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);

		initServerBasicInformation(container);
		initSelectOneProject(container);
		initProjectBasicInformation(container);
		IWorkbenchHelpSystem whs = PlatformUI.getWorkbench().getHelpSystem();
		whs.setHelp(container, ContextIds.SERVER_SERVICE_MODEL);
		setControl(container);
		setRelativeProject();
		validate();
		addModifyListener();

	}

	private void setRelativeProject() {
		if (StringUtils.isNotBlank(preconfigureProject)) {
			projectNameText.setText(preconfigureProject);
			nameText.setText(preconfigureProject + "-server");
		}
	}

	private void addModifyListener() {
		nameText.addModifyListener(modifyListener);
		projectNameText.addModifyListener(modifyListener);
		confText.addModifyListener(modifyListener);
		appNameText.addModifyListener(modifyListener);
		appPortText.addModifyListener(modifyListener);
		appTypeCombo.addModifyListener(modifyListener);
		argText.addModifyListener(modifyListener);
		vmArgText.addModifyListener(modifyListener);
	}

	void validate() {

		String name = nameText.getText();
		if (StringUtils.isBlank(name)) {
			setErrorMessage("Please input a boort server name!");
			nameText.forceFocus();
			return;
		}

		if (ServiceServerUtil.findServer(name) != null) {
			setErrorMessage("Duplicate boort server name exist!");
			nameText.forceFocus();
			return;
		}

		String projectName = projectNameText.getText();
		if (StringUtils.isBlank(projectName)) {
			setErrorMessage("Please select one project!");
			projectNameText.forceFocus();
			return;
		}

		String appName = appNameText.getText();
		if (StringUtils.isBlank(appName)) {
			setErrorMessage("Please input a app name!");
			appNameText.forceFocus();
			return;
		}

		String appPort = appPortText.getText();
		if (StringUtils.isBlank(appPort)) {
			setErrorMessage("Please input a app name!");
			appPortText.forceFocus();
			return;
		} else if (PortUtil.isInvalidatServiceProvide(appPort)) {
			setErrorMessage("App port is invalidate!");
			appPortText.forceFocus();
			return;
		}
		updateStatus(null);
		return;
	}

	private class InternalModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			validate();
		}
	}

	private void initSelectOneProject(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container
				.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
						| GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_FILL));
		layout.numColumns = 3;
		layout.verticalSpacing = 9;
		layout.horizontalSpacing = 50;
		layout.marginWidth = 5;
		container.setLayout(layout);
		plfe = new ProjectListFieldEditor("projectName", "Project:", container);
		projectNameText = plfe.getTextControl(container);
		projectNameText.setEditable(false);

		if (StringUtils.isNotBlank(preconfigureProject)) {
			plfe.setEnabled(false, container);
		}

	}

	private void initProjectBasicInformation(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container
				.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
						| GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_FILL));
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		layout.horizontalSpacing = 50;
		layout.marginWidth = 5;
		container.setLayout(layout);

		new Label(container, SWT.NONE).setText("Spring config:");
		confText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		confText.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL_HORIZONTAL, true, false));

		confText.setText(ServiceServer.DEFAULT_SPRING_CONFIG);

		new Label(container, SWT.NONE).setText("App Name:");
		appNameText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		appNameText.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL_HORIZONTAL, true, false));

		new Label(container, SWT.NONE).setText("App Port:");
		appPortText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		appPortText.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL_HORIZONTAL, true, false));
		appPortText.setText(ServiceServer.DEFAULT_APP_PORT + "");

		new Label(container, SWT.NONE).setText("App Type:");
		appTypeCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		appTypeCombo.setItems(new String[] { ServiceServer.DEFAULT_APP_TYPE });
		appTypeCombo.select(0);

		new Label(container, SWT.NONE).setText("Program arguments:");
		argText = new Text(container, SWT.MULTI | SWT.WRAP | SWT.BORDER
				| SWT.V_SCROLL);
		GridData data = new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
				true, false);
		data.heightHint = 40;
		argText.setLayoutData(data);

		new Label(container, SWT.NONE).setText("VM arguments:");
		vmArgText = new Text(container, SWT.MULTI | SWT.WRAP | SWT.BORDER
				| SWT.V_SCROLL);
		data = new GridData(GridData.FILL, GridData.FILL_HORIZONTAL, true,
				false);
		data.heightHint = 40;
		vmArgText.setLayoutData(data);

	}

	private void initServerBasicInformation(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		container
				.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
						| GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_FILL));
		layout.numColumns = 2;
		layout.verticalSpacing = 9;
		layout.horizontalSpacing = 50;
		layout.marginWidth = 5;
		container.setLayout(layout);

		new Label(container, SWT.NONE).setText("Boort server name:");
		nameText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		nameText.setLayoutData(new GridData(GridData.FILL,
				GridData.FILL_HORIZONTAL, true, false));
	}

	public void setErrorMessage(String message) {
		if (message != null) {
			super.setErrorMessage(message);
			setPageComplete(false);
		} else {
			super.setErrorMessage(null);
			setPageComplete(true);
		}
	}

	public final void updateStatus(String message) {
		setErrorMessage(message);
		if (message == null) {
			setPageComplete(true);
		} else {
			setPageComplete(false);
		}
	}

	public ServiceServer generateServiceServer() {
		ServiceServer serviceServer = new ServiceServer();
		serviceServer.setHome(ServiceServer.SERVICE_SERVER_BASE_PATH
				+ File.separator + nameText.getText());
		serviceServer.setName(nameText.getText());
		if (StringUtils.isBlank(confText.getText())) {
			serviceServer.setConf(ServiceServer.DEFAULT_SPRING_CONFIG);
		} else {
			serviceServer.setConf(confText.getText());
		}
		serviceServer.setArg(argText.getText());
		serviceServer.setVmArg(vmArgText.getText());
		serviceServer.setAppName(appNameText.getText());
		serviceServer.setAppPort(appPortText.getText());
		serviceServer.setAppType(appTypeCombo.getText());
		serviceServer.setServiceProjects(Arrays.asList(projectNameText
				.getText()));

		return serviceServer;
	}

	private String preconfigureProject;

	public void setProject(String projectName) {
		this.preconfigureProject = projectName;

	}

}
