package webx.studio.projectcreation.ui.components;


import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;

import webx.studio.utils.PathUtil;

public class AdvancedComponent extends ExpandableComposite {

	Combo settingFileCombo;
	Combo antxPropertiesCombo;
	Combo antxCharset;
	Composite parent;

	public Combo getSettingFileCombo() {
		return settingFileCombo;
	}

	private static final String[] DEFAULT_SETTING_FILES = {
			getUserSettingPath(), getGlobalSettingsPath() };
	private static final String[] DEFAULT_ANTX_PROPERTIES_FILEs = { getDefaultAntxPropertiesFile() };
	private static final String[] DEFAULT_AUTOCONFIG_CHARSETS = { "GBK",
			"UTF-8" };

	public AdvancedComponent(final Composite parent, int style) {
		super(parent, ExpandableComposite.COMPACT | ExpandableComposite.TWISTIE
				| ExpandableComposite.EXPANDED);
		this.parent = parent;
		setText("Ad&vanced");

		final Composite advancedComposite = new Composite(this, SWT.NONE);
		setClient(advancedComposite);
		addExpansionListener(new ExpansionAdapter() {
			public void expansionStateChanged(ExpansionEvent e) {
				Shell shell = parent.getShell();
				Point minSize = shell.getMinimumSize();
				shell.setMinimumSize(shell.getSize().x, minSize.y);
				shell.pack();
				parent.layout();
				shell.setMinimumSize(minSize);
			}
		});

		GridLayout gridLayout = new GridLayout();
		gridLayout.marginLeft = 11;
		gridLayout.numColumns = 2;
		advancedComposite.setLayout(gridLayout);

		Label settingFileLabel = new Label(advancedComposite, SWT.NONE);
		settingFileLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false));
		settingFileLabel.setText("Setting File:");

		settingFileCombo = new Combo(advancedComposite, SWT.BORDER);
		settingFileCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		settingFileCombo
				.setToolTipText("Please input a correct path of setting file");
		settingFileCombo.setItems(DEFAULT_SETTING_FILES);

		Label antxPropertiesLabel = new Label(advancedComposite, SWT.NONE);
		antxPropertiesLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false));
		antxPropertiesLabel.setText("Antx Properties File:");

		antxPropertiesCombo = new Combo(advancedComposite, SWT.BORDER);
		antxPropertiesCombo.setLayoutData(new GridData(SWT.FILL, SWT.CENTER,
				true, false));
		antxPropertiesCombo
				.setToolTipText("Please input a correct path of setting file");
		antxPropertiesCombo.setItems(DEFAULT_ANTX_PROPERTIES_FILEs);

		Label antxCharsetLabel = new Label(advancedComposite, SWT.NONE);
		antxCharsetLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER,
				false, false));
		antxCharsetLabel.setText("Anto Config Charst:");

		antxCharset = new Combo(advancedComposite, SWT.BORDER| SWT.READ_ONLY);
		antxCharset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));
		antxCharset
				.setToolTipText("Please input a correct path of setting file");
		antxCharset.setText("GBK");
		antxCharset.setItems(DEFAULT_AUTOCONFIG_CHARSETS);

	}

	public String getAutoConfigCharset(){
		return this.antxCharset.getText();
	}
	
	public String getSettingFile() {
		return this.settingFileCombo.getText();
	}

	public String getAntPropertiesFile() {
		return this.antxPropertiesCombo.getText();
	}

	private static String getUserSettingPath() {
		return PathUtil.getFile("${user.home}/.m2/settings.xml", "user.home",
				"shouldnotexistproperty").getAbsolutePath();
	}

	private static String getGlobalSettingsPath() {
		return PathUtil.getFile("${maven.home}/conf/settings.xml",
				"maven.home", "shouldnotexistproperty").getAbsolutePath();
	}

	private static String getDefaultAntxPropertiesFile() {
		return PathUtil.getFile("${user.home}/antx.properties", "user.home",
				"shouldnotexistproperty").getAbsolutePath();
	}

	public void setModifyingListener(ModifyListener modifyingListener) {
		settingFileCombo.addModifyListener(modifyingListener);
	}

}
