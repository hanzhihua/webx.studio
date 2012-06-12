/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-11
 * $Id: ProjectStructureContentProvider.java 108938 2011-08-30 15:01:06Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.pages;


import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

import webx.studio.projectcreation.ui.structure.BizStructureRootNode;
import webx.studio.projectcreation.ui.structure.BundleWarRootNode;
import webx.studio.projectcreation.ui.structure.ChildNode;
import webx.studio.projectcreation.ui.structure.CommonConfigRootNode;
import webx.studio.projectcreation.ui.structure.DalStructureRootNode;
import webx.studio.projectcreation.ui.structure.DeployRootNode;
import webx.studio.projectcreation.ui.structure.IStructureRootNode;
import webx.studio.projectcreation.ui.structure.WebStructureRootNode;


/**
 * TODO Comment of ProjectStructureContentProvider
 *
 * @author zhihua.hanzh
 */
public class ProjectStructureContentProvider implements IStructuredContentProvider,
        ITreeContentProvider {

    private IStructureRootNode webStructureRootNode;
    private IStructureRootNode bizStructureRootNode;
    private IStructureRootNode dalStructureRootNode;
    private IStructureRootNode bundleWarRootNode;
    private IStructureRootNode commonConfigRootNode;
    private IStructureRootNode deployRootNode;


    public void inputChanged(Viewer v, Object oldInput, Object newInput) {
    }

    public void dispose() {
    }

    public Object[] getElements(Object parent) {
        return getChildren(parent);
    }

    public Object getParent(Object child) {
    	if(child instanceof ChildNode){
    		return ((ChildNode)child).getParent();
    	}
        return null;
    }

    public boolean hasChildren(Object parent) {
        if (parent instanceof IStructureRootNode) {
            return ((IStructureRootNode) parent).hasChildren();
        }
        return false;
    }

    public Object[] getRootNodes() {
        if (webStructureRootNode == null) {
            webStructureRootNode = new WebStructureRootNode();
        }
        if(bizStructureRootNode == null){
            bizStructureRootNode = new BizStructureRootNode();
        }
        if(bundleWarRootNode == null){
            bundleWarRootNode = new BundleWarRootNode();
        }
        if(deployRootNode == null){
            deployRootNode = new DeployRootNode();
        }
        if(commonConfigRootNode == null){
            commonConfigRootNode = new CommonConfigRootNode();
        }
        if(dalStructureRootNode == null){
            dalStructureRootNode = new DalStructureRootNode();
        }
        return new Object[] { webStructureRootNode,bizStructureRootNode,dalStructureRootNode,commonConfigRootNode,bundleWarRootNode,deployRootNode };
    }

    public Object[] getChildren(Object parent) {
        if (parent == ProjectStructureWizardPage.root) {
            return getRootNodes();
        } else if (parent instanceof IStructureRootNode) {
        	IStructureRootNode node = (IStructureRootNode)parent;
            return node.getChildren();
        }
        return new Object[0];
    }

    public void reset(){
        for(Object obj:getRootNodes()){
            if(obj instanceof IStructureRootNode){
                ((IStructureRootNode)obj).reset();
            }
        }
    }

}
