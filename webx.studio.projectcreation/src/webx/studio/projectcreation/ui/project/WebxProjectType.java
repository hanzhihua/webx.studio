/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-4-27
 * $Id: WebxProjectType.java 138323 2012-01-09 07:13:26Z zhihua.hanzh $
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


/**
 * TODO Comment of Webx3ProjectType
 *
 * @author zhihua.hanzh
 */
public enum WebxProjectType implements IProjectType {

//    WEBX3("generic","Generic",true),WEBX25("chiness", "жпнду╬", false);
	WEBX3("generic","Generic",true);

    private String  id;

    private String  title;

    private boolean isDefault;

    WebxProjectType(String id, String title, boolean isDefault) {
        this.id = id;
        this.title = title;
        this.isDefault = isDefault;
     }

    public String getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public boolean isDefault() {
        return this.isDefault;
    }

}
