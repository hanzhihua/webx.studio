package webx.studio.utils;

import java.text.MessageFormat;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public abstract class StatusUtils {

	public static IStatus getErrorStatus(String pluginId, String message, Throwable throwable,
			Object[] messageArguments) {
		String formattedMessage = null;
		if (message != null) {
			formattedMessage = MessageFormat.format(message, messageArguments);
		}
		return new Status(Status.ERROR, pluginId, Status.ERROR, formattedMessage,
				throwable);
	}

	public static IStatus getInfoStatus(String pluginId, String message, Object[] messageArguments) {
		return new Status(Status.INFO, pluginId, Status.INFO, MessageFormat.format(message, messageArguments), null);
	}

	public static IStatus clone(IStatus status) {
		switch (status.getSeverity()) {
		case IStatus.INFO:
			return getInfoStatus(status.getPlugin(), status.getMessage(), null);
		case IStatus.ERROR:
			return getErrorStatus(status.getPlugin(), status.getMessage(), status.getException(), null);
		default:
			throw new UnsupportedOperationException("noy implemented yet!");
		}
	}


}
