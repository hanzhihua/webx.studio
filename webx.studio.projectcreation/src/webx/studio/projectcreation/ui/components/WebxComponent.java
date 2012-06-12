/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-10
 * $Id: WebxComponent.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.components;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

/**
 * TODO Comment of WebxComponent
 *
 * @author zhihua.hanzh
 */
public class WebxComponent extends Composite {

    public static final String DEFAULT_VERSION = "3.0.6"; //$NON-NLS-1$
    protected Combo            webxVersionCombo;
    private ModifyListener     modifyingListener;
    private Label              webxVersionLabel;

    public WebxComponent(Composite parent, int styles) {
        super(parent, styles);

        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        layout.numColumns = 2;
        setLayout(layout);

        Group group = new Group(this, SWT.NONE);
        group.setText("Webx");
        GridData gd_artifactGroup = new GridData(SWT.FILL, SWT.FILL, true, true, 3, 1);
        group.setLayoutData(gd_artifactGroup);
        group.setLayout(new GridLayout(2, false));

        webxVersionLabel = new Label(group, SWT.NONE);
        webxVersionLabel.setText("Version");

        webxVersionCombo = new Combo(group, SWT.BORDER);
        webxVersionCombo.setLayoutData(new GridData(150, SWT.DEFAULT));
        webxVersionCombo.setText(DEFAULT_VERSION);
        webxVersionCombo.setData("name", "versionCombo"); //$NON-NLS-1$ //$NON-NLS-2$
    }

    public void setModifyingListener(ModifyListener modifyingListener) {
        this.modifyingListener = modifyingListener;

        webxVersionCombo.addModifyListener(modifyingListener);
    }

    public void dispose() {
        super.dispose();

        if (modifyingListener != null) {
            webxVersionCombo.removeModifyListener(modifyingListener);

        }
    }

    /**
     * @return the webxVersionCombo
     */
    public Combo getWebxVersionCombo() {
        return webxVersionCombo;
    }

}
