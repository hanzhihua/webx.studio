package webx.studio.server.ui.wizard;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontData;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Tree;

import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.server.core.Server;
import webx.studio.server.ui.ServerUILabelProvider;
import webx.studio.utils.UIUtil;



public class ModifyModulesComposite extends Composite {

	protected static Color color;
	protected static Font font;

	protected TreeViewer availableTreeViewer;
	protected TreeViewer deployedTreeViewer;

	protected Button add, addAll;
	protected Button remove, removeAll;

	private static final String ROOT = "root";

	protected List<String> deployeds = new ArrayList<String>();
	protected List<String> availables = new ArrayList<String>();

	private Server server;

	public ModifyModulesComposite(Composite parent) {
		super(parent, SWT.NONE);
		createControl();
	}

	protected void createControl() {
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = UIUtil.convertHorizontalDLUsToPixels(this,
				4);
		layout.verticalSpacing = UIUtil.convertVerticalDLUsToPixels(this, 4);
		layout.numColumns = 3;
		setLayout(layout);
		setFont(getParent().getFont());

		Display display = getDisplay();
		color = display.getSystemColor(SWT.COLOR_DARK_GRAY);
		FontData[] fd = getFont().getFontData();
		int size2 = fd.length;
		for (int i = 0; i < size2; i++)
			fd[i].setStyle(SWT.ITALIC);
		font = new Font(display, fd);
		addDisposeListener(new DisposeListener() {
			public void widgetDisposed(DisposeEvent e) {
				font.dispose();
			}
		});

		Label label = new Label(this, SWT.NONE);
		GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_BEGINNING);
		data.horizontalSpan = 3;
		label.setLayoutData(data);
		label.setText("Move WebX projects to the right to configure them on the server");

		label = new Label(this, SWT.NONE);
		label.setText("&Available:");

		label = new Label(this, SWT.NONE);
		label.setText("");

		label = new Label(this, SWT.NONE);
		label.setText("&Configured:");

		Tree availableTree = new Tree(this, SWT.BORDER | SWT.MULTI);
		data = new GridData(GridData.FILL_BOTH);
		data.heightHint = 200;
		data.widthHint = 150;
		availableTree.setLayoutData(data);

		availableTreeViewer = new TreeViewer(availableTree);
		ILabelProvider labelProvider = new ServerUILabelProvider();
		availableTreeViewer.setLabelProvider(labelProvider);
		availableTreeViewer.setContentProvider(new AvailableContentProvider());
		availableTreeViewer.setInput(ROOT);

		availableTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setEnablement();
			}
		});

		availableTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				setEnablement();
				if (add.isEnabled())
					add(false);
			}
		});

		Composite comp = new Composite(this, SWT.NONE);
		data = new GridData(GridData.FILL_VERTICAL
				| GridData.HORIZONTAL_ALIGN_FILL);
		data.widthHint = 120;
		comp.setLayoutData(data);

		layout = new GridLayout();
		layout.marginWidth = 5;
		layout.marginHeight = 25;
		layout.verticalSpacing = 20;
		comp.setLayout(layout);

		add = new Button(comp, SWT.PUSH);
		add.setText("A&dd >");
		add.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		add.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				add(false);
			}
		});

		remove = new Button(comp, SWT.PUSH);
		remove.setText("< &Remove");
		remove.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		remove.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remove(false);
			}
		});

		label = new Label(comp, SWT.NONE);
		label.setText("");

		addAll = new Button(comp, SWT.PUSH);
		addAll.setText("Add A&ll >>");
		addAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		addAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				add(true);
			}
		});

		removeAll = new Button(comp, SWT.PUSH);
		removeAll.setText("<< Re&move All");
		removeAll.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		removeAll.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent event) {
				remove(true);
			}
		});

		Tree deployedTree = new Tree(this, SWT.BORDER | SWT.MULTI);
		data = new GridData(GridData.FILL_BOTH);
		data.widthHint = 150;
		deployedTree.setLayoutData(data);

		deployedTreeViewer = new TreeViewer(deployedTree);
		labelProvider = new ServerUILabelProvider();
		deployedTreeViewer.setLabelProvider(labelProvider);
		deployedTreeViewer.setContentProvider(new DeployedContentProvider());
		deployedTreeViewer.setInput(ROOT);

		deployedTreeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				setEnablement();
			}
		});
		deployedTreeViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				setEnablement();
				if (remove.isEnabled())
					remove(false);
			}
		});

		setEnablement();
		availableTree.setFocus();

		Dialog.applyDialogFont(this);
	}

	public void setServer(Server server) {
		if(server == this.server)
			return;

		availables.clear();
		deployeds.clear();
		this.server = server;

		List<JejuProject> totalWebXProjects = JejuProjectCore.getWebXProjectList();
		for (String name : server.getOnlyWebXProjectNames()) {
			deployeds.add(name);
		}

		for (JejuProject project : totalWebXProjects) {
			if (!deployeds.contains(project.getName())) {
				availables.add(project.getName());
			}
		}
		availableTreeViewer.refresh();
		deployedTreeViewer.refresh();

		setEnablement();

	}


	class AvailableContentProvider implements ITreeContentProvider {


		public void dispose() {
		}


		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}


		public Object[] getElements(Object inputElement) {
			return availables.toArray(new String[0]);
		}


		public Object[] getChildren(Object parentElement) {
			return null;
		}


		public Object getParent(Object element) {
			return null;
		}


		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	class DeployedContentProvider implements ITreeContentProvider {


		public void dispose() {
			// TODO Auto-generated method stub

		}


		public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		}


		public Object[] getElements(Object inputElement) {
			return deployeds.toArray(new String[0]);
		}


		public Object[] getChildren(Object parentElement) {
			// TODO Auto-generated method stub
			return null;
		}


		public Object getParent(Object element) {
			// TODO Auto-generated method stub
			return null;
		}


		public boolean hasChildren(Object element) {
			// TODO Auto-generated method stub
			return false;
		}

	}

	protected void add(boolean all) {
		if (all) {
			moveAll(availables.toArray(new String[0]), true);
		} else {
			moveAll(getAvailableSelection(), true);
		}
	}

	protected String[] getAvailableSelection() {
		IStructuredSelection sel = (IStructuredSelection) availableTreeViewer
				.getSelection();
		if (sel.isEmpty())
			return new String[0];

		String[] mss = new String[sel.size()];
		System.arraycopy(sel.toArray(), 0, mss, 0, sel.size());
		return mss;
	}

	protected String[] getDeployedSelection() {
		IStructuredSelection sel = (IStructuredSelection) deployedTreeViewer
				.getSelection();
		if (sel.isEmpty())
			return new String[0];

		String[] mss = new String[sel.size()];
		System.arraycopy(sel.toArray(), 0, mss, 0, sel.size());
		return mss;
	}

	protected void moveAll(String[] mods, boolean add2) {
		for (String str : mods) {
			if (add2) {
				if (!deployeds.contains(str))
					deployeds.add(str);
				availables.remove(str);
				availableTreeViewer.remove(str);
				deployedTreeViewer.add(ROOT, str);

			} else {
				deployeds.remove(str);
				if (!availables.contains(str))
					availables.add(str);
				availableTreeViewer.add(ROOT, str);
				deployedTreeViewer.remove(str);

			}
		}
		setEnablement();
		updateServerModel();
	}

	protected void setEnablement() {
		add.setEnabled(getAvailableSelection().length>0);
		remove.setEnabled(getDeployedSelection().length>0);
		addAll.setEnabled(availables.size() > 0);
		removeAll.setEnabled(deployeds.size() > 0);
	}

	protected void remove(boolean all) {
		if (all) {
			moveAll(deployeds.toArray(new String[0]), false);
		} else {
			moveAll(getDeployedSelection(), false);
		}
	}

	protected void updateServerModel() {
		server.setWebXProjects(deployeds);
	}

}