/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-4-27
 * $Id: IProjectType.java 105908 2011-08-15 02:32:24Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.project;

import java.util.Comparator;

/**
 * TODO Comment of IProjectType
 * @author zhihua.hanzh
 *
 */
public interface IProjectType{

    public String getTitle();

    public boolean isDefault();

    public String getId();

}
