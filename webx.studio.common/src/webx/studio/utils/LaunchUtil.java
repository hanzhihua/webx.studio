package webx.studio.utils;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public abstract class LaunchUtil {

	public static void addAttribute(ILaunchConfiguration configuration,
			final List<String> runtimeVmArgs, String cfgAttr) throws CoreException {
		String value = configuration.getAttribute(cfgAttr, "");

		if (StringUtils.isBlank(value))
			return;
		String arg = "-D" + cfgAttr + "=" + value;
		runtimeVmArgs.add(arg);
		return;
	}

	public static void addAttribute(final List<String> runtimeVmArgs,
			String key, String value) throws CoreException {
		String arg = "-D" + key + "=" + value;
		runtimeVmArgs.add(arg);
		return;
	}

}
