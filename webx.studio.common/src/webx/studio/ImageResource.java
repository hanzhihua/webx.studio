package webx.studio;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public class ImageResource {

	private static ImageRegistry imageRegistry;
	private static Map<String, ImageDescriptor> imageDescriptors;

	private static URL ICON_BASE_URL;
	static {
		try {
			String pathSuffix = "icons/";
			ICON_BASE_URL = StudioCommonPlugin.getInstance().getBundle()
					.getEntry(pathSuffix);
		} catch (Exception e) {
			StudioCommonPlugin.logError(e);
		}
	}

	private static final String URL_PROJECT = "project/";
	private static final String URL_SERVER = "server/";
	private static final String URL_LAUNCH = "launch/";
	private static final String URL_SERVICE = "service/";
	private static final String URL_EDITOR = "editor/";

	private ImageResource() {
	}

	protected static void dispose() {
	}

	public static Image getImage(String key) {
		if (imageRegistry == null)
			initializeImageRegistry();
		Image image = imageRegistry.get(key);
		if (image == null) {
			imageRegistry.put(key, ImageDescriptor.getMissingImageDescriptor());
			image = imageRegistry.get(key);
		}
		return image;
	}

	public static Image getImage(ImageDescriptor imageDescriptor) {
		if (imageRegistry == null)
			initializeImageRegistry();
		Image image = imageRegistry.get(imageDescriptor.hashCode() + "");
		if (image == null) {
			image = imageDescriptor.createImage(true);
			imageRegistry.put(imageDescriptor.hashCode() + "", image);
		}
		return image;
	}

	public static ImageDescriptor getImageDescriptor(String key) {
		if (imageRegistry == null)
			initializeImageRegistry();
		ImageDescriptor id = imageDescriptors.get(key);
		if (id != null)
			return id;
		return ImageDescriptor.getMissingImageDescriptor();
	}

	protected static void initializeImageRegistry() {
		imageRegistry = new ImageRegistry();
		imageDescriptors = new HashMap<String, ImageDescriptor>();

		registerImage(IMG_JBOSS_SERVER, URL_SERVER + "jboss_server.png");
		registerImage(IMG_GENERAL_SERVER, URL_SERVER + "general_server.gif");
		registerImage(IMG_JETTY_SERVER, URL_SERVER + "jetty_server.png");
		registerImage(IMG_NEW_SERVER, URL_SERVER + "add_server.png");
		registerImage(IMG_SERVERS_VIEW, URL_SERVER + "servers_view.gif");
		registerImage(IMG_NEW_SERVER_WIZ, URL_SERVER + "general_server.png");

		registerImage(IMG_JAVA_PROJECT, URL_PROJECT + "java_project.png");
		registerImage(IMG_NEW_PROJECT, URL_PROJECT + "project.png");
		registerImage(IMG_JEJU_PROJECT, URL_PROJECT + "jeju_project.png");
		registerImage(IMG_JEJU_PROJECT_LAYER, URL_PROJECT
				+ "jeju_project_layer.png");

		registerImage(IMG_LAUNCH_STOP, URL_LAUNCH + "stop.gif");
		registerImage(IMG_LAUNCH_START, URL_LAUNCH + "run.gif");
		registerImage(IMG_LAUNCH_DEBUG, URL_LAUNCH + "debug.gif");

		registerImage(IMG_SERVICE_SERVER, URL_SERVICE + "service_server.png");

		registerImage(IMG_DIAGNOSE, URL_EDITOR + "digg.png");
		registerImage(IMG_CREATEMODULE, URL_EDITOR + "createmodule.gif");
		registerImage(IMG_MAIN_CONFIG_FILE, URL_EDITOR + "mainconfigfile.gif");
		registerImage(IMG_CLEAR_CACHE, URL_EDITOR + "clearcache.png");

	}

	public final static String IMG_JBOSS_SERVER = "jboss";
	public final static String IMG_GENERAL_SERVER = "general_server";
	public final static String IMG_JETTY_SERVER = "jetty";
	public final static String IMG_NEW_SERVER = "new_server";
	public final static String IMG_SERVERS_VIEW = "servers_view";
	public final static String IMG_NEW_SERVER_WIZ = "new_server_wiz";

	public final static String IMG_JAVA_PROJECT = "java_project";
	public final static String IMG_NEW_PROJECT = "new_project";
	public final static String IMG_JEJU_PROJECT = "jeju_project";
	public final static String IMG_JEJU_PROJECT_LAYER = "jeju_project_layer";

	public final static String IMG_LAUNCH_STOP = "stop";
	public final static String IMG_LAUNCH_START = "start";
	public final static String IMG_LAUNCH_DEBUG = "debug";

	public final static String IMG_SERVICE_SERVER = "service_server";

	public final static String IMG_DIAGNOSE = "diagnose";
	public final static String IMG_CREATEMODULE = "createmodule";
	public final static String IMG_MAIN_CONFIG_FILE = "mainconfigfile";
	public final static String IMG_CLEAR_CACHE = "clearcache";

	private static void registerImage(String key, String partialURL) {
		try {
			ImageDescriptor id = ImageDescriptor.createFromURL(new URL(
					ICON_BASE_URL, partialURL));
			imageRegistry.put(key, id);
			imageDescriptors.put(key, id);
		} catch (Exception e) {
			StudioCommonPlugin.logError(e);
		}
	}

}
