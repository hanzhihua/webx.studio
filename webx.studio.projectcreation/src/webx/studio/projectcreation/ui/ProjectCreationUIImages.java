package webx.studio.projectcreation.ui;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;

/**
 *
 * @author zhhan
 */
public class ProjectCreationUIImages {

	private static final String ICON_PATH_PREFIX = "icons/";

	private static URL ICON_BASE_URL = null;

	private static final String NAME_PREFIX = ProjectCreationPlugin.PLUGIN_ID + '.';

	private static final int NAME_PREFIX_LENGTH = NAME_PREFIX.length();
	static {
		try {
			ICON_BASE_URL = new URL(ProjectCreationPlugin.getDefault()
					.getBundle().getEntry("/"), ICON_PATH_PREFIX);
		} catch (MalformedURLException e) {
			ProjectCreationPlugin.logThrowable(e);
		}
	}

	private final static Map<String, ImageDescriptor> imageDescriptors = new HashMap<String, ImageDescriptor>();

	public static final String IMG_WIZ_PROJECT = NAME_PREFIX + "webx3.png";

	private final static String WIZDIR = "";

	public static final ImageDescriptor DESC_WIZ_PROJECT = createManaged(
			WIZDIR, IMG_WIZ_PROJECT);

	public static void initializeImageRegistry(ImageRegistry registry) {
		for (String key : imageDescriptors.keySet()) {
			registry.put(key, imageDescriptors.get(key));
		}
	}

	public static Image getImage(String key) {
		return ProjectCreationPlugin.getDefault().getImageRegistry().get(key);
	}

	private static ImageDescriptor createManaged(String prefix, String name) {
		try {
			ImageDescriptor result = ImageDescriptor
					.createFromURL(makeIconFileURL(prefix, name
							.substring(NAME_PREFIX_LENGTH)));
			imageDescriptors.put(name, result);
			return result;
		} catch (MalformedURLException e) {
			ProjectCreationPlugin.logThrowable(e);
			return ImageDescriptor.getMissingImageDescriptor();
		}
	}

	private static URL makeIconFileURL(String prefix, String name)
			throws MalformedURLException {
		if (ICON_BASE_URL == null) {
			throw new MalformedURLException();
		}
		String str;
		if (StringUtils.isBlank(prefix)) {
			str = name;
		} else {
			str = prefix + "/" + name;
		}
		return new URL(ICON_BASE_URL, str);
	}

}
