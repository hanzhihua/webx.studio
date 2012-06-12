/**
 * Project: webx3.projectcreation
 *
 * File Created at 2011-7-11
 * $Id: ProjectStructureWizardPage.java 142213 2012-02-04 12:54:48Z zhihua.hanzh $
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
package webx.studio.projectcreation.ui.pages;

import java.util.ArrayList;
import java.util.List;


import org.apache.commons.lang.StringUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTreeViewer;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.actions.BaseSelectionListenerAction;

import webx.studio.projectcreation.ui.BaseWizardPage;
import webx.studio.projectcreation.ui.GeneralUtils;
import webx.studio.projectcreation.ui.NameRule;
import webx.studio.projectcreation.ui.ProjectCreationConstants;
import webx.studio.projectcreation.ui.ProjectCreationException;
import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.structure.AbstractStructureNodeModel;
import webx.studio.projectcreation.ui.structure.AbstractStructureRootNode;
import webx.studio.projectcreation.ui.structure.BizStructureRootNode;
import webx.studio.projectcreation.ui.structure.BundleWarRootNode;
import webx.studio.projectcreation.ui.structure.ChildNode;
import webx.studio.projectcreation.ui.structure.CommonConfigRootNode;
import webx.studio.projectcreation.ui.structure.DalStructureRootNode;
import webx.studio.projectcreation.ui.structure.DeployRootNode;
import webx.studio.projectcreation.ui.structure.IStructureRootNode;
import webx.studio.projectcreation.ui.structure.StructureLabelProvider;
import webx.studio.projectcreation.ui.structure.WebStructureRootNode;
import webx.studio.projectcreation.ui.structure.WebxStructureModel;

/**
 * TODO Comment of ProjectStructureWizardPage
 *
 * @author zhihua.hanzh
 */
public class ProjectStructureWizardPage extends BaseWizardPage {

	private final static String PAGE_NAME = ProjectStructureWizardPage.class
			.getName();
	private final static String pageTitle = "Project Structure Details";
	private static final String MENU_ID = ".structureViewMenu";

	public final static Object root = new Object();

	private IAction collapseAllAction;
	private IAction resetAction;
	private BaseSelectionListenerAction addAction;
	private BaseSelectionListenerAction deleteAction;

	/**
	 * @param pageName
	 */
	public ProjectStructureWizardPage() {
		super(PAGE_NAME);
		setTitle(pageTitle);
	}

	private CheckboxTreeViewer viewer;
	private ProjectStructureContentProvider contentProvider;

	void validate() {
		String error = null;
		List<String> nameList = new ArrayList<String>();
		WebxStructureModel webxStructureModel = getWebxStructureModel();
		if(webxStructureModel.getWebs() == null || webxStructureModel.getWebs().size() == 0){
			error = "One web app must at least exists!";
			updateStatus(error);
			return;
		}
		for (AbstractStructureNodeModel nodeModel : webxStructureModel) {
			if (nameList.contains(nodeModel.getName())) {
				error = "There are duplicate name [" + nodeModel.getName()
						+ "]";
				break;
			}
			nameList.add(nodeModel.getName());
		}
		updateStatus(error);
		return;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see com.alibaba.platform.webx3.projectcreation.ui.BaseWizardPage#
	 * internalCreateControl(org.eclipse.swt.widgets.Composite)
	 */
	
	public void internalCreateControl(Composite parent)
			throws ProjectCreationException {
		Composite container = new Composite(parent, SWT.NULL);

		GridLayout gl = new GridLayout(1, true);
		container.setLayout(gl);

		GridData gd;
		gd = new GridData(GridData.FILL_BOTH);
		gd.horizontalSpan = 1;
		viewer = new CheckboxTreeViewer(container, SWT.BORDER | SWT.H_SCROLL
				| SWT.V_SCROLL);

		contentProvider = new ProjectStructureContentProvider();
		viewer.setContentProvider(contentProvider);

		StructureLabelProvider labelProvider = new StructureLabelProvider();
		viewer.setLabelProvider(labelProvider);

		viewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {

			}
		});
		viewer.setInput(root);
		viewer.getTree().setLayoutData(gd);
		viewer.expandToLevel(2);
		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
//				if(!event.getChecked() && event.getElement() instanceof WebStructureRootNode){
//					updateStatus("One web app must at least exists!");
//					return;
//				}
				validate();


			}
		});
		initTreeView();
		makeActions();
		hookContextMenu();
		viewer.addSelectionChangedListener(addAction);
		viewer.addSelectionChangedListener(deleteAction);
		setControl(container);
		validate();
	}

	private void initTreeView() {
		for (Object obj : contentProvider.getRootNodes()) {
			viewer.setSubtreeChecked(obj, true);
		}
		viewer.addCheckStateListener(new ProjectStructureItemCheckListener(
				viewer));
		viewer.addCheckStateListener(new ICheckStateListener() {

			public void checkStateChanged(CheckStateChangedEvent event) {
				validate();
			}
		});
	}

	void fillContextMenu(IMenuManager manager) {

		manager.add(new Separator());
		manager.add(collapseAllAction);
		manager.add(new Separator());
		manager.add(resetAction);
		manager.add(new Separator());
		manager.add(addAction);
		manager.add(deleteAction);

	}

	private void hookContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu-" + MENU_ID); //$NON-NLS-1$
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {
			public void menuAboutToShow(IMenuManager manager) {
				ProjectStructureWizardPage.this.fillContextMenu(manager);
				manager.update();
			}
		});

		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
	}

	private void makeActions() {
		collapseAllAction = new Action("Collapse All") {
			public void run() {
				viewer.collapseAll();
			}
		};
		collapseAllAction.setToolTipText("Collapse All");
		collapseAllAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));
		resetAction = new Action("Reset") {
			public void run() {
				contentProvider.reset();
				viewer.refresh();
				for (Object obj : contentProvider.getRootNodes()) {
					viewer.setSubtreeChecked(obj, true);
				}
			}
		};
		collapseAllAction.setToolTipText("Collapse All");
		collapseAllAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_COLLAPSEALL));

		addAction = new BaseSelectionListenerAction("Add Itme") {
			public void run() {
				TreeSelection ts = (TreeSelection) viewer.getSelection();
				org.eclipse.jface.viewers.TreePath tp = ts.getPaths()[0];
				Object obj = tp.getFirstSegment();
				if (obj instanceof AbstractStructureRootNode) {
					AbstractStructureRootNode node = (AbstractStructureRootNode) obj;
					InputDialog dlg = null;
					if (node instanceof BundleWarRootNode) {
						dlg = new InputDialog(
								Display.getCurrent().getActiveShell(),
								"",
								"Enter Item name",
								ProjectCreationConstants.DEFAULT_BUNDLE_WAR_VALUE,
								null) {
							protected int getInputTextStyle() {
								return SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY;
							}
						};
					} else if (node instanceof DeployRootNode) {
						dlg = new InputDialog(Display.getCurrent()
								.getActiveShell(), "", "Enter Item name",
								ProjectCreationConstants.DEFAULT_DEPLOY_VALUE,
								null) {
							protected int getInputTextStyle() {
								return SWT.SINGLE | SWT.BORDER | SWT.READ_ONLY;
							}
						};
					} else {
						dlg = new InputDialog(Display.getCurrent()
								.getActiveShell(), "", "Enter Item name", "",
								null);
					}
					if (dlg.open() == Window.OK) {
						String itemName = dlg.getValue().toString();
						if (StringUtils.isNotBlank(itemName)) {
							AbstractStructureRootNode rootNode = (AbstractStructureRootNode) tp
									.getFirstSegment();
							ChildNode child = new ChildNode(rootNode, itemName);
							rootNode.addChild(child);
							viewer.refresh();
							viewer.setChecked(child, true);
							this.setEnabled(updateSelection(ts));
						}
					}
					calcCheckBox(viewer, node);
				}

				validate();

			}

			protected boolean updateSelection(IStructuredSelection selection) {
				Object obj = selection.getFirstElement();
				if (obj == null)
					return false;
				if (obj instanceof IStructureRootNode) {
					return ((IStructureRootNode) obj)
							.canOperation(IStructureRootNode.ADD_OPERATION);
				}
				return false;
			}
		};
		addAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_OBJ_ADD));

		deleteAction = new BaseSelectionListenerAction("Delete Item") {
			public void run() {
				TreeSelection ts = (TreeSelection) viewer.getSelection();
				org.eclipse.jface.viewers.TreePath tp = ts.getPaths()[0];
				if (tp.getFirstSegment() instanceof AbstractStructureRootNode) {
					AbstractStructureRootNode rootNode = (AbstractStructureRootNode) tp
							.getFirstSegment();
					Object obj = ts.getFirstElement();
					if (obj instanceof ChildNode) {
						rootNode.removeChild((ChildNode) obj);
					}
					viewer.refresh();
					calcCheckBox(viewer, rootNode);
				}
				validate();
			}

			protected boolean updateSelection(IStructuredSelection selection) {
				Object obj = selection.getFirstElement();
				if (obj == null || obj instanceof IStructureRootNode)
					return false;
				TreeSelection ts = (TreeSelection) viewer.getSelection();
				org.eclipse.jface.viewers.TreePath tp = ts.getPaths()[0];
				if (tp.getFirstSegment() instanceof AbstractStructureRootNode) {
					AbstractStructureRootNode rootNode = (AbstractStructureRootNode) tp
							.getFirstSegment();
					return rootNode
							.canOperation(IStructureRootNode.REMOVE_OPERATION);
				}
				return true;
			}
		};
		deleteAction.setImageDescriptor(PlatformUI.getWorkbench()
				.getSharedImages()
				.getImageDescriptor(ISharedImages.IMG_ELCL_REMOVE));

	}

	public void updateModel(Model parentModel, NameRule nameRule) {

		for (AbstractStructureNodeModel nodeModel : getWebxStructureModel()) {
			parentModel.addModule(GeneralUtils.nameAsModulePath(nodeModel
					.getName()));
			Dependency dependency = nodeModel.selfAsDependencyForParent(
					parentModel.getGroupId(), nameRule);
			if (dependency != null)
				parentModel.getDependencyManagement().addDependency(dependency);
		}
	}

	public WebxStructureModel getWebxStructureModel() {
		WebxStructureModel model = new WebxStructureModel();
		Object[] objs = contentProvider.getRootNodes();
		for (Object obj : objs) {
			if(!viewer.getChecked(obj))
				continue;
			if (obj instanceof WebStructureRootNode) {
				WebStructureRootNode node = (WebStructureRootNode) obj;
				model.getWebs().addAll(node.getModelList(viewer));
			} else if (obj instanceof BizStructureRootNode) {
				model.getBizs().addAll(
						((BizStructureRootNode) obj).getModelList(viewer));
			} else if (obj instanceof CommonConfigRootNode) {
				model.getCommons().addAll(
						((CommonConfigRootNode) obj).getModelList(viewer));
			} else if (obj instanceof DeployRootNode) {
				model.getDeploys()
						.addAll(((DeployRootNode) obj).getModelList(viewer));
			} else if (obj instanceof BundleWarRootNode) {
				model.getWars()
						.addAll(((BundleWarRootNode) obj).getModelList(viewer));
			} else if (obj instanceof DalStructureRootNode) {
				model.getDals().addAll(
						((DalStructureRootNode) obj).getModelList(viewer));
			}
		}
		return model;

	}

	private void calcCheckBox(CheckboxTreeViewer viewer,
			IStructureRootNode parent) {
		boolean origChecked = viewer.getChecked(parent);
		ChildNode[] children = parent.getChildren();
		if (children == null || children.length == 0) {
			viewer.setGrayChecked(parent, false);
			viewer.setChecked(parent, false);
		}
		boolean childChecked = false;
		boolean isBegin = false;
		for (ChildNode child : children) {
			if (!isBegin) {
				isBegin = true;
				childChecked = viewer.getChecked(child);
			} else {
				if(childChecked != viewer.getChecked(child)){
					viewer.setGrayChecked(parent, true);
					return;
				}
			}
		}
		viewer.setGrayChecked(parent, false);
		viewer.setChecked(parent, childChecked);
	}

}
