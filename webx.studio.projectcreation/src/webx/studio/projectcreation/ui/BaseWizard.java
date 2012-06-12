/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-4-26
 * $Id: BaseWizard.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;

/**
 * TODO Comment of BaseWizard
 *
 * @author zhihua.hanzh
 */
public abstract class BaseWizard extends Wizard implements INewWizard {

    protected List<IWorkingSet>    workingSets = new ArrayList<IWorkingSet>();
    protected IStructuredSelection selection;

    public abstract BaseWizardPage[] getWizardPages();

    public final void addPages() {

        BaseWizardPage[] pages = getWizardPages();
        for (int i = 0; i < pages.length; i++) {
            addPage(pages[i]);
        }
    }

    public void init(IWorkbench workbench, IStructuredSelection selection) {
        this.selection = selection;
        IWorkingSet workingSet = SelectionUtil.getSelectedWorkingSet(selection);
        if (workingSet != null) {
            this.workingSets.add(workingSet);
        }
    }

}
