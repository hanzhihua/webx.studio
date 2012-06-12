package webx.studio.server.ui.launchtab;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaLaunchTab;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

import webx.studio.server.ServerPlugin;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerUtil;

public class WebappTab extends JavaLaunchTab {

	List<Data> datas = new ArrayList<Data>();

	class Data {
		String webxProjectName;
		String classpath;
	}

	public void createControl(Composite parent) {
		Composite comp = new Composite(parent, SWT.NONE);
		comp.setFont(parent.getFont());

		GridData gd = new GridData(1);
		gd.horizontalSpan = GridData.FILL_BOTH;
		comp.setLayoutData(gd);

		GridLayout layout = new GridLayout(1, false);
		layout.verticalSpacing = 0;
		comp.setLayout(layout);

		setControl(comp);
		return;
	}

	private void draw(Composite parent, String webxProjectName, String classpath) {
		Font font = parent.getFont();
		Group group = new Group(parent, SWT.NONE);
		group.setFont(font);
		group.setText(webxProjectName);
		GridLayout layout = new GridLayout();
		group.setLayout(layout);
		group.setLayoutData(new GridData(GridData.FILL_BOTH));

		Text text = new Text(group, SWT.MULTI | SWT.WRAP | SWT.BORDER
				| SWT.V_SCROLL | SWT.READ_ONLY);
		GridData gd = new GridData(GridData.FILL_BOTH);
		gd.heightHint = 40;
		gd.widthHint = 100;
		text.setLayoutData(gd);
		text.setFont(font);
		classpath = org.apache.commons.lang.StringUtils.replace(classpath, File.pathSeparator, System.getProperty("line.separator"));
		text.setText(classpath);

	}

	public void initializeFrom(ILaunchConfiguration configuration) {
		super.initializeFrom(configuration);
		if(getControl() == null)
			return;
//		datas.clear();
		if(datas.size() >0)
			return;
		try {
			Server server = (Server)ServerUtil.getServer(configuration);
			if (server == null)
				return;

			for (String webxProjectName : server.getOnlyWebXProjectNames()) {
				String classpathKey = ServerUtil
						.getWebmoduleClasspathKey(webxProjectName);
				String classpath = configuration.getAttribute(classpathKey, "");
				if (org.apache.commons.lang.StringUtils.isNotBlank(classpath)) {
					Data data = new Data();
					data.webxProjectName = webxProjectName;
					data.classpath = resovleWebappClasspath(classpath);
					datas.add(data);
				}
			}
		} catch (Exception e) {
			ServerPlugin.logError(e);
		}

		for (Data data : datas) {
			draw((Composite)getControl(), data.webxProjectName, data.classpath);
		}

	}

	private static String resovleWebappClasspath(String classpath) {

		if (classpath != null && classpath.startsWith("file://")) {
			try {
				String filePath = classpath.substring(7);

				BufferedReader br = new BufferedReader(new InputStreamReader(
						new FileInputStream(filePath), "UTF-8"));
				StringBuffer sb = new StringBuffer();
				String str = br.readLine();
				while (str != null) {
					sb.append(str);
					str = br.readLine();
				}
				return sb.toString();

			} catch (IOException e) {
				System.err.println("read classpath failed!");
				throw new RuntimeException(" read classpath failed ", e);
			}
		}

		return classpath;
	}

	public String getName() {
		return "WebX Project Classpath";
	}

	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
	}

	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
	}

}
