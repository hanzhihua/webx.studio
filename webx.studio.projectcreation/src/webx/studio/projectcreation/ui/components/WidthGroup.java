/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-9
 * $Id: WidthGroup.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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

import java.util.HashSet;

import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Control;

/**
 * TODO Comment of WidthGroup
 * @author zhihua.hanzh
 *
 */
public class WidthGroup extends ControlAdapter {

    private final HashSet<Control> controls = new HashSet<Control>();

    public void controlResized(ControlEvent e) {
      int maxWidth = 0;
      for(Control c : this.controls) {
        int width = c.getSize().x;
        if(width > maxWidth) {
          maxWidth = width;
        }
      }
      if(maxWidth > 0) {
        for(Control c : this.controls) {
          GridData gd = (GridData) c.getLayoutData();
          gd.widthHint = maxWidth;
          c.getParent().layout();
        }
      }
    }

    public void addControl(Control control) {
      controls.add(control);
      control.getParent().layout();
    }

  }