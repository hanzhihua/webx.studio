package webx.studio.server.ui;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.LabelProviderChangedEvent;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.model.IWorkbenchAdapter;

import webx.studio.ImageResource;
import webx.studio.server.core.Server;


public class ServerUILabelProvider  implements ILabelProvider, IColorProvider, IWorkbenchAdapter {
	private ILabelDecorator decorator;
	protected transient List<ILabelProviderListener> listeners;
	protected ILabelProviderListener providerListener;

	public ServerUILabelProvider() {
		decorator = PlatformUI.getWorkbench().getDecoratorManager().getLabelDecorator();
		providerListener = new ILabelProviderListener() {
			public void labelProviderChanged(LabelProviderChangedEvent event) {
				fireListener(event);
			}
		};
		decorator.addListener(providerListener);
	}

	public void addListener(ILabelProviderListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");

		if (listeners == null)
			listeners = new ArrayList<ILabelProviderListener>();
		listeners.add(listener);
	}

	public void removeListener(ILabelProviderListener listener) {
		if (listener == null)
			throw new IllegalArgumentException("Listener cannot be null");

		if (listeners != null)
			listeners.remove(listener);
	}

	protected void fireListener(LabelProviderChangedEvent event) {
		if (listeners == null || listeners.isEmpty())
			return;

		int size = listeners.size();
		ILabelProviderListener[] srl = new ILabelProviderListener[size];
		listeners.toArray(srl);

		for (int i = 0; i < size; i++) {
			try {
				srl[i].labelProviderChanged(event);
			} catch (Exception e) {

			}
		}
	}

	protected Image getModuleImage(String typeId) {
		if (typeId == null)
			return null;

		Image image = ImageResource.getImage(typeId);
		int ind = typeId.indexOf(".");
		while (image == null && ind >= 0) {
			typeId = typeId.substring(0, ind);
			image = ImageResource.getImage(typeId);
		}
		return image;
	}

	protected ImageDescriptor getModuleImageDescriptor(String typeId) {
		if (typeId == null)
			return null;

		ImageDescriptor image = ImageResource.getImageDescriptor(typeId);
		int ind = typeId.indexOf(".");
		while (image == null && ind >= 0) {
			typeId = typeId.substring(0, ind);
			image = ImageResource.getImageDescriptor(typeId);
		}
		return image;
	}

	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public ImageDescriptor getImageDescriptor(Object element) {
		try {
			return null;
		} catch (Exception e) {
		}
		return null;
	}

	private Image decorate(Image image, Object obj) {
		Image dec = decorator.decorateImage(image, obj);
		if (dec != null)
			return dec;
		return image;
	}

	private String decorate(String text, Object obj) {
		String dec = decorator.decorateText(text, obj);
		if (dec != null)
			return dec;
		return text;
	}

	/*
	 * @see ILabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		if(element instanceof Server){

		}
		return ImageResource.getImage(ImageResource.IMG_JEJU_PROJECT);
	}

	protected String getString(String s) {
		if (s == null)
			return "";

		return s;
	}

	/*
	 * @see ILabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element == null)
			return "";

		if(element instanceof Server){
			return ((Server)element).getName();
		}

		if(element instanceof String){
			return element.toString();
		}

		return "";
	}

	/*
	 * @see IBaseLabelProvider#isLabelProperty(Object, String)
	 */
	public boolean isLabelProperty(Object element, String property) {
		return false;
	}

	/*
	 * @see IBaseLabelProvider#dispose()
	 */
	public void dispose() {
		decorator.removeListener(providerListener);
	}

	public Color getBackground(Object element) {
		return null;
	}

	public Color getForeground(Object element) {
		return null;
	}

	public Object[] getChildren(Object o) {
		return null;
	}

	public String getLabel(Object o) {
		return getText(o);
	}

	public Object getParent(Object o) {
		return null;
	}
}