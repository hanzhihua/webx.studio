/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: StructureLabelProvider.java 138323 2012-01-09 07:13:26Z zhihua.hanzh $
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


import org.eclipse.jface.viewers.DelegatingStyledCellLabelProvider.IStyledLabelProvider;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.IFontProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.StyledString;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import webx.studio.ImageResource;

/**
 * TODO Comment of StructureLabelProvider
 *
 * @author zhihua.hanzh
 */
public class StructureLabelProvider extends LabelProvider implements IStyledLabelProvider,
        IColorProvider, IFontProvider {


    public String getText(Object obj) {
        if (obj instanceof IStructureRootNode) {
            return ((IStructureRootNode) obj).getName();
        }if(obj instanceof ChildNode){
        	return ((ChildNode)obj).getName();
        }
        return obj.toString();
    }

    public Image getImage(Object obj) {
        if (obj instanceof IStructureRootNode) {
            return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_PROJECT);
        }
//        return PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_OBJ_ELEMENT);
        return ImageResource.getImage(ImageResource.IMG_JEJU_PROJECT_LAYER);
    }

    public Font getFont(Object element) {
//        return italicFont;
        return null;
    }

    public Color getForeground(Object element) {

    	if (element instanceof DeployRootNode || element instanceof  BundleWarRootNode) {
			return Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
		}
    	if(element instanceof ChildNode){
    		Object parent = ((ChildNode)element).getParent();
    		if (parent!= null && (parent instanceof DeployRootNode || parent instanceof  BundleWarRootNode) ){
    			return Display.getDefault().getSystemColor(SWT.COLOR_GRAY);
    		}
    	}
		return null;
    }

    public Color getBackground(Object element) {
        return null;
    }

    public StyledString getStyledText(Object element) {
        return new StyledString(getText(element));
    }

}
