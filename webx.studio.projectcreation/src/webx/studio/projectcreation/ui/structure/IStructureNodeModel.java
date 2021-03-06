/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: IStructureNodeModel.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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

import java.util.List;

import org.apache.maven.model.Model;

/**
 * TODO Comment of StructureModel
 * @author zhihua.hanzh
 *
 */
public interface IStructureNodeModel {





    String getName();

    String getPomType();

    List<ProjectFolder> getProjectFolders();

}
