package webx.studio.server.ui.cnf;

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


/**
 * @author zhihua.hanzh
 *
 */
public class ServerLabelProvider  extends BaseCellLabelProvider{

	/**
	 * ServerTableLabelProvider constructor comment.
	 */
	public ServerLabelProvider() {
		super();
		this.providerImageCache = new HashMap<String, Image>();
	}

	public String getText(Object element) {
		if( element instanceof Server ) {
			Server server = (Server) element;
			return StringUtils.trimToEmpty(server.getName());
		}else if(element instanceof ServerChild){
			return ((ServerChild)element).getName();
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

		if (element instanceof ServerChild) {
			return ImageResource.getImage(ImageResource.IMG_JEJU_PROJECT);
		} else if( element instanceof Server ) {
			Server server = (Server) element;
			ServerType type = server.getServerType();
			if (type != null) {
				if(type.getId().indexOf("jetty") != -1){
					return ImageResource.getImage(ImageResource.IMG_JETTY_SERVER);
				}else{
					return ImageResource.getImage(ImageResource.IMG_JBOSS_SERVER);
				}
			}
		}
		return null;
	}


	private final Map<String, Image> providerImageCache;
}