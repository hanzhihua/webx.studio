/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: DeployRootNode.java 138323 2012-01-09 07:13:26Z zhihua.hanzh $
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


import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.swt.graphics.Image;

import webx.studio.projectcreation.ui.ProjectCreationConstants;


/**
 * TODO Comment of DeployNode
 * @author zhihua.hanzh
 *
 */
public class DeployRootNode extends AbstractStructureRootNode {

    public String getName() {
        return "Deploy";
    }

    public List<DeployNodeModel> getModelList(CheckboxTreeViewer viewer){
        List<DeployNodeModel> modelList = new ArrayList<DeployNodeModel>();
        ChildNode[] strs = (ChildNode[])getChildren();
        for(ChildNode str:strs){
        	if(!viewer.getChecked(str))
        		continue;
            DeployNodeModel model = new DeployNodeModel(str.getName());
            modelList.add(model);
        }
        return modelList;
    }

    @Override
    protected ChildNode[] getDefaultChildren() {
        return new ChildNode[]{new ChildNode(this,ProjectCreationConstants.DEFAULT_DEPLOY_VALUE)};
    }

    public boolean canOperation(String operation){
//        if(ADD_OPERATION.equals(operation) && children.size() >0)
//            return false;
////        if(REMOVE_OPERATION.equals(operation) && children.size() == 1)
////            return false;
//        return true;
        return false;
    }

    public boolean canEdit(){
    	return false;
    }

}