package webx.studio.server.ui.launchtab;

import java.text.MessageFormat;


import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import webx.studio.server.ServerPlugin;
import webx.studio.server.jetty.Constants;
import webx.studio.utils.PortUtil;



public class JettyRunTab extends JavaLaunchTab {

	private Text fPortText;
	private String initPort = 80+"";
	private UpdateModfiyListener _updatedListener = new UpdateModfiyListener();

	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setFont(parent.getFont());

		GridData gd = new GridData(1);
		gd.horizontalSpan = GridData.FILL_BOTH;
		comp.setLayoutData(gd);

		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		comp.setLayout(layout);


		 createJettyPort(comp);
		 setControl(comp);

		return;
	}

	public void createJettyPort(Composite parent){
		Font font = parent.getFont();
		new Label(parent, SWT.LEFT).setText("Port");
		fPortText = new Text(parent, SWT.SINGLE | SWT.BORDER);
		fPortText.addModifyListener(_updatedListener);
		fPortText.setFont(font);
		fPortText.setTextLimit(5);
		fPortText.setLayoutData(createHFillGridData(5, -1));
		setWidthForSampleText(fPortText, " 65535 ");
//		fPortText.setEnabled(false);


	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			initPort = configuration.getAttribute(Constants.PORT, "");
			fPortText.setText(initPort);
		} catch (CoreException e) {
			ServerPlugin.logError(e);
		}
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		fPortText.setText(initPort);
		configuration.setAttribute(Constants.PORT, initPort);
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(Constants.PORT, fPortText.getText());
	}

	public String getName() {
		return "Jeju Jetty";
	}

	private GridData createHFillGridData() {
		GridData gd = new GridData();
		gd.horizontalAlignment = SWT.FILL;
		gd.grabExcessHorizontalSpace = true;
		return gd;
	}

	private GridData createHFillGridData(int span, int position) {
		GridData gd = createHFillGridData();
		if (position != -1)
			gd.horizontalAlignment = position;
		if (span != -1)
			gd.horizontalSpan = span;

		return gd;
	}

	private void setWidthForSampleText(Text control, String sampleText) {
		GC gc = new GC(control);
		try {
			Point sampleSize = gc.textExtent(sampleText);
			Point currentSize = control.getSize();
			sampleSize.y = currentSize.y;
			control.setSize(sampleSize);
			return;
		} finally {
			gc.dispose();
		}
	}

	private class UpdateModfiyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			updateLaunchConfigurationDialog();
		}
	}

	public boolean isValid(ILaunchConfiguration config) {
		setErrorMessage(null);
		setMessage(null);
		String port = fPortText.getText().trim();
		if (port.length() == 0 ) {
			setErrorMessage("Must specify at least one port");
			return false;
		}
		if(PortUtil.isInvalidPort(port)){
			setErrorMessage(MessageFormat.format(
					"Not a valid TCP port number: {0}", port));
			return false;
		}
		return true;
	}



}