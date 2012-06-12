/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-15
 * $Id: AbstractStructureRootNode.java 108367 2011-08-26 09:28:47Z zhihua.hanzh $
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

import org.eclipse.swt.graphics.Image;

/**
 * TODO Comment of AbstractStructureRootNode
 *
 * @author zhihua.hanzh
 */
public abstract class AbstractStructureRootNode implements IStructureRootNode {

    protected List<ChildNode> children = new ArrayList<ChildNode>();

    public boolean hasChildren() {
        return true;
    }

    public Image getImage() {
        return null;
    }

    public AbstractStructureRootNode() {
        for (ChildNode str : getDefaultChildren()) {
            children.add(str);
        }
    }

    public void reset() {
        children.clear();
        for (ChildNode str : getDefaultChildren()) {
            children.add(str);
        }
    }

    public void addChild(ChildNode str) {
        if (canOperation(ADD_OPERATION))
            children.add(str);
    }

    public void removeChild(ChildNode str) {
        if (canOperation(REMOVE_OPERATION))
            children.remove(str);
    }

    public ChildNode[] getChildren() {
        return children.toArray(new ChildNode[0]);
    }

    protected abstract ChildNode[] getDefaultChildren();

    public boolean canOperation(String operation) {
        return true;
    }

    public boolean canEdit(){
    	return true;
    }

}
