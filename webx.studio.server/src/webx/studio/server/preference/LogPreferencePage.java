package webx.studio.server.preference;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.osgi.framework.FrameworkUtil;

import webx.studio.server.ui.actions.ExportLogAction;

public class LogPreferencePage extends PreferencePage implements
		IWorkbenchPreferencePage {

	public LogPreferencePage() {
//		setDescription("Export all jeju log !");
	}

	public LogPreferencePage(String title) {
		super(title);
		// TODO Auto-generated constructor stub
	}

	public LogPreferencePage(String title, ImageDescriptor image) {
		super(title, image);
		// TODO Auto-generated constructor stub
	}

	public void init(IWorkbench workbench) {
		// TODO Auto-generated method stub

	}

	
	protected Control createContents(Composite parent) {
		// TODO Auto-generated method stub
		noDefaultAndApplyButton();

		initializeDialogUnits(parent);

		Composite composite = new Composite(parent, SWT.NONE);
		GridLayout layout = new GridLayout();
		layout.horizontalSpacing = convertHorizontalDLUsToPixels(4);
		layout.verticalSpacing = convertVerticalDLUsToPixels(4);
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 3;
		composite.setLayout(layout);
		GridData data = new GridData(GridData.FILL_HORIZONTAL
				| GridData.VERTICAL_ALIGN_FILL);
		composite.setLayoutData(data);

		Button button = new Button(composite, SWT.WRAP);
		data = new GridData(GridData.HORIZONTAL_ALIGN_FILL);
		data.horizontalSpan = 2;
		button.setLayoutData(data);
		button.setText("Export Log");
		button.addMouseListener(new MouseListener() {

			public void mouseUp(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			public void mouseDown(MouseEvent e) {
				// TODO Auto-generated method stub
				new ExportLogAction().run(null);
			}

			public void mouseDoubleClick(MouseEvent e) {
				new ExportLogAction().run(null);
			}
		});

		Dialog.applyDialogFont(composite);

		return composite;

	}

}
