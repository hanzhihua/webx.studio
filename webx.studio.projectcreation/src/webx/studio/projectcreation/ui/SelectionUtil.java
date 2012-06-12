/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-12
 * $Id: SelectionUtil.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

/**
 * TODO Comment of SelectionUtil
 *
 * @author zhihua.hanzh
 */
public class SelectionUtil {

    public static IWorkingSet getSelectedWorkingSet(IStructuredSelection selection) {
        Object element = selection == null ? null : selection.getFirstElement();
        if (element == null) {
            return null;
        }

        IWorkingSet workingSet = getType(element, IWorkingSet.class);
        if (workingSet != null) {
            return workingSet;
        }

        IResource resource = getType(element, IResource.class);
        if (resource != null) {
            return getWorkingSet(resource.getProject());
        }

        return null;

    }

    public static <T> T getType(Object element, Class<T> type) {
        if (element == null) {
            return null;
        }
        if (type.isInstance(element)) {
            return (T) element;
        }
        if (element instanceof IAdaptable) {
            T adapter = (T) ((IAdaptable) element).getAdapter(type);
            if (adapter != null) {
                return adapter;
            }
        }
        return (T) Platform.getAdapterManager().getAdapter(element, type);
    }

    public static IWorkingSet getWorkingSet(Object element) {
        IWorkingSetManager workingSetManager = PlatformUI.getWorkbench().getWorkingSetManager();
        for (IWorkingSet workingSet : workingSetManager.getWorkingSets()) {
            for (IAdaptable adaptable : workingSet.getElements()) {
                if (adaptable.getAdapter(IResource.class) == element) {
                    return workingSet;
                }
            }
        }
        return null;
    }

}
