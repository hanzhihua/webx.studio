/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: ProjectFolder.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.structure;

/**
 * TODO Comment of ProjectFolder
 *
 * @author zhihua.hanzh
 */
public class ProjectFolder {



    /** Folder path */
    private String                     path           = null;

    /** Output path */
    private String                     outputPath     = null;

    public ProjectFolder(String path, String outputPath) {
        this.path = path;
        this.outputPath = outputPath;
    }

    /**
     * Returns folder path
     */
    public String getPath() {
        return path;
    }

    /**
     * Returns folder output path
     */
    public String getOutputPath() {
        return outputPath;
    }

    /**
     * Returns true for source folder
     */
    public boolean isSourceEntry() {
        return this.getOutputPath() != null;
    }

}
