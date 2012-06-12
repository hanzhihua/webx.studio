/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-10
 * $Id: ProjectMavenComponent.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.components;


import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import webx.studio.projectcreation.ui.GeneralUtils;
import webx.studio.projectcreation.ui.NameRule;
import webx.studio.projectcreation.ui.ProjectCreationConstants;


/**
 * TODO Comment of ProjectMavenComponent
 *
 * @author zhihua.hanzh
 */
public class ProjectMavenComponent extends Composite {

    protected Combo            groupIdCombo;
    protected Combo            artifactIdCombo;
    protected Combo            versionCombo;
    private ModifyListener     modifyingListener;
    private Label              groupIdlabel;
    private Label              artifactIdLabel;
    private Label              versionLabel;

    private Combo              parentArtifactIdCombo;
    private Combo              parentGroupIdCombo;
    private Combo              parentVersionCombo;

    private Label              parentGroupIdLabel;
    private Label              parentArtifactIdLabel;
    private Label              parentVersionLabel;

    public ProjectMavenComponent(Composite parent, int styles) {
        super(parent, styles);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.numColumns = 2;
        setLayout(layout);

        Group group = new Group(this, SWT.NONE);
        group.setText("Maven Information:");
        GridData gd_artifactGroup = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        group.setLayoutData(gd_artifactGroup);
        group.setLayout(new GridLayout(2, false));

        groupIdlabel = new Label(group, SWT.NONE);
        groupIdlabel.setText("Group Id:");
        groupIdCombo = new Combo(group, SWT.BORDER);
        groupIdCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true, false));
        groupIdCombo.setData("name", "groupIdCombo"); //$NON-NLS-1$ //$NON-NLS-2$

        artifactIdLabel = new Label(group, SWT.NONE);
        artifactIdLabel.setText("Artifact Id:");

        artifactIdCombo = new Combo(group, SWT.BORDER);
        artifactIdCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        artifactIdCombo.setData("name", "artifactIdCombo"); //$NON-NLS-1$ //$NON-NLS-2$

        versionLabel = new Label(group, SWT.NONE);
        versionLabel.setText("Version");

        versionCombo = new Combo(group, SWT.BORDER);
        versionCombo.setLayoutData(new GridData(150, SWT.DEFAULT));
        versionCombo.setText(ProjectCreationConstants.DEFAULT_VERSION);
        versionCombo.setData("name", "versionCombo"); //$NON-NLS-1$ //$NON-NLS-2$

        parentGroupIdLabel = new Label(group, SWT.NONE);
        parentGroupIdLabel.setText("Parent Group Id:");

        parentGroupIdCombo = new Combo(group, SWT.NONE);
        parentGroupIdCombo
                .setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false, false));
        parentGroupIdCombo.setData("name", "parentGroupIdCombo"); //$NON-NLS-1$ //$NON-NLS-2$

        parentArtifactIdLabel = new Label(group, SWT.NONE);
        parentArtifactIdLabel.setText("Parent Artifact Id:");

        parentArtifactIdCombo = new Combo(group, SWT.NONE);
        parentArtifactIdCombo.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false,
                false));
        parentArtifactIdCombo.setData("name", "parentArtifactIdCombo"); //$NON-NLS-1$ //$NON-NLS-2$

        parentVersionLabel = new Label(group, SWT.NONE);
        parentVersionLabel.setText("Parent Version:");

        parentVersionCombo = new Combo(group, SWT.NONE);
        parentVersionCombo.setLayoutData(new GridData(150, SWT.DEFAULT));
        parentVersionCombo.setData("name", "parentVersionCombo"); //$NON-NLS-1$ //$NON-NLS-2$

    }

    public void setModifyingListener(ModifyListener modifyingListener) {
        this.modifyingListener = modifyingListener;

        groupIdCombo.addModifyListener(modifyingListener);
        artifactIdCombo.addModifyListener(modifyingListener);
        versionCombo.addModifyListener(modifyingListener);

        parentGroupIdCombo.addModifyListener(modifyingListener);
        parentArtifactIdCombo.addModifyListener(modifyingListener);
        parentVersionCombo.addModifyListener(modifyingListener);

    }

    public void dispose() {
        super.dispose();

        if (modifyingListener != null) {
            groupIdCombo.removeModifyListener(modifyingListener);
            artifactIdCombo.removeModifyListener(modifyingListener);
            versionCombo.removeModifyListener(modifyingListener);

            parentArtifactIdCombo.removeModifyListener(modifyingListener);
            parentArtifactIdCombo.removeModifyListener(modifyingListener);
            parentVersionCombo.removeModifyListener(modifyingListener);
        }
    }

    /*
     * (non-Javadoc)
     * @see org.eclipse.swt.widgets.Composite#setFocus()
     */
    public boolean setFocus() {
        if (groupIdCombo != null) {
            return groupIdCombo.setFocus();
        }
        return super.setFocus();
    }

    public String getArtifactId() {
        return this.artifactIdCombo.getText().trim();
    }

    public String getGroupId() {
        return this.groupIdCombo.getText().trim();
    }

    public String getVersion() {
        return this.versionCombo.getText().trim();
    }

    public void setGroupId(String groupId) {
        this.groupIdCombo.setText(groupId);
    }

    public void setArtifactId(String artifact) {
        this.artifactIdCombo.setText(artifact);
    }

    public void setVersion(String version) {
        versionCombo.setText(version);
    }

    public Model getModel(NameRule nameRule) {
        Model model = new Model();
        model.setModelVersion("4.0.0"); //$NON-NLS-1$

        model.setGroupId(getGroupId());
        model.setArtifactId(nameRule.getParentArtifactId());
        model.setVersion(getVersion());
        model.setPackaging("pom");
        model.setName(nameRule.getProjectName());

        String parentGroupId = parentGroupIdCombo.getText().trim();
        if (parentGroupId.length() > 0) {
            Parent parent = new Parent();
            parent.setGroupId(parentGroupId);
            parent.setArtifactId(parentArtifactIdCombo.getText().trim());
            parent.setVersion(parentVersionCombo.getText().trim());
            model.setParent(parent);
        }
        return model;
    }


    public Combo getGroupIdCombo() {
        return groupIdCombo;
    }

    public Combo getArtifactIdCombo() {
        return artifactIdCombo;
    }

    public Combo getVersionCombo() {
        return versionCombo;
    }

    public void setWidthGroup(WidthGroup widthGroup) {
        widthGroup.addControl(this.groupIdlabel);
        widthGroup.addControl(this.artifactIdLabel);
        widthGroup.addControl(this.versionLabel);

        widthGroup.addControl(this.parentGroupIdLabel);
        widthGroup.addControl(this.parentArtifactIdLabel);
        widthGroup.addControl(this.parentVersionLabel);

    }

    /**
     * @return the parentArtifactIdCombo
     */
    public Combo getParentArtifactIdCombo() {
        return parentArtifactIdCombo;
    }

    /**
     * @return the parentGroupIdCombo
     */
    public Combo getParentGroupIdCombo() {
        return parentGroupIdCombo;
    }

    /**
     * @return the parentVersionCombo
     */
    public Combo getParentVersionCombo() {
        return parentVersionCombo;
    }

}
