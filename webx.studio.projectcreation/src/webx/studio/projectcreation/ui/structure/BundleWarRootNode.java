/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: BundleWarRootNode.java 142213 2012-02-04 12:54:48Z zhihua.hanzh $
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
 * TODO Comment of BundleWarNode
 * @author zhihua.hanzh
 *
 */
public class BundleWarRootNode extends AbstractStructureRootNode {

    public String getName() {
        return "Bundle";
    }

    public List<BundleWarNodeModel> getModelList(CheckboxTreeViewer viewer){
        List<BundleWarNodeModel> modelList = new ArrayList<BundleWarNodeModel>();
        ChildNode[] strs = (ChildNode[])getChildren();
        for(ChildNode str:strs){
        	if(!viewer.getChecked(str))
        		continue;
            BundleWarNodeModel model = new BundleWarNodeModel(str.getName());
            modelList.add(model);
        }
        return modelList;
    }

    /* (non-Javadoc)
     * @see com.alibaba.platform.webx3.projectcreation.ui.structure.AbstractStructureRootNode#getDefaultChildren()
     */
    
    protected ChildNode[] getDefaultChildren() {
        return new ChildNode[]{new ChildNode(this,ProjectCreationConstants.DEFAULT_BUNDLE_WAR_VALUE)};
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