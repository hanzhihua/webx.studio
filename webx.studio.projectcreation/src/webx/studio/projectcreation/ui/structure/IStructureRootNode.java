/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: IStructureRootNode.java 108367 2011-08-26 09:28:47Z zhihua.hanzh $
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

import org.eclipse.swt.graphics.Image;

/**
 * TODO Comment of IStructureNode
 * @author zhihua.hanzh
 *
 */
public interface IStructureRootNode {

    public ChildNode[] getChildren();
    public String getName();
    public Image getImage();
    public boolean hasChildren();
    public void reset();
    public void addChild(ChildNode str);
    public void removeChild(ChildNode str);
    public boolean canOperation(String operation);
    public boolean canEdit();

    String ADD_OPERATION = "add";
    String REMOVE_OPERATION = "remove";

}
