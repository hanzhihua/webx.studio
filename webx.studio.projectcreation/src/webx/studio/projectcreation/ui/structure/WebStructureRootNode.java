/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: WebStructureRootNode.java 142213 2012-02-04 12:54:48Z zhihua.hanzh $
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


/**
 * TODO Comment of WebStructureNode
 *
 * @author zhihua.hanzh
 */
public class WebStructureRootNode extends AbstractStructureRootNode {

    public String getName() {
        return "Web Layer";
    }

    public List<WebStructureNodeModel> getModelList(CheckboxTreeViewer viewer){
        List<WebStructureNodeModel> modelList = new ArrayList<WebStructureNodeModel>();
        ChildNode[] strs = (ChildNode[])getChildren();
        for(ChildNode str:strs){
        	if(!viewer.getChecked(str))
        		continue;
            WebStructureNodeModel model = new WebStructureNodeModel(str.getName());
            modelList.add(model);
        }
        return modelList;
    }

    
    protected ChildNode[] getDefaultChildren() {
        return new ChildNode[]{new ChildNode(this,"app")};
    }


}
