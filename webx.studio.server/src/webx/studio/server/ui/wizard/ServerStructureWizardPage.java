package webx.studio.server.ui.wizard;


import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;

import webx.studio.ImageResource;


public class ServerStructureWizardPage  extends WizardPage {

	protected ModifyModulesComposite composite;

	 protected ServerStructureWizardPage(String pageName) {
		super(pageName);
		this.setTitle("Configure a New Server");
		this.setDescription("Choose the type of server to create");
		this.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_NEW_SERVER_WIZ));

	}

	protected ServerStructureWizardPage(String pageName,String title,String description){
		super(pageName);
		this.setTitle(title);
		this.setDescription(description);
		this.setImageDescriptor(ImageResource.getImageDescriptor(ImageResource.IMG_NEW_SERVER_WIZ));
	}

	public final void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		GridLayout layout = new GridLayout();
		layout.marginWidth = 0;
		layout.marginHeight = 0;
		container.setLayout(layout);
		initControl(container);

		setControl(container);
	 }

	private void initControl(Composite parent){
		composite = new ModifyModulesComposite(parent);
	}







}