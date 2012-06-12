/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-17
 * $Id: ModuleProjectInformation.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.project;

import java.io.File;

/**
 * TODO Comment of WebLayerProjectInformation
 * @author zhihua.hanzh
 *
 */
public class ModuleProjectInformation {

    /**
     * @return the inputName
     */
    public String getInputName() {
        return inputName;
    }
    /**
     * @param inputName the inputName to set
     */
    public void setInputName(String inputName) {
        this.inputName = inputName;
    }
    /**
     * @return the artfiactId
     */
    public String getArtfiactId() {
        return artfiactId;
    }
    /**
     * @param artfiactId the artfiactId to set
     */
    public void setArtfiactId(String artfiactId) {
        this.artfiactId = artfiactId;
    }
    /**
     * @return the projectDir
     */
    public File getProjectDir() {
        return projectDir;
    }
    /**
     * @param projectDir the projectDir to set
     */
    public void setProjectDir(File projectDir) {
        this.projectDir = projectDir;
    }
    String inputName;
    String artfiactId;
    File projectDir;
    String projectName;

	public String getProjectName() {
		return projectName;
	}

}
