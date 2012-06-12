package webx.studio.service.server.ui.launchtab;

import java.text.MessageFormat;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import webx.studio.server.ServerPlugin;
import webx.studio.service.server.core.BoortConstants;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerUtil;
import webx.studio.utils.PortUtil;

public class ServiceServerTab extends JavaLaunchTab {

	private ServiceServer serviceServer;
	private Text nameText,projectNameText,confText,appNameText,appPortText,argText, vmArgText;
	private Combo appTypeCombo;

	private UpdateModfiyListener _updatedListener = new UpdateModfiyListener();
	private class UpdateModfiyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}
	}


	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		createServiceServer(container);
		setControl(container);

		return;

	}

	public void createServiceServer(Composite parent){
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

		new Label(container, SWT.NONE).setText("Service server name:");
		nameText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL |SWT.READ_ONLY);
		nameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
				true, false));
		nameText.setEnabled(false);


//		new Label(container, SWT.NONE).setText("Service server home:");
//		homeText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL |SWT.READ_ONLY);
//		homeText.setLayoutData(new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
//				true, false));
//		homeText.setEnabled(false);
//
//
//		new Label(container, SWT.NONE).setText("Service server version:");
//		versionText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL |SWT.READ_ONLY);
//		versionText.setLayoutData(new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
//				true, false));
//		versionText.setEnabled(false);

		new Label(container, SWT.NONE).setText("Project name:");
		projectNameText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL |SWT.READ_ONLY);
		projectNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
				true, false));
		projectNameText.setEnabled(false);

		new Label(container, SWT.NONE).setText("Spring config:");
		confText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL );
		confText.setLayoutData(new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
				true, false));
		confText.addModifyListener(_updatedListener);
//		confText.setEnabled(false);

		new Label(container, SWT.NONE).setText("App Name:");
		appNameText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		appNameText.setLayoutData(new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
				true, false));
		appNameText.addModifyListener(_updatedListener);
//		appNameText.setEnabled(false);

		new Label(container, SWT.NONE).setText("App port:");
		appPortText = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		appPortText.setLayoutData(new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
				true, false));
		appPortText.addModifyListener(_updatedListener);
//		appPortText.setEnabled(false);

		new Label(container, SWT.NONE).setText("App type:");
		appTypeCombo = new Combo(container, SWT.BORDER | SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan =1;
		appTypeCombo.setLayoutData(data);
		appTypeCombo.setItems(new String[]{ServiceServer.DEFAULT_APP_TYPE});
		appTypeCombo.select(0);
		appTypeCombo.addModifyListener(_updatedListener);
//		appTypeCombo.setEnabled(false);

		new Label(container, SWT.NONE).setText("Program arguments:");
		argText = new Text(container, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
				true, false);
		data.heightHint = 40;
		argText.setLayoutData(data);
		argText.addModifyListener(_updatedListener);

		new Label(container, SWT.NONE).setText("VM arguments:");
		vmArgText = new Text(container, SWT.MULTI | SWT.WRAP | SWT.BORDER | SWT.V_SCROLL);
		data = new GridData(GridData.FILL, GridData.FILL_HORIZONTAL,
				true, false);
		data.heightHint = 40;
		vmArgText.setLayoutData(data);
		vmArgText.addModifyListener(_updatedListener);




	}


	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		try {
			this.serviceServer = ServiceServerUtil.getServer(configuration);
			nameText.setText(StringUtils.trimToEmpty(this.serviceServer.getName()));
//			homeText.setText(StringUtils.trimToEmpty(this.serviceServer.getHome()));
//			versionText.setText(StringUtils.trimToEmpty(this.serviceServer.getVersion()));
			String[] projects = this.serviceServer.getServiceProjects();
			if(projects != null && projects.length == 1)
				projectNameText.setText(StringUtils.trimToEmpty(projects[0]));
			confText.setText(StringUtils.trimToEmpty(this.serviceServer.getConf()));
			appNameText.setText(StringUtils.trimToEmpty(serviceServer.getAppName()));
			appPortText.setText(StringUtils.trimToEmpty(serviceServer.getAppPort()));
			appTypeCombo.setText(StringUtils.trimToEmpty(serviceServer.getAppType()));
			argText.setText(StringUtils.trimToEmpty(serviceServer.getArg()));
			vmArgText.setText(StringUtils.trimToEmpty(serviceServer.getVmArg()));
		} catch (CoreException e) {
			ServerPlugin.logError(e);
		}

	}


	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		// TODO Auto-generated method stub
		configuration.setAttribute(BoortConstants.SPRING_CONFIG, StringUtils.trimToEmpty(confText.getText()));
		configuration.setAttribute(BoortConstants.APP_NAME, StringUtils.trimToEmpty(appNameText.getText()));
		configuration.setAttribute(BoortConstants.APP_PORT, StringUtils.trimToEmpty(appPortText.getText()));
		configuration.setAttribute(BoortConstants.APP_TYPE, StringUtils.trimToEmpty(appTypeCombo.getText()));
		configuration.setAttribute(BoortConstants.VM_ARG, StringUtils.trimToEmpty(vmArgText.getText()));
		configuration.setAttribute(BoortConstants.ARG, StringUtils.trimToEmpty(argText.getText()));
	}


	public String getName() {
		return "Service server";
	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			this.serviceServer = ServiceServerUtil.getServer(configuration);
			nameText.setText(StringUtils.trimToEmpty(this.serviceServer.getName()));
//			homeText.setText(StringUtils.trimToEmpty(this.serviceServer.getHome()));
//			versionText.setText(StringUtils.trimToEmpty(this.serviceServer.getVersion()));
			String[] projects = this.serviceServer.getServiceProjects();
			if(projects != null && projects.length == 1)
				projectNameText.setText(StringUtils.trimToEmpty(projects[0]));
			confText.setText(configuration.getAttribute(BoortConstants.SPRING_CONFIG, ""));
			appNameText.setText(configuration.getAttribute(BoortConstants.APP_NAME, ""));
			appPortText.setText(configuration.getAttribute(BoortConstants.APP_PORT, ""));
			appTypeCombo.setText(configuration.getAttribute(BoortConstants.APP_TYPE, ""));
			argText.setText(configuration.getAttribute(BoortConstants.ARG, ""));
			vmArgText.setText(configuration.getAttribute(BoortConstants.VM_ARG, ""));
		} catch (CoreException e) {
			ServerPlugin.logError(e);
		}
	}

	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		String confName = confText.getText();
		if (StringUtils.isBlank(confName)) {
			confText.forceFocus();
			setErrorMessage("Please input a spring config pattern!");
			return false;
		}

		String appName = appNameText.getText();
		if (StringUtils.isBlank(appName)) {
			appNameText.forceFocus();
			setErrorMessage("Please input a app name!");
			return false;
		}

		String appPort = appPortText.getText();
		if (StringUtils.isBlank(appPort)) {
			appPortText.forceFocus();
			setErrorMessage("Please input a app name!");
			return false;
		} else if (PortUtil.isInvalidatServiceProvide(appPort)) {
			appPortText.forceFocus();
			setErrorMessage("App port is invalidate!");
			return false;
		}
		return true;
	}

}
