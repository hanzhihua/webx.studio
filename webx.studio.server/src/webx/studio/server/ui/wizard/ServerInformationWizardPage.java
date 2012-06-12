package webx.studio.server.ui.wizard;


import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

import webx.studio.ImageResource;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerCore;
import webx.studio.server.core.ServerType;
import webx.studio.server.ui.viewers.ServerTypeComposite;

public class ServerInformationWizardPage extends WizardPage {

	private Text serverName;
	private Text port;
	private Text host;
	private Button openBrowser;
	private Button withoutAutoconfig;
	private Button withoutReloadFunction;
	private Combo uriCharset;
	private ServerTypeComposite typeComposite;
	private Server server = ServerCore.newServer();


	private final String defaultPort = "8080";
	private final String defaultHost = "localhost";
	private final ModifyListener modifyListener = new InternalModifyListener();

	protected ServerInformationWizardPage(String pageName) {
		super(pageName);
		this.setTitle("Define a new jeju server");
		this.setDescription("Choose one type of server to create");
		this.setImageDescriptor(ImageResource
				.getImageDescriptor(ImageResource.IMG_NEW_SERVER_WIZ));

	}

	public final void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		initServerType(container);
		initServerOthers(container);
		setControl(container);
		validate();
	}

	private void initServerType(Composite parent) {
		typeComposite = new ServerTypeComposite(parent);
		typeComposite.getTreeViewer().addSelectionChangedListener(
				new ISelectionChangedListener() {


					public void selectionChanged(SelectionChangedEvent event) {
						validate();

					}
				});

	}

	private void initServerOthers(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		layout.numColumns = 2;

		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(layout);
		container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		Label serverNameLabel = new Label(container, SWT.NONE);
		serverNameLabel.setText("Server na&me:");
		serverName = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		serverName.setLayoutData(new GridData(GridData.FILL, GridData.CENTER,
				true, false));
		serverName.addModifyListener(modifyListener);
		Label portLabel = new Label(container, SWT.NONE);
		portLabel.setText("Server &Port:");
		port = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		port.setText(defaultPort);
		port.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
				false));
		port.addModifyListener(modifyListener);

		Label hostLable = new Label(container, SWT.NONE);
		hostLable.setText("&Host name:");
		host = new Text(container, SWT.SINGLE | SWT.BORDER | SWT.CANCEL);
		host.setText(defaultHost);
		host.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, true,
				false));
		host.addModifyListener(modifyListener);

		Label withoutAutoconfigLabel = new Label(container, SWT.NONE);
		withoutAutoconfigLabel.setText("Server start without autoconfig");
		withoutAutoconfig = new Button(container, SWT.CHECK);
		withoutAutoconfig.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false,
				false));

		Label openBrowserLabel = new Label(container, SWT.NONE);
		openBrowserLabel.setText("Open browser after server start");
		openBrowser = new Button(container, SWT.CHECK);
		openBrowser.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false,
				false));

		Label withoutReloadFunctionLabel = new Label(container, SWT.NONE);
		withoutReloadFunctionLabel.setText("Server start without the Reload Function");
		withoutReloadFunction = new Button(container, SWT.CHECK);
		withoutReloadFunction.setLayoutData(new GridData(GridData.FILL, GridData.CENTER, false,
				false));

		Label uriCharsetLabel = new Label(container, SWT.NONE);
		uriCharsetLabel.setText("Request URI charset:");
		uriCharset = new Combo(container, SWT.BORDER| SWT.READ_ONLY);
		uriCharset.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true,
				false));

		uriCharset.setItems(Server.DEFAULT_URI_CHARSETS);
		uriCharset.setText(Server.DEFAULT_URI_CHARSETS[0]);
	}

	public Server getServer() {

		server.setName(serverName.getText());
		server.setPort(port.getText());
		server.setHost(host.getText());
		server.setOpenBrowser(openBrowser.getSelection());
		server.setWithoutAutoconfig(withoutAutoconfig.getSelection());
		server.setWithoutReloadfunction(withoutReloadFunction.getSelection());
		server.setUriCharset(uriCharset.getText());
		ServerType type = typeComposite.getSelectedServerType();
		server.setType(type);
		return server;
	}

	public void reset(){
		server = ServerCore.newServer();
	}

	void validate() {
		ServerType serverType = typeComposite.getSelectedServerType();
		if (serverType == null) {
			setErrorMessage("Please select a server type!");
			return;
		}
		String serverNameText = serverName.getText();
		String portText = port.getText();

		if (serverNameText == null
				|| "".equalsIgnoreCase(serverNameText.trim())) {
			setErrorMessage("Please input a server name!");
			return;
		}

		if (portText == null || "".equals(portText.trim())) {
			setErrorMessage("Please input a port!");
			return;
		}

		try {
			int portNumber = Integer.parseInt(portText);
		} catch (Exception e) {
			setErrorMessage("Please input a vaild port!");
			return;
		}
		setErrorMessage(null);

	}

	public void setErrorMessage(String message) {
		if (message != null) {
			super.setErrorMessage(message);
			setPageComplete(false);
		}else{
			super.setErrorMessage(null);
//			 getContainer().updateMessage();
			setPageComplete(true);
		}
	}

	private class InternalModifyListener implements ModifyListener {
		public void modifyText(ModifyEvent e) {
			validate();
		}
	}

}
