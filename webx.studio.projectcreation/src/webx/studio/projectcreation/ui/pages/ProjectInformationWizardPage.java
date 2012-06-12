/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-4-27
 * $Id: ProjectInformationWizardPage.java 132800 2011-12-16 06:17:24Z zhihua.hanzh $
 *
 * Copyright 1999-2100 Alibaba.com Corporation Limited.
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Alibaba Company. ("Confidential Information").  You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Alibaba.com.
 */
package webx.studio.projectcreation.ui.pages;

import hidden.edu.emory.mathcs.backport.java.util.Arrays;

import java.io.File;


import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import webx.studio.projectcreation.ui.BaseWizardPage;
import webx.studio.projectcreation.ui.HelpConstants;
import webx.studio.projectcreation.ui.NameRule;
import webx.studio.projectcreation.ui.ProjectCreationConstants;
import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.components.AdvancedComponent;
import webx.studio.projectcreation.ui.components.ProjectMavenComponent;
import webx.studio.projectcreation.ui.components.WebxComponent;
import webx.studio.projectcreation.ui.components.WidthGroup;
import webx.studio.projectcreation.ui.project.IProjectType;
import webx.studio.projectcreation.ui.project.MavenHelper;
import webx.studio.projectcreation.ui.project.WebxProjectType;


/**
 * TODO Comment of ProjectWizardPage
 *
 * @author zhihua.hanzh
 */
public class ProjectInformationWizardPage extends BaseWizardPage {

	private final static String PAGE_NAME = ProjectInformationWizardPage.class
			.getName();
	private final static String pageTitle = "Project Details";
	private IProjectType defaultProjectType = WebxProjectType.values()[0];
	private Group webxProjectTypeGroup;
	private Text pathName;
	private DirectoryFieldEditor dfe;
	private ProjectMavenComponent mavenComponent;
	private WebxComponent webxComponent;
	private AdvancedComponent advancedComponent;
	private final ModifyListener modifyListener = new InternalModifyListener();
	private final WidthGroup widthGroup = new WidthGroup();


	public ProjectInformationWizardPage() {
		super(PAGE_NAME);
		setTitle(pageTitle);
		setDescription("Create a new WebX project");
	}

	public String getPathValue() {
		return pathName.getText();
	}

	public String getProjectType() {

		if (webxProjectTypeGroup == null) {
			return null;
		}
		Control[] typeButtons = webxProjectTypeGroup.getChildren();
		if (typeButtons == null || typeButtons.length < 1) {
			return null;
		}
		for (Control button : typeButtons) {
			if (button instanceof Button) {
				Button selectButton = (Button) button;
				if (selectButton.getSelection()) {
					return selectButton.getText();
				}
			}
		}
		return defaultProjectType.getTitle();
	}

	public void internalCreateControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
//		initProjectType(container);
		initProjectPathAndName(container);
		initMavenAttribute(container);
		initWebxComponent(container);
		inttAdvancedComponent(container);
//		inttAdvancedComponent(container);
		loadInputHistory();
		loadDefaultParentMavenInformation();
		validate();
		container.layout();
		setControl(container);
		ProjectCreationPlugin.getHelpSystem().setHelp(
				getControl(), HelpConstants.PROJECT_INFO_WIZARD_HELP);
	}

	private void loadDefaultParentMavenInformation(){
		Combo combo = mavenComponent.getParentGroupIdCombo();
//		Collections combo.getItems())
		if(!Arrays.asList(combo.getItems()).contains("com.alibaba")){
			combo.add("com.alibaba");
			mavenComponent.getParentArtifactIdCombo().add("pampas");
			mavenComponent.getParentVersionCombo().add("4");
		}

	}

	private void inttAdvancedComponent(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(layout);
		container
		.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
				| GridData.HORIZONTAL_ALIGN_FILL
				| GridData.VERTICAL_ALIGN_FILL));
		advancedComponent = new AdvancedComponent(container, SWT.NONE);
		advancedComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		advancedComponent.setModifyingListener(modifyListener);
		new Label(container, SWT.NONE);
		addFieldWithHistory("settingFile", advancedComponent.getSettingFileCombo()); //$NON-NLS-1$
		container.layout();
	}

	private void initWebxComponent(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(layout);
		container
				.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
						| GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_FILL));

		webxComponent = new WebxComponent(container, SWT.NONE);
		webxComponent
				.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		webxComponent.setModifyingListener(modifyListener);
		addFieldWithHistory("webx.version", webxComponent.getWebxVersionCombo());
		container.layout();
	}

	private void initMavenAttribute(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(layout);
		container
				.setLayoutData(new GridData(GridData.GRAB_HORIZONTAL
						| GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_FILL));

		container.addControlListener(this.widthGroup);



		mavenComponent = new ProjectMavenComponent(container, SWT.NONE);
		mavenComponent.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
				true));
		mavenComponent.setWidthGroup(this.widthGroup);
		mavenComponent.setModifyingListener(modifyListener);

		addFieldWithHistory("groupId", mavenComponent.getGroupIdCombo());
		addFieldWithHistory("artifactId", mavenComponent.getArtifactIdCombo());
		addFieldWithHistory("version", mavenComponent.getVersionCombo());

		addFieldWithHistory("parentGroupId",
				mavenComponent.getParentGroupIdCombo());
		addFieldWithHistory("parentArtifactId",
				mavenComponent.getParentArtifactIdCombo());
		addFieldWithHistory("parentVersion",
				mavenComponent.getParentVersionCombo());

		container.layout();
	}

	private void initProjectType(Composite parent) {

		webxProjectTypeGroup = new Group(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		if (null == WebxProjectType.values()) {
			return;
		}
		layout.numColumns = WebxProjectType.values().length;
		layout.verticalSpacing = 9;
		layout.horizontalSpacing = 50;
		layout.marginWidth = 70;
		webxProjectTypeGroup.setLayout(layout);
		webxProjectTypeGroup.setLayoutData(new GridData(
				GridData.GRAB_HORIZONTAL | GridData.HORIZONTAL_ALIGN_FILL
						| GridData.VERTICAL_ALIGN_FILL));
		webxProjectTypeGroup.setText("Project Type");

		for (WebxProjectType type : WebxProjectType.values()) {
			Button selectButton = new Button(webxProjectTypeGroup, SWT.RADIO);
			selectButton.setText(type.getTitle());

			if (type.isDefault()) {
				defaultProjectType = type;
				selectButton.setSelection(true);
			} else {
				selectButton.setSelection(false);
				selectButton.setEnabled(false);
			}
		}

	}

	private void initProjectPathAndName(Composite parent) {
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
		dfe = new DirectoryFieldEditor("path", "Path", container);
		pathName = dfe.getTextControl(container);
		pathName.addModifyListener(modifyListener);
	}


	private class InternalModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			validate();
		}
	}

	void validate() {

		String path = pathName.getText();
		String error = null;
		if ((!new File(path).exists()) || (!new File(path).isDirectory())) {
			error = "Destination directory: " + path + " not exists.";
			updateStatus(error);
			return;
		}
		error = validateGroupIdInput(mavenComponent.getGroupId().trim());
		if (StringUtils.isNotBlank(error)) {
			updateStatus(error);
			return;
		}
		error = validateArtifactIdInput(mavenComponent.getArtifactId().trim());
		if (StringUtils.isNotBlank(error)) {
			updateStatus(error);
			return;
		}
		if (new File(path, mavenComponent.getArtifactId().trim()).exists()) {
			error = new File(path, mavenComponent.getArtifactId().trim())
					.getAbsolutePath() + " already exists.";
			updateStatus(error);
			return;
		}

		if (StringUtils.isBlank(mavenComponent.getVersion())) {
			error = "Enter a version for the artifact.";
			updateStatus(error);
			return;
		}

		{
			int parentCheck = 0;
			if (mavenComponent.getParentGroupIdCombo().getText().trim()
					.length() > 0) {
				parentCheck++;
			}
			if (mavenComponent.getParentArtifactIdCombo().getText().trim()
					.length() > 0) {
				parentCheck++;
			}
			if (mavenComponent.getParentVersionCombo().getText().trim()
					.length() > 0) {
				parentCheck++;
			}
			if (!(parentCheck == 0 || parentCheck == 3)) {
				error = "Enter a version for the artifact.";
				updateStatus("To specify a parent project, set the parent group id, artifact id and version");
				return;
			}
		}

		if(!validateFile(advancedComponent.getSettingFile())){
			error = "Invalid setting file.";
			updateStatus("Please input a correct setting file or remained blank");
			return;
		}

		updateStatus(null);
		return;
	}

	public Model getModel(NameRule nameRule) {
		Model model = mavenComponent.getModel(nameRule);
		model.addProperty("webx3-version", webxComponent.getWebxVersionCombo()
				.getText());
		model.addProperty("jetty-version",
				ProjectCreationConstants.DEFAULT_JETTY_VERSION);
		model.setDependencyManagement(new DependencyManagement());
		model.getDependencyManagement().addDependency(
				MavenHelper.genWebx3Compat("${webx3-version}"));
		model.getDependencyManagement().addDependency(
				MavenHelper.genWebx3Core("${webx3-version}"));
		model.getDependencyManagement().addDependency(
				MavenHelper.genWebx3Test("${webx3-version}"));
		try {
			model.setBuild(MavenHelper.genBuildWithEclipsePluginForParentPom());
		} catch (Exception e) {
			// ignore
		}
		return model;
	}

	public String getProjectName() {
		return mavenComponent.getArtifactId();
	}

	public String getProjectGroupId() {
		return mavenComponent.getGroupId();
	}

	public String getWebxVersion(){
		return webxComponent.getWebxVersionCombo().getText();
	}

	public String getSeetingFilePath(){
		return advancedComponent.getSettingFile();
	}

	public String getAntxPropertiesPath(){
		return advancedComponent.getAntPropertiesFile();
	}

	public String getAutoconfigCharset(){
		return  advancedComponent.getAutoConfigCharset();
	}
}
