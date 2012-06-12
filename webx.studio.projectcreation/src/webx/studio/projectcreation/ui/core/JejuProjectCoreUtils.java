package webx.studio.projectcreation.ui.core;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;

public class JejuProjectCoreUtils {

	public static void addJejuProjectNature(IProject project, IProgressMonitor monitor) throws CoreException {
		addProjectNature(project, JejuProjectCore.NATURE_ID,monitor);
	}

	public static void addProjectNature(IProject project, String nature,
			IProgressMonitor monitor) throws CoreException {
		if (project != null && nature != null) {
			if (!project.hasNature(nature)) {
				IProjectDescription desc = project.getDescription();
				String[] oldNatures = desc.getNatureIds();
				String[] newNatures = new String[oldNatures.length + 1];
				newNatures[0] = nature;
				if (oldNatures.length > 0) {
					System.arraycopy(oldNatures, 0, newNatures, 1,
						oldNatures.length);
				}
				desc.setNatureIds(newNatures);
				project.setDescription(desc, monitor);
//				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		}
	}

	public static void addProjectNatures(IProject project,
			String[] newNatureIds, IProgressMonitor monitor)
	throws CoreException {
		if (null == project || null == newNatureIds) {
			return;
		}

		IProjectDescription description = project.getDescription();

		String[] prevNatures = description.getNatureIds();
		String[] newNatures = new String[prevNatures.length
		                                 + newNatureIds.length];
		System.arraycopy(prevNatures, 0, newNatures, 0, prevNatures.length);

		int i = prevNatures.length;
		for (String natureId : newNatureIds) {
			if (project.hasNature(natureId)) {
				continue;
			}
			newNatures[i] = natureId;
			i = i + 1;
		}

		description.setNatureIds(newNatures);
		project.setDescription(description, monitor);
	}

	public static void removeJejuProjectNature(IProject project,IProgressMonitor monitor) throws CoreException {
		removeProjectNature(project,JejuProjectCore.NATURE_ID,monitor);
	}

	/**
	 * Removes given nature from specified project.
	 */
	public static void removeProjectNature(IProject project, String nature,
			IProgressMonitor monitor) throws CoreException {
		if (project != null && nature != null) {
			if (project.exists() && project.hasNature(nature)) {

				// now remove project nature
				IProjectDescription desc = project.getDescription();
				String[] oldNatures = desc.getNatureIds();
				String[] newNatures = new String[oldNatures.length - 1];
				int newIndex = oldNatures.length - 2;
				for (int i = oldNatures.length - 1; i >= 0; i--) {
					if (!oldNatures[i].equals(nature)) {
						newNatures[newIndex--] = oldNatures[i];
					}
				}
				desc.setNatureIds(newNatures);
				project.setDescription(desc, monitor);
//				project.refreshLocal(IResource.DEPTH_INFINITE, monitor);
			}
		}
	}

	/**
	 * Returns true if given resource's project has the given nature.
	 */
	public static boolean hasNature(IResource resource, String natureId) {
		if (resource != null && resource.isAccessible()) {
			IProject project = resource.getProject();
			if (project != null) {
				try {
					return project.hasNature(natureId);
				} catch (CoreException e) {
				}
			}
		}
		return false;
	}

	public static boolean hasJejuNature(IResource resource){
		if (resource != null && resource.isAccessible()) {
			IProject project = resource.getProject();
			if (project != null) {
				try {
					return project.hasNature(JejuProjectCore.NATURE_ID);
				} catch (CoreException e) {
				}
			}
		}
		return false;

	}

	/**
	 * Returns true if given resource's project is a Spring project.
	 */
	public static boolean isJejuProject(IResource resource) {
		return hasNature(resource, JejuProjectCore.NATURE_ID);
	}


}
