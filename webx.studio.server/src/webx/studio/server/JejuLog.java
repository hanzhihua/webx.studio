package webx.studio.server;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.eclipse.core.runtime.internal.adaptor.EclipseEnvironmentInfo;
import org.eclipse.osgi.framework.internal.core.FrameworkProperties;
import org.eclipse.osgi.framework.log.FrameworkLog;
import org.eclipse.osgi.framework.log.FrameworkLogEntry;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.FrameworkEvent;

public class JejuLog implements FrameworkLog {
	
	private File outFile;
	private static final String PASSWORD = "-password"; 
	private static final String SESSION = "!SESSION";
	private static final String ENTRY = "!ENTRY";
	private static final String SUBENTRY = "!SUBENTRY";
	private static final String MESSAGE = "!MESSAGE"; 
	private static final String STACK = "!STACK"; //$NON-NLS-1$
	private static final String LINE_SEPARATOR;
	private static final String TAB_STRING = "\t"; //$NON-NLS-1$
	private static final int DEFAULT_LOG_SIZE = 1000*5;
	private static final int DEFAULT_LOG_FILES = 10;
	private static final int LOG_SIZE_MIN = 10;
	private static final String LOG_EXT = ".log"; //$NON-NLS-1$
	private static final String BACKUP_MARK = ".bak_"; 
	
	static {
		String s = System.getProperty("line.separator"); //$NON-NLS-1$
		LINE_SEPARATOR = s == null ? "\n" : s; //$NON-NLS-1$
	}
	
	private boolean newSession = true;
	
	protected Writer writer;
	int maxLogSize = DEFAULT_LOG_SIZE; // The value is in KB.
	int maxLogFiles = DEFAULT_LOG_FILES;
	int backupIdx = 0;
	

	public JejuLog(File outFile) {
		this.outFile = outFile;
		if(!this.outFile.exists()){
			try {
				FileUtils.touch(this.outFile);
			} catch (IOException e) {
				//ignore;
			}
		}
	}
	
	private Throwable getRoot(Throwable t) {
		Throwable root = null;
		if (t instanceof BundleException)
			root = ((BundleException) t).getNestedException();
		if (t instanceof InvocationTargetException)
			root = ((InvocationTargetException) t).getTargetException();
		if (root instanceof InvocationTargetException || root instanceof BundleException) {
			Throwable deeplyNested = getRoot(root);
			if (deeplyNested != null)
				root = deeplyNested;
		}
		return root;
	}
	
	private void writeArgs(String header, String[] args) throws IOException {
		if (args == null || args.length == 0)
			return;
		write(header);
		for (int i = 0; i < args.length; i++) {
			if (i > 0 && PASSWORD.equals(args[i - 1]))
				write(" (omitted)"); //$NON-NLS-1$
			else
				write(" " + args[i]); //$NON-NLS-1$
		}
		writeln();
	}
	
	protected String getSessionTimestamp() {
		String ts = FrameworkProperties.getProperty("eclipse.startTime"); //$NON-NLS-1$
		if (ts != null) {
			try {
				return getDate(new Date(Long.parseLong(ts)));
			} catch (NumberFormatException e) {
			}
		}
		return getDate(new Date());
	}
	

	protected void writeSession() throws IOException {
		write(SESSION);
		writeSpace();
		String date = getSessionTimestamp();
		write(date);
		writeSpace();
		for (int i = SESSION.length() + date.length(); i < 78; i++) {
			write("-"); //$NON-NLS-1$
		}
		writeln();
		try {
			String key = "eclipse.buildId"; //$NON-NLS-1$
			String value = FrameworkProperties.getProperty(key, "unknown"); //$NON-NLS-1$
			writeln(key + "=" + value); //$NON-NLS-1$

			key = "java.fullversion"; //$NON-NLS-1$
			value = System.getProperty(key);
			if (value == null) {
				key = "java.version"; //$NON-NLS-1$
				value = System.getProperty(key);
				writeln(key + "=" + value); //$NON-NLS-1$
				key = "java.vendor"; //$NON-NLS-1$
				value = System.getProperty(key);
				writeln(key + "=" + value); //$NON-NLS-1$
			} else {
				writeln(key + "=" + value); //$NON-NLS-1$
			}
		} catch (Exception e) {
		}
		write("BootLoader constants: OS=" + EclipseEnvironmentInfo.getDefault().getOS()); //$NON-NLS-1$
		write(", ARCH=" + EclipseEnvironmentInfo.getDefault().getOSArch()); //$NON-NLS-1$
		write(", WS=" + EclipseEnvironmentInfo.getDefault().getWS()); //$NON-NLS-1$
		writeln(", NL=" + EclipseEnvironmentInfo.getDefault().getNL()); //$NON-NLS-1$
		writeArgs("Framework arguments: ", EclipseEnvironmentInfo.getDefault().getNonFrameworkArgs()); //$NON-NLS-1$
		writeArgs("Command-line arguments: ", EclipseEnvironmentInfo.getDefault().getCommandLineArgs()); //$NON-NLS-1$
		
		writeln("System Infomation:");
		print(new String[]{"os.name","user.name","sun.boot.class.path","java.runtime.name","java.runtime.version","java.specification.name",
				"java.specification.vendor","java.specification.version","java.vendor"});
	}
	
	public void close() {
		try {
			if (writer != null) {
				Writer tmpWriter = writer;
				writer = null;
				tmpWriter.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private void openFile() {
		if (writer == null) {
			if (outFile != null) {
				try {
					writer = logForStream( new FileOutputStream(outFile.getAbsolutePath(), true));
				} catch (IOException e) {
					writer = logForStream(System.err);
				}
			} else {
				writer = logForStream(System.err);
			}
		}
	}
	
	protected void closeFile() {
		if (outFile != null) {
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				writer = null;
			}
		}
	}
	
	public void log(FrameworkEvent frameworkEvent) {
		Bundle b = frameworkEvent.getBundle();
		Throwable t = frameworkEvent.getThrowable();
		String entry = b.getSymbolicName() == null ? b.getLocation() : b.getSymbolicName();
		int severity;
		switch (frameworkEvent.getType()) {
			case FrameworkEvent.INFO :
				severity = FrameworkLogEntry.INFO;
				break;
			case FrameworkEvent.ERROR :
				severity = FrameworkLogEntry.ERROR;
				break;
			case FrameworkEvent.WARNING :
				severity = FrameworkLogEntry.WARNING;
				break;
			default :
				severity = FrameworkLogEntry.OK;
		}
		FrameworkLogEntry logEntry = new FrameworkLogEntry(entry, severity, 0, "", 0, t, null); //$NON-NLS-1$
		log(logEntry);
	}

	public synchronized void log(FrameworkLogEntry logEntry) {
		if (logEntry == null)
			return;
		try {
			checkLogFileSize();
			openFile();
			if (newSession) {
				writeSession();
				newSession = false;
			}
			writeLog(0, logEntry);
			writer.flush();
		} catch (Exception e) {
			System.err.println("An exception occurred while writing to the platform log:");//$NON-NLS-1$
			e.printStackTrace(System.err);
			System.err.println("Logging to the console instead.");//$NON-NLS-1$
			try {
				writer = logForStream(System.err);
				writeLog(0, logEntry);
				writer.flush();
			} catch (Exception e2) {
				System.err.println("An exception occurred while logging to the console:");//$NON-NLS-1$
				e2.printStackTrace(System.err);
			}
		} finally {
			closeFile();
		}
	}
	
	public synchronized void setWriter(Writer newWriter, boolean append) {
		setOutput(null, newWriter, append);
	}

	public synchronized void setFile(File newFile, boolean append) throws IOException {
		if (newFile != null && !newFile.equals(this.outFile)) {
			backupIdx = 0;
		}
		setOutput(newFile, null, append);
	}

	public synchronized File getFile() {
		return outFile;
	}

	public void setConsoleLog(boolean consoleLog) {
	}

	private void setOutput(File newOutFile, Writer newWriter, boolean append) {
		if (newOutFile == null || !newOutFile.equals(this.outFile)) {
			if (this.writer != null) {
				try {
					this.writer.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				this.writer = null;
			}
			File oldOutFile = this.outFile;
			this.outFile = newOutFile;
			this.writer = newWriter;
			boolean copyFailed = false;
			if (append && oldOutFile != null && oldOutFile.isFile()) {
				Reader fileIn = null;
				try {
					openFile();
					fileIn = new InputStreamReader(new FileInputStream(oldOutFile), "UTF-8"); //$NON-NLS-1$
					copyReader(fileIn, this.writer);
				} catch (IOException e) {
					copyFailed = true;
					e.printStackTrace();
				} finally {
					if (fileIn != null) {
						try {
							fileIn.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
						if (!copyFailed)
							oldOutFile.delete();
					}
					closeFile();
				}
			}
		}
	}

	private void copyReader(Reader reader, Writer aWriter) throws IOException {
		char buffer[] = new char[1024];
		int count;
		while ((count = reader.read(buffer, 0, buffer.length)) > 0) {
			aWriter.write(buffer, 0, count);
		}
	}

	protected String getDate(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		StringBuffer sb = new StringBuffer();
		appendPaddedInt(c.get(Calendar.YEAR), 4, sb).append('-');
		appendPaddedInt(c.get(Calendar.MONTH) + 1, 2, sb).append('-');
		appendPaddedInt(c.get(Calendar.DAY_OF_MONTH), 2, sb).append(' ');
		appendPaddedInt(c.get(Calendar.HOUR_OF_DAY), 2, sb).append(':');
		appendPaddedInt(c.get(Calendar.MINUTE), 2, sb).append(':');
		appendPaddedInt(c.get(Calendar.SECOND), 2, sb).append('.');
		appendPaddedInt(c.get(Calendar.MILLISECOND), 3, sb);
		return sb.toString();
	}

	private StringBuffer appendPaddedInt(int value, int pad, StringBuffer buffer) {
		pad = pad - 1;
		if (pad == 0)
			return buffer.append(Integer.toString(value));
		int padding = (int) Math.pow(10, pad);
		if (value >= padding)
			return buffer.append(Integer.toString(value));
		while (padding > value && padding > 1) {
			buffer.append('0');
			padding = padding / 10;
		}
		buffer.append(value);
		return buffer;
	}

	protected String getStackTrace(Throwable t) {
		if (t == null)
			return null;

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);

		t.printStackTrace(pw);
		// ensure the root exception is fully logged
		Throwable root = getRoot(t);
		if (root != null) {
			pw.println("Root exception:"); //$NON-NLS-1$
			root.printStackTrace(pw);
		}
		return sw.toString();
	}

	protected Writer logForStream(OutputStream output) {
		try {
			return new BufferedWriter(new OutputStreamWriter(output, "UTF-8")); //$NON-NLS-1$
		} catch (UnsupportedEncodingException e) {
			return new BufferedWriter(new OutputStreamWriter(output));
		}
	}

	protected void writeLog(int depth, FrameworkLogEntry entry) throws IOException {
		writeEntry(depth, entry);
		writeMessage(entry);
		writeStack(entry);

		FrameworkLogEntry[] children = entry.getChildren();
		if (children != null) {
			for (int i = 0; i < children.length; i++) {
				writeLog(depth + 1, children[i]);
			}
		}
	}

	protected void writeEntry(int depth, FrameworkLogEntry entry) throws IOException {
		if (depth == 0) {
			writeln(); // write a blank line before all !ENTRY tags bug #64406
			write(ENTRY);
		} else {
			write(SUBENTRY);
			writeSpace();
			write(Integer.toString(depth));
		}
		writeSpace();
		write(entry.getEntry());
		writeSpace();
		write(Integer.toString(entry.getSeverity()));
		writeSpace();
		write(Integer.toString(entry.getBundleCode()));
		writeSpace();
		write(getDate(new Date()));
		writeln();
	}

	protected void writeMessage(FrameworkLogEntry entry) throws IOException {
		write(MESSAGE);
		writeSpace();
		writeln(entry.getMessage());
	}

	protected void writeStack(FrameworkLogEntry entry) throws IOException {
		Throwable t = entry.getThrowable();
		if (t != null) {
			String stack = getStackTrace(t);
			write(STACK);
			writeSpace();
			write(Integer.toString(entry.getStackCode()));
			writeln();
			write(stack);
		}
	}

	protected void write(String message) throws IOException {
		if (message != null) {
			writer.write(message);
		}
	}

	protected void writeln(String s) throws IOException {
		write(s);
		writeln();
	}

	protected void writeln() throws IOException {
		write(LINE_SEPARATOR);
	}

	protected void writeSpace() throws IOException {
		write(" "); 
	}

	protected boolean checkLogFileSize() {
		if (maxLogSize == 0)
			return true; // no size limitation.

		boolean isBackupOK = true;
		if (outFile != null) {
			if ((outFile.length() >> 10) > maxLogSize) { // Use KB as file size unit.
				String logFilename = outFile.getAbsolutePath();
				String backupFilename = ""; 
				if (logFilename.toLowerCase().endsWith(LOG_EXT)) {
					backupFilename = logFilename.substring(0, logFilename.length() - LOG_EXT.length()) + BACKUP_MARK + backupIdx + LOG_EXT;
				} else {
					backupFilename = logFilename + BACKUP_MARK + backupIdx;
				}
				File backupFile = new File(backupFilename);
				if (backupFile.exists()) {
					if (!backupFile.delete()) {
						System.err.println("Error when trying to delete old log file: " + backupFile.getName());//$NON-NLS-1$ 
						if (backupFile.renameTo(new File(backupFile.getAbsolutePath() + System.currentTimeMillis()))) {
							System.err.println("So we rename it to filename: " + backupFile.getName()); //$NON-NLS-1$
						} else {
							System.err.println("And we also cannot rename it!"); //$NON-NLS-1$
							isBackupOK = false;
						}
					}
				}
				boolean isRenameOK = outFile.renameTo(backupFile);
				if (!isRenameOK) {
					System.err.println("Error when trying to rename log file to backup one."); //$NON-NLS-1$
					isBackupOK = false;
				}
				File newFile = new File(logFilename);
				setOutput(newFile, null, false);
				openFile();
				try {
					writeSession();
					writeln();
					writeln("This is a continuation of log file " + backupFile.getAbsolutePath());//$NON-NLS-1$
					writeln("Created Time: " + getDate(new Date(System.currentTimeMillis()))); //$NON-NLS-1$
					writer.flush();
				} catch (IOException ioe) {
					ioe.printStackTrace(System.err);
				}
				closeFile();
				backupIdx = (++backupIdx) % maxLogFiles;
			}
		}
		return isBackupOK;
	}

	
//	protected void writeSession() throws IOException {
//		super.writeSession();
//		writeln("System Infomation:");
//		print(new String[]{"os.name","user.name","sun.boot.class.path","java.runtime.name","java.runtime.version","java.specification.name",
//				"java.specification.vendor","java.specification.version","java.vendor"});
//	}
//
	public void print(String[] strs) throws IOException {
		for (String key : strs) {
			String value = StringUtils.trimToEmpty(EclipseEnvironmentInfo
					.getDefault().getProperty(key));
			writeln(key + "=" + value);
		}
	}

}
