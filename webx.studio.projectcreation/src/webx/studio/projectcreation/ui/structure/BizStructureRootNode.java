/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: BizStructureRootNode.java 138323 2012-01-09 07:13:26Z zhihua.hanzh $
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

/**
 * TODO Comment of BizStructureNode
 * @author zhihua.hanzh
 *
 */
public class BizStructureRootNode extends AbstractStructureRootNode {

    public boolean hasChildren() {
        return true;
    }

    public Image getImage() {
        return null;
    }

    public String getName() {
        return "Biz Layer";
    }

    public List<BizStructureNodeModel> getModelList(CheckboxTreeViewer viewer){
        List<BizStructureNodeModel> modelList = new ArrayList<BizStructureNodeModel>();
        ChildNode[] strs = (ChildNode[])getChildren();
        for(ChildNode str:strs){
        	if(!viewer.getChecked(str))
        		continue;
            BizStructureNodeModel model = new BizStructureNodeModel(str.getName());
            modelList.add(model);
        }
        return modelList;
    }

    @Override
    protected ChildNode[] getDefaultChildren() {
        return new ChildNode[]{new ChildNode(this,"biz")};
    }

}