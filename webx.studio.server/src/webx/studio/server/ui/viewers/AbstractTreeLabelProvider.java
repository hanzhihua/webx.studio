package webx.studio.server.ui.viewers;


import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;

import webx.studio.server.ui.cnf.BaseLabelProvider;


public abstract class AbstractTreeLabelProvider  extends BaseLabelProvider {
	/**
	 * A standard tree label provider.
	 */
	public AbstractTreeLabelProvider() {
		super();
	}

	/**
	 * A standard tree label provider.
	 *
	 * @param decorator a label decorator, or null if no decorator is required
	 */
	public AbstractTreeLabelProvider(ILabelDecorator decorator) {
		super(decorator);
	}

	/**
	 * @see BaseLabelProvider#getImage(Object)
	 */
	public Image getImage(Object element) {
		if (element instanceof AbstractTreeContentProvider.TreeElement) {
			ISharedImages sharedImages = PlatformUI.getWorkbench().getSharedImages();
			return sharedImages.getImage(ISharedImages.IMG_OBJ_FOLDER);
		}
		Image image = getImageImpl(element);
		if (decorator != null) {
			Image dec = decorator.decorateImage(image, element);
			if (dec != null)
				return dec;
		}
		return image;
	}

	/**
	 * Return an image for the given element.
	 *
	 * @param element an element
	 * @return an image
	 */
	protected abstract Image getImageImpl(Object element);

	/**
	 * @see BaseLabelProvider#getText(Object)
	 */
	public String getText(Object element) {
		if (element instanceof AbstractTreeContentProvider.TreeElement) {
			return ((AbstractTreeContentProvider.TreeElement) element).text;
		}
		String text = getTextImpl(element);
		if (decorator != null) {
			String dec = decorator.decorateText(text, element);
			if (dec != null && !dec.equals(""))
				return dec;
		}
		return text;
	}

	/**
	 * Return a label for the given element.
	 *
	 * @param element an element
	 * @return a label
	 */
	protected abstract String getTextImpl(Object element);
}