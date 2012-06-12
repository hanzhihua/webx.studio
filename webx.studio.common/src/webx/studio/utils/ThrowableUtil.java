package webx.studio.utils;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public abstract class ThrowableUtil {

	public static Throwable getRootCause(Throwable ex) {
		if (ex == null) {
			return null;
		}
		Throwable rootCause = ex;
		Throwable cause = rootCause.getCause();
		while (cause != null && cause != rootCause) {
			rootCause = cause;
			cause = cause.getCause();
		}
		return cause == null ? rootCause : cause;
	}

}
