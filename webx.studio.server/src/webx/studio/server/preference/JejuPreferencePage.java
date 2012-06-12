package webx.studio.server.preference;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Link;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.FrameworkUtil;

import webx.studio.server.ServerPlugin;
import webx.studio.utils.BrowserUtil;

public class JejuPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public JejuPreferencePage() {
		setDescription("Jeju is a suite of plugins for web develop, and it currently containes project creating and debugging !");
	}

	public JejuPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
	}

	public void init(IWorkbench workbench) {
	}

	@Override
	protected Control createContents(Composite parent) {

		noDefaultAndApplyButton();

		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 1;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);

		Label label = new Label(composite, SWT.WRAP);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		label.setLayoutData(data);
		label.setText("Version: "+FrameworkUtil.getBundle(this.getClass()).getBundleContext().getBundle().getVersion());

		Link link = new Link(composite, SWT.WRAP);
		link.setFont(parent.getFont());
		link.setText("<a>Site</a>");
		link.setToolTipText("http://b2b-doc.alibaba-inc.com/display/RC/Jeju_Home+%28WebX+IDE%29");
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BrowserUtil.checkedCreateExternalBrowser(
						"http://b2b-doc.alibaba-inc.com/display/RC/Jeju_Home+%28WebX+IDE%29",
						ServerPlugin.PLUGIN_ID,
						ServerPlugin.getDefault().getLog());
			}
		});

		link = new Link(composite, SWT.WRAP);
		link.setFont(parent.getFont());
		link.setText("<a>Jira</a>");
		link.setToolTipText("http://agile.alibaba-inc.com/browse/JEJU");
		link.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				BrowserUtil.checkedCreateExternalBrowser(
						"http://agile.alibaba-inc.com/browse/JEJU",
						ServerPlugin.PLUGIN_ID,
						ServerPlugin.getDefault().getLog());
			}
		});

		Dialog.applyDialogFont(composite);

		return composite;
	}

}
