package arden.plugin.compiler.library;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.ClasspathContainerInitializer;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;

/**
 * Looks at a projects ".classpath" file and adds Arden2ByteCode to the
 * classpath if necessary.
 */
public class ArdenClasspathContainerInitializer extends ClasspathContainerInitializer {

	public static final Path LIBRARY_PATH = new Path("arden.plugin.compiler.CONTAINER");

	@Override
	public void initialize(final IPath containerPath, final IJavaProject project) throws CoreException {
		if (LIBRARY_PATH.equals(containerPath)) {
			IClasspathContainer container = new ArdenClasspathContainer(containerPath);
			JavaCore.setClasspathContainer(containerPath, new IJavaProject[] { project },
					new IClasspathContainer[] { container }, null);
		}
	}

}
