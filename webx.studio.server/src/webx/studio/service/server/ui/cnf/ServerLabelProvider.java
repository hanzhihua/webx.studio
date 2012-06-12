package webx.studio.service.server.ui.cnf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


import org.apache.commons.lang.StringUtils;
import org.eclipse.swt.graphics.Image;

import webx.studio.ImageResource;
import webx.studio.server.core.Server;
import webx.studio.server.core.ServerChild;
import webx.studio.server.core.ServerType;
import webx.studio.server.ui.viewers.BaseCellLabelProvider;
import webx.studio.service.server.core.ServiceServer;
import webx.studio.service.server.core.ServiceServerChild;

public class ServerLabelProvider   extends BaseCellLabelProvider{

	public ServerLabelProvider() {
		super();
		this.providerImageCache = new HashMap<String, Image>();
	}

	public String getText(Object element) {
		if( element instanceof ServiceServer ) {
			ServiceServer server = (ServiceServer) element;
			return StringUtils.trimToEmpty(server.getName());
		}else if(element instanceof ServiceServerChild){
			ServiceServerChild child = (ServiceServerChild) element;
			return StringUtils.trimToEmpty(child.getName());
		}
		return "";
	}

	@Override
	public void dispose() {

		super.dispose();
		if (this.providerImageCache != null) {
			final Iterator<Image> providerImageCacheIterator = this.providerImageCache.values().iterator();
			while (providerImageCacheIterator.hasNext()) {
				providerImageCacheIterator.next().dispose();
			}
			this.providerImageCache.clear();
		}

	}

	public Image getImage(Object element) {

		if (element instanceof ServiceServerChild) {
			return ImageResource.getImage(ImageResource.IMG_JAVA_PROJECT);
		} else if( element instanceof ServiceServer ) {
			ServiceServer server = (ServiceServer) element;
			return ImageResource.getImage(ImageResource.IMG_SERVICE_SERVER);
		}
		return null;
	}


	private final Map<String, Image> providerImageCache;
}
