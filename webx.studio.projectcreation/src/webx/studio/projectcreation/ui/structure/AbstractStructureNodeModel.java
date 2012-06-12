/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-13
 * $Id: AbstractStructureNodeModel.java 108804 2011-08-30 05:27:35Z zhihua.hanzh $
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


import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Build;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.Parent;
import org.apache.maven.model.Resource;

import webx.studio.projectcreation.ui.NameRule;
import webx.studio.projectcreation.ui.project.MavenHelper;


/**
 * TODO Comment of AbstractStructureNodeModel
 *
 * @author zhihua.hanzh
 */
public abstract class AbstractStructureNodeModel implements IStructureNodeModel {

    public final static ProjectFolder JAVA           = new ProjectFolder("src/main/java", "target/classes");

    public final static ProjectFolder JAVA_TEST      = new ProjectFolder("src/test/java","target/test-classes");

    public final static ProjectFolder RESOURCES      = new ProjectFolder("src/main/resources","target/classes");

    public final static ProjectFolder RESOURCES_TEST = new ProjectFolder("src/test/resources","target/test-classes");

    public final static ProjectFolder WEBAPP         = new ProjectFolder("src/main/webapp", null);

    private final String name;
    private String       groupId;
    private String       artifactId;
    private String       version;

    public AbstractStructureNodeModel(String name) {
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public Dependency selfAsDependency() {
        Dependency dependency = new Dependency();
        dependency.setArtifactId(this.artifactId);
        dependency.setGroupId(this.groupId);
        return dependency;
    }

    public Dependency selfAsDependencyForParent(String groupId,NameRule nameRule) {
        if (!"jar".equalsIgnoreCase(getPomType()))
            return null;
        Dependency dependency = new Dependency();
        dependency.setArtifactId(nameRule.getModelArtifactId(this));
        dependency.setGroupId(groupId);
        dependency.setVersion("${pom.version}");
        return dependency;
    }

    public Model getModel(Model parentModel,NameRule nameRule) {
        Model model = new Model();
        model.setModelVersion("4.0.0"); //$NON-NLS-1$

        model.setGroupId(parentModel.getGroupId());
        model.setArtifactId(nameRule.getModelArtifactId(this));
        model.setVersion(parentModel.getVersion());
        model.setName(getName());
        model.setPackaging(getPomType());

        this.groupId = model.getGroupId();
        this.artifactId = model.getArtifactId();
        this.version = model.getVersion();

        Parent parent = new Parent();
        parent.setArtifactId(parentModel.getArtifactId());
        parent.setGroupId(parentModel.getGroupId());
        parent.setVersion(parentModel.getVersion());
        String relativePath = "../pom.xml";
        for (int i = 0; i < StringUtils.countMatches(name, "."); i++) {
            relativePath = "../" + relativePath;
        }
        parent.setRelativePath(relativePath);
        model.setParent(parent);

        if (this instanceof WebStructureNodeModel) {
            model.getDependencies().add(MavenHelper.genWebx3Core(null));
            model.getDependencies().add(MavenHelper.genWebx3Compat(null));
            model.getDependencies().add(MavenHelper.genWebx3Test(null));
//            model.getDependencies().add(MavenHelper.genWebx3Reload(null));
        }
        if(this instanceof BundleWarNodeModel){
            try{
            model.setBuild(MavenHelper.genBuildWithJetty());
            }catch(Exception e){
                //ignore
            }
        }
        if(this instanceof DeployNodeModel){

        	Resource resource = new Resource();
        	resource.setDirectory("htdocs");
        	resource.addInclude("favicon.ico");
        	Build build = new Build();
        	build.addResource(resource);
        	model.setBuild(build);

        }

        return model;

    }

}
