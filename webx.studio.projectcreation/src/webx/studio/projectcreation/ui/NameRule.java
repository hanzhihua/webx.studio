/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-16
 * $Id: NameRule.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui;

import webx.studio.projectcreation.ui.structure.AbstractStructureNodeModel;
import webx.studio.projectcreation.ui.structure.BizStructureNodeModel;
import webx.studio.projectcreation.ui.structure.BundleWarNodeModel;
import webx.studio.projectcreation.ui.structure.CommonConfigNodeModel;
import webx.studio.projectcreation.ui.structure.DalStructureNodeModel;
import webx.studio.projectcreation.ui.structure.DeployNodeModel;
import webx.studio.projectcreation.ui.structure.WebStructureNodeModel;

/**
 * TODO Comment of NameRule
 *
 * @author zhihua.hanzh
 */
public class NameRule {

    /**
     * @return the projectName
     */
    public String getProjectName() {
        return projectName;
    }

    private final String        projectName;
    private final String groupId;
    private final static String PARENT_ARTIFACT_ID_SUFFER = ".all";

    public NameRule(String groupId,String projectName) {
        this.groupId = groupId;
        this.projectName = projectName;
    }

    public String getParentArtifactId() {
        return projectName + PARENT_ARTIFACT_ID_SUFFER;
    }

    public String getWebLayerArtifactId(String name) {
        if (name.startsWith("web")) {
            return projectName + "."+name;
        }
        return projectName + ".web." + name;
    }

    public String getBizLayerArtifactId(String name) {
        if (name.startsWith("biz")) {
            return projectName + "."+ name;
        }
        return projectName + ".biz." + name;
    }

    public String getDalLayerArtifactId(String name) {
        if (name.startsWith("dal")) {
            return projectName + "."+ name;
        }
        return projectName + ".dal." + name;
    }

    public String getCommonArtifactId(String name) {
        if (name.startsWith("common")) {
            return projectName + "."+ name;
        }
        return projectName + ".common." + name;
    }

    public String getBundleWarArtifactId(String name) {
        return projectName + ".bundle.war";
    }

    public String getDeployArtifactId(String name) {
        return projectName + ".deploy";
    }

    public String getModelArtifactId(AbstractStructureNodeModel model) {

        if (model instanceof DeployNodeModel) {
            return getDeployArtifactId(model.getName());
        } else if (model instanceof CommonConfigNodeModel) {
            return getCommonArtifactId(model.getName());
        } else if (model instanceof DalStructureNodeModel) {
            return getDalLayerArtifactId(model.getName());
        } else if (model instanceof BizStructureNodeModel) {
            return getBizLayerArtifactId(model.getName());
        } else if (model instanceof WebStructureNodeModel) {
            return getWebLayerArtifactId(model.getName());
        } else if (model instanceof BundleWarNodeModel) {
            return getBundleWarArtifactId(model.getName());
        }
        return "errorId";
    }

    public String getPackagePrefix(){
        return groupId+"."+projectName+".";
    }




}
