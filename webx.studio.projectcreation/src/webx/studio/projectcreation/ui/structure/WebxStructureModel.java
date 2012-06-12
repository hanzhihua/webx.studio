/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-13
 * $Id: WebxStructureModel.java 138323 2012-01-09 07:13:26Z zhihua.hanzh $
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
import java.util.Iterator;
import java.util.List;

/**
 * TODO Comment of WebxStructureModel
 * @author zhihua.hanzh
 *
 */
public class WebxStructureModel implements Iterable<AbstractStructureNodeModel>{

    private final List<DalStructureNodeModel> dals = new ArrayList<DalStructureNodeModel>();
    private final List<BizStructureNodeModel> bizs = new ArrayList<BizStructureNodeModel>();
    private final List<BundleWarNodeModel> wars = new ArrayList<BundleWarNodeModel>();
    private final List<CommonConfigNodeModel> commons = new ArrayList<CommonConfigNodeModel>();
    private final List<DeployNodeModel> deploys = new ArrayList<DeployNodeModel>();
    private final List<WebStructureNodeModel> webs = new ArrayList<WebStructureNodeModel>();


    /**
     * @return the bizs
     */
    public List<BizStructureNodeModel> getBizs() {
        return bizs;
    }

    /**
     * @return the wars
     */
    public List<BundleWarNodeModel> getWars() {
        return wars;
    }

    /**
     * @return the commons
     */
    public List<CommonConfigNodeModel> getCommons() {
        return commons;
    }

    /**
     * @return the deploys
     */
    public List<DeployNodeModel> getDeploys() {
        return deploys;
    }

    /**
     * @return the webs
     */
    public List<WebStructureNodeModel> getWebs() {
        return webs;
    }

    public void addDalStructureNodeModel(DalStructureNodeModel dal){
        dals.add(dal);
    }

    public void addBizStructureNodeModel(BizStructureNodeModel biz){
        bizs.add(biz);
    }

    public void addBundleWarNodeModel(BundleWarNodeModel war){
        wars.add(war);
    }
    public void addCommonConfigNodeModel(CommonConfigNodeModel common){
        commons.add(common);
    }
    public void addDeployNodeModel(DeployNodeModel deploy){
        deploys.add(deploy);
    }
    public void addWebStructureNodeModel(WebStructureNodeModel web){
        webs.add(web);
    }

    public int getProjectSize(){
        return dals.size()+bizs.size()+wars.size()+commons.size()+deploys.size()+webs.size();
    }

    /**
     * @return the dals
     */
    public List<DalStructureNodeModel> getDals() {
        return dals;
    }

    /* (non-Javadoc)
     * @see java.lang.Iterable#iterator()
     */
    public Iterator<AbstractStructureNodeModel> iterator() {
        List<AbstractStructureNodeModel> list = new ArrayList<AbstractStructureNodeModel>();
        list.addAll(deploys);
        list.addAll(commons);
        list.addAll(dals);
        list.addAll(bizs);
        list.addAll(webs);
        list.addAll(wars);
        return list.iterator();
    }

    public List<? extends AbstractStructureNodeModel> getDependencyNodeMode(AbstractStructureNodeModel model){
        List<AbstractStructureNodeModel> returnList = new ArrayList<AbstractStructureNodeModel>();
        if(model instanceof DeployNodeModel || model instanceof CommonConfigNodeModel)
            return returnList;
        if(model instanceof DalStructureNodeModel){
            return commons;
        }else if(model instanceof BizStructureNodeModel){
            returnList.addAll(commons);
            returnList.addAll(dals);
        }else if(model instanceof WebStructureNodeModel){
            returnList.addAll(commons);
            returnList.addAll(dals);
            returnList.addAll(bizs);
        }else if(model instanceof BundleWarNodeModel){
            returnList.addAll(commons);
            returnList.addAll(bizs);
            returnList.addAll(webs);
        }
        return returnList;

    }

    public String[] getWebLayerName(){
        List<String> strs = new ArrayList<String>();
        for(WebStructureNodeModel web:webs){
            strs.add(web.getName());
        }
        return strs.toArray(new String[0]);
    }




}
