package webx.studio.server;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

/**
 * @author zhihua.hanzh
 *
 */
public class Trace {

	public static final byte CONFIG = 0;
	public static final byte INFO = 1;
	public static final byte WARNING = 2;
	public static final byte SEVERE = 3;
	public static final byte FINEST = 4;
	public static final byte FINER = 5;
	public static final byte PERFORMANCE = 6;
	public static final byte EXTENSION_POINT = 7;

	private static final String[] levelNames = new String[] {
		"CONFIG ", "INFO   ", "WARNING", "SEVERE ", "FINER  ", "FINEST ", "PERF   ", "EXTENSION"};

	private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yy HH:mm.ss.SSS");

	private static Set<String> logged = new HashSet<String>();

	private Trace() {
		super();
	}

	public static void trace(byte level, String s) {
		trace(level, s, null);
	}

	public static void traceError(String s){
		traceError(s,null);
	}

	public static void traceError(String s,Throwable t){
		trace(SEVERE,s,t);
	}

	public static void traceError(Throwable t){
		traceError(t.getMessage(),t);
	}

	public static void trace(byte level, String s, Throwable t) {
		if (s == null)
			return;

		if (level == SEVERE) {
			if (!logged.contains(s)) {
				ServerPlugin.getInstance().getLog().log(new Status(IStatus.ERROR, ServerPlugin.PLUGIN_ID, s, t));
//				logged.add(s);
			}
		}

		if (!ServerPlugin.getInstance().isDebugging())
			return;

		StringBuffer sb = new StringBuffer(ServerPlugin.PLUGIN_ID);
		sb.append(" ");
		sb.append(levelNames[level]);
		sb.append(" ");
		sb.append(sdf.format(new Date()));
		sb.append(" ");
		sb.append(s);
		System.out.println(sb.toString());
		if (t != null)
			t.printStackTrace();
	}
}