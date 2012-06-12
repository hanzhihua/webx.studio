/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-13
 * $Id: BizStructureNodeModel.java 138323 2012-01-09 07:13:26Z zhihua.hanzh $
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

import java.util.ArrayList;
import java.util.List;

/**
 * TODO Comment of BizLayerModel
 * @author zhihua.hanzh
 *
 */
public class BizStructureNodeModel  extends  AbstractStructureNodeModel{

    public BizStructureNodeModel(String name){
        super(name);
    }

    public List<ProjectFolder> getProjectFolders() {
       List<ProjectFolder> pfList = new ArrayList<ProjectFolder>();
       pfList.add(JAVA);
       pfList.add(JAVA_TEST);
       pfList.add(RESOURCES);
       pfList.add(RESOURCES_TEST);
        return pfList;
    }

    public String getPomType() {
        return "jar";
    }

}