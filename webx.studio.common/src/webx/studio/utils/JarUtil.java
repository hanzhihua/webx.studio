package webx.studio.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.JarURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;

/**
 *  @author zhihua.han@alibaba-inc.com
 *
 */
public abstract class JarUtil {

	public static InputStream getInputStream(URL url) {
		String urlString = url.toString();
		if (urlString.length() > 12 && urlString.startsWith("jar:file:") && urlString.indexOf("!/") > 9) {
			int fileIndex = urlString.indexOf("!/"); //$NON-NLS-1$
			String jarFileName = urlString.substring(9, fileIndex);
			if (fileIndex < urlString.length()) {
				String jarPath = urlString.substring(fileIndex + 1);
				return getInputStream(jarFileName, jarPath);
			}
		}

		InputStream input = null;
		JarURLConnection jarUrlConnection = null;
		try {
			URLConnection openConnection = url.openConnection();
			openConnection.setDefaultUseCaches(false);
			openConnection.setUseCaches(false);
			if (openConnection instanceof JarURLConnection) {
				jarUrlConnection = (JarURLConnection) openConnection;
				JarFile jarFile = jarUrlConnection.getJarFile();
				input = jarFile.getInputStream(jarUrlConnection.getJarEntry());
			}
			else {
				input = openConnection.getInputStream();
			}
			if (input != null) {
				return copyAndCloseStream(input);
			}
		}
		catch (FileNotFoundException e) {
		}
		catch (IOException e) {
		}
		finally {
			if (jarUrlConnection != null) {
				try {
					jarUrlConnection.getJarFile().close();
				}
				catch (IOException e) {
					// ignore
				}
				catch (IllegalStateException e) {
				}

			}
		}
		return null;
	}

	public static InputStream getInputStream(String jarFilename, String entryName) {
		if (jarFilename == null || jarFilename.length() < 1 || entryName == null || entryName.length() < 1)
			return null;

		String internalName = null;
		if (entryName.startsWith("/")) //$NON-NLS-1$
			internalName = entryName.substring(1);
		else
			internalName = entryName;

		return getCachedInputStream(jarFilename, internalName);
	}

	protected static InputStream getCachedInputStream(String jarFilename, String entryName) {
		File testFile = new File(jarFilename);
		if (!testFile.exists())
			return getInputStream(ResourcesPlugin.getWorkspace().getRoot().getFile(new Path(jarFilename)), entryName);

		InputStream cache = null;
		ZipFile jarfile = null;
		try {
			jarfile = new ZipFile(jarFilename);
		}
		catch (IOException ioExc) {
			closeJarFile(jarfile);
		}

		if (jarfile != null) {
			try {
				ZipEntry zentry = jarfile.getEntry(entryName);
				if (zentry != null) {
					InputStream entryInputStream = null;
					try {
						entryInputStream = jarfile.getInputStream(zentry);
					}
					catch (IOException ioExc) {
						// no cleanup can be done
					}

					if (entryInputStream != null) {
						int c;
						ByteArrayOutputStream buffer = null;
						if (zentry.getSize() > 0) {
							buffer = new ByteArrayOutputStream((int) zentry.getSize());
						}
						else {
							buffer = new ByteArrayOutputStream();
						}
						// array dim restriction?
						byte bytes[] = new byte[2048];
						try {
							while ((c = entryInputStream.read(bytes)) >= 0) {
								buffer.write(bytes, 0, c);
							}
							cache = new ByteArrayInputStream(buffer.toByteArray());
							closeJarFile(jarfile);
						}
						catch (IOException ioe) {
							// no cleanup can be done
						}
						finally {
							try {
								entryInputStream.close();
							}
							catch (IOException e) {
								// no cleanup can be done
							}
						}
					}
				}
			}
			finally {
				closeJarFile(jarfile);
			}
		}
		return cache;
	}

	public static void closeJarFile(ZipFile file) {
		if (file == null)
			return;
		try {
			file.close();
		}
		catch (IOException ioe) {
			// no cleanup can be done
		}
	}

	public static InputStream getInputStream(IResource jarResource, String entryName) {
		if (jarResource == null || jarResource.getType() != IResource.FILE || !jarResource.isAccessible())
			return null;

		try {
			InputStream zipStream = ((IFile) jarResource).getContents();
			return getInputStream(jarResource.getFullPath().toString(), new ZipInputStream(zipStream), entryName);
		}
		catch (CoreException e) {
			// no cleanup can be done, probably out of sync
		}

		IPath location = jarResource.getLocation();
		if (location != null) {
			return getInputStream(location.toString(), entryName);
		}
		return null;
	}

	private static InputStream getInputStream(String filename, ZipInputStream zip, String entryName) {
		InputStream result = null;
		try {
			ZipEntry z = zip.getNextEntry();
			while (z != null && !z.getName().equals(entryName)) {
				z = zip.getNextEntry();
			}
			if (z != null) {
				result = copyAndCloseStream(zip);
			}
		}
		catch (ZipException zExc) {
		}
		catch (IOException ioExc) {
			// no cleanup can be done
		}
		finally {
			closeStream(zip);
		}
		return result;
	}

	private static void closeStream(InputStream inputStream) {
		try {
			inputStream.close();
		}
		catch (IOException e) {
			// no cleanup can be done
		}
	}

	private static InputStream copyAndCloseStream(InputStream original) {
		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		InputStream cachedCopy = null;

		if (original != null) {
			int c;
			// array dim restriction?
			byte bytes[] = new byte[2048];
			try {
				while ((c = original.read(bytes)) >= 0) {
					buffer.write(bytes, 0, c);
				}
				cachedCopy = new ByteArrayInputStream(buffer.toByteArray());
				closeStream(original);
			}
			catch (IOException ioe) {
				// no cleanup can be done
			}
		}
		return cachedCopy;
	}



}
