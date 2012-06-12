package webx.studio.server.ui.viewers;


import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;

import webx.studio.ImageResource;
import webx.studio.server.core.ServerType;


public class ServerTypeTreeLabelProvider extends AbstractTreeLabelProvider {
	/**
	 * ServerTypeTreeLabelProvider constructor comment.
	 */
	public ServerTypeTreeLabelProvider() {
		super();
	}

	/**
	 * ServerTypeTreeLabelProvider constructor comment.
	 *
	 * @param decorator
	 *            a label decorator, or null if no decorator is required
	 */
	public ServerTypeTreeLabelProvider(ILabelDecorator decorator) {
		super(decorator);
	}

	/**
	 *
	 */
	protected Image getImageImpl(Object element) {
		// ServerType type = (ServerType) element;
		if (element instanceof ServerType) {
			if (((ServerType) element).getId().indexOf("jetty") != -1) {
				return ImageResource.getImage("jetty");
			} else {
				return ImageResource.getImage("jboss");
			}
		}

		return null;
	}

	/**
	 *
	 */
	protected String getTextImpl(Object element) {
		ServerType type = (ServerType) element;
		return notNull(type.getName());
	}
}