package webx.studio.projectcreation.ui.editor;

import java.io.File;


import org.apache.commons.lang.StringUtils;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.fieldassist.FieldDecorationRegistry;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.ManagedForm;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.part.EditorPart;

import webx.studio.projectcreation.ui.ProjectCreationPlugin;
import webx.studio.projectcreation.ui.core.JejuProject;
import webx.studio.projectcreation.ui.core.JejuProjectCore;
import webx.studio.projectcreation.ui.project.ProjectHelper;
import webx.studio.projectcreation.ui.views.IWebXProjectLifecycleListener;

/**
 * @author zhihua.hanzh
 *
 */
public class WebXProjectEditor extends EditorPart {

	private String projectId;
	private JejuProject webxProject;

	private boolean isDirty = false;
	protected ManagedForm managedForm;

	protected Text webxProjectName;
	protected Text settingFile;
	protected Text antxPropertiesFile;
	protected Text antxDestFiles;
	protected Text antxIncludeDescriptorPatterns;
	protected Text webxVersion;
	private Text webRoot;
	private Combo antxCharset;

	private static final String[] DEFAULT_AUTOCONFIG_CHARSETS = { "GBK",
			"UTF-8" };

	protected boolean changedWebXVersion;

	protected LifecycleListener resourceListener;

	public WebXProjectEditor() {
		// TODO Auto-generated constructor stub
	}

	class LifecycleListener implements IWebXProjectLifecycleListener {


		public void projectAdded(JejuProject project) {
			// TODO Auto-generated method stub

		}


		public void projectChanged(JejuProject project) {
			// TODO Auto-generated method stub

		}


		public void projectRemoved(JejuProject project) {
			if (webxProject.equals(project))
				closeEditor();

		}


		public void reloadProjects() {
			// TODO Auto-generated method stub

		}

	}


	public void init(IEditorSite site, IEditorInput input)
			throws PartInitException {
		setSite(site);
		setInput(input);
		if (input instanceof IProjectEditorInput) {
			IProjectEditorInput pei = (IProjectEditorInput) input;
			projectId = pei.getProjectId();
			webxProject = JejuProjectCore.findWebXProject(projectId);
		}
		if (webxProject != null) {
			setPartName(webxProject.getName());
			setTitleToolTip(webxProject.getId());
		} else {
			setPartName("-");
		}

		resourceListener = new LifecycleListener();
		JejuProjectCore.addWebXProjectLifecycleListener(resourceListener);

	}

	public void dispose() {
		if (resourceListener != null)
			JejuProjectCore
					.removeWebXProjectLifecycleListener(resourceListener);
	}


	public boolean isDirty() {
		return this.isDirty;
	}


	public boolean isSaveAsAllowed() {
		return false;
	}


	public void createPartControl(Composite parent) {
		managedForm = new ManagedForm(parent);
		ScrolledForm form = managedForm.getForm();
		FormToolkit toolkit = managedForm.getToolkit();
		toolkit.decorateFormHeading(form.getForm());
		form.setText("Overview");
		// form.setImage(ImageResource.getImage(ImageResource.IMG_SERVER));
		form.getBody().setLayout(new GridLayout());

		Composite columnComp = toolkit.createComposite(form.getBody());
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		layout.verticalSpacing = 0;
		layout.horizontalSpacing = 10;
		columnComp.setLayout(layout);
		columnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));

		Composite leftColumnComp = toolkit.createComposite(columnComp);
		layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 10;
		layout.horizontalSpacing = 0;
		leftColumnComp.setLayout(layout);
		leftColumnComp.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));

		createGeneralSection(leftColumnComp, toolkit);

	}

	protected void createGeneralSection(Composite leftColumnComp,
			FormToolkit toolkit) {

		Section section = toolkit.createSection(leftColumnComp,
				ExpandableComposite.TITLE_BAR | Section.DESCRIPTION);
		section.setText("General Information");
		section.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));
		Composite composite = toolkit.createComposite(section);
		GridLayout layout = new GridLayout();
		layout.numColumns = 3;
		layout.marginHeight = 5;
		layout.marginWidth = 10;
		layout.verticalSpacing = 5;
		layout.horizontalSpacing = 5;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL));
		toolkit.paintBordersFor(composite);
		section.setClient(composite);

		int decorationWidth = FieldDecorationRegistry.getDefault()
				.getMaximumDecorationWidth();
		createLabel(toolkit, composite, "WebX Project Name:");
		webxProjectName = toolkit.createText(composite, "", SWT.SINGLE
				| SWT.READ_ONLY);
		GridData data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		webxProjectName.setLayoutData(data);
		if (webxProject != null)
			webxProjectName.setText(StringUtils.trimToEmpty(webxProject
					.getName()));

		createLabel(toolkit, composite, "AntX Properties File:");
		antxPropertiesFile = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		antxPropertiesFile.setLayoutData(data);
		if (webxProject != null)
			antxPropertiesFile.setText(StringUtils.trimToEmpty(webxProject
					.getAntxPropertiesFile()));

		antxPropertiesFile.addModifyListener(new ModifyListener() {


			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});

		createLabel(toolkit, composite, "AntX Dest Files:");
		antxDestFiles = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		antxDestFiles.setLayoutData(data);
		if (webxProject != null)
			antxDestFiles.setText(StringUtils.trimToEmpty(webxProject
					.getAntxDestFiles()));

		antxDestFiles.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});

		createLabel(toolkit, composite, "AntX Include Descriptor Patterns:");
		antxIncludeDescriptorPatterns = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		antxIncludeDescriptorPatterns.setLayoutData(data);
		if (webxProject != null)
			antxIncludeDescriptorPatterns.setText(StringUtils.trimToEmpty(webxProject
					.getAntxIncludeDescriptorPatterns()));

		antxIncludeDescriptorPatterns.addModifyListener(new ModifyListener() {

			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});

		createLabel(toolkit, composite, "Setting File:");
		settingFile = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		settingFile.setLayoutData(data);
		if (webxProject != null)
			settingFile.setText(StringUtils.trimToEmpty(webxProject
					.getSettingFile()));

		settingFile.addModifyListener(new ModifyListener() {


			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});

		createLabel(toolkit, composite, "Anto Config Charst:");
		antxCharset = new Combo(composite, SWT.BORDER | SWT.READ_ONLY);
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		antxCharset.setLayoutData(data);
		antxCharset.setItems(DEFAULT_AUTOCONFIG_CHARSETS);
		antxCharset.setText(StringUtils.trimToEmpty(webxProject
				.getAutoconfigCharset()));
		antxCharset.addModifyListener(new ModifyListener() {


			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});

		createLabel(toolkit, composite, "Web Root:");
		webRoot = toolkit.createText(composite, "");
		data = new GridData(GridData.FILL_HORIZONTAL);
		data.horizontalSpan = 2;
		data.horizontalIndent = decorationWidth;
		webRoot.setLayoutData(data);
		if (webxProject != null)
			webRoot.setText(StringUtils.trimToEmpty(webxProject.getWebRoot()));

		webRoot.addModifyListener(new ModifyListener() {


			public void modifyText(ModifyEvent e) {
				setDirty(true);
			}
		});

		if (webxProject != null
				&& StringUtils.isNotBlank(webxProject.getWebxVersion())) {
			createLabel(toolkit, composite, "WebX Version:");

			webxVersion = toolkit.createText(composite, "",SWT.SINGLE|SWT.READ_ONLY);
			data = new GridData(GridData.FILL_HORIZONTAL);
			data.horizontalSpan = 2;
			data.horizontalIndent = decorationWidth;
			webxVersion.setLayoutData(data);

			webxVersion.setText(StringUtils.trimToEmpty(webxProject
					.getWebxVersion()));

			webxVersion.addModifyListener(new ModifyListener() {


				public void modifyText(ModifyEvent e) {
					setDirty(true);
				}
			});
		}

	}

	protected Label createLabel(FormToolkit toolkit, Composite parent,
			String text) {
		Label label = toolkit.createLabel(parent, text);
		label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
		return label;
	}

	private void setDirty(boolean isDirty) {
		this.isDirty = isDirty;
		firePropertyChange(PROP_DIRTY);
	}


	public void doSave(IProgressMonitor monitor) {
		if (webxProject != null) {
			changeProjectInstance();
			webxProject.save();
			if (changedWebXVersion) {
				try {
					File topDir = JejuProjectCore
							.getTopDir(webxProject.getId());
					ProjectHelper.changeWebXVersion(topDir, webxProject
							.getSettingFile(), webxProject.getWebxVersion());
				} catch (Exception e) {
					ProjectCreationPlugin.logThrowable(e);
				}
			}
			changedWebXVersion = false;
		}
		setDirty(false);
		setPartName(StringUtils.trimToEmpty(webxProject.getName()));
	}

	public void changeProjectInstance() {
		if (!StringUtils.equals(antxPropertiesFile.getText(), webxProject
				.getAntxPropertiesFile())) {
			webxProject.setAntxPropertiesFile(StringUtils
					.trimToEmpty(antxPropertiesFile.getText()));
		}

		if (!StringUtils.equals(antxDestFiles.getText(), webxProject
				.getAntxDestFiles())) {
			webxProject.setAntxDestFiles(StringUtils
					.trimToEmpty(antxDestFiles.getText()));
		}

		if (!StringUtils.equals(antxIncludeDescriptorPatterns.getText(), webxProject
				.getAntxIncludeDescriptorPatterns())) {
			webxProject.setAntxIncludeDescriptorPatterns(StringUtils
					.trimToEmpty(antxIncludeDescriptorPatterns.getText()));
		}

		if (!StringUtils.equals(settingFile.getText(), webxProject
				.getSettingFile())) {
			webxProject.setSettingFile(StringUtils.trimToEmpty(settingFile
					.getText()));
		}
		if (!StringUtils.equals(antxCharset.getText(), webxProject
				.getAutoconfigCharset())) {
			webxProject.setAutoconfigCharset(StringUtils
					.trimToEmpty(antxCharset.getText()));
		}
		if (!StringUtils.equals(webRoot.getText(), webxProject.getWebRoot())) {
			webxProject.setWebRoot(StringUtils
					.trimToEmpty(webRoot.getText()));
		}
		if (webxVersion != null && !StringUtils.equals(webxVersion.getText(), webxProject
				.getWebxVersion())) {
			webxProject.setWebxVersion(webxVersion.getText());
			changedWebXVersion = true;
		}
	}


	public void doSaveAs() {
	}

	protected void closeEditor() {
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				getEditorSite().getPage().closeEditor(WebXProjectEditor.this,
						false);
			}
		});
	}


	public void setFocus() {
		// TODO Auto-generated method stub

	}

}
