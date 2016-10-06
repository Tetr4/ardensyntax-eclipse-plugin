package arden.plugin.compiler.library;

import java.io.IOException;
import java.net.URL;

import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jdt.core.IAccessRule;
import org.eclipse.jdt.core.IClasspathContainer;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;

/**
 * Container that allows adding Arden2ByteCode.jar to the classpath.
 */
public class ArdenClasspathContainer implements IClasspathContainer {

	private final IPath containerPath;
	private IClasspathEntry[] compilerClassPaths;

	ArdenClasspathContainer(IPath containerPath) {
		this.containerPath = containerPath;
	}

	@Override
	public IClasspathEntry[] getClasspathEntries() {
		if (compilerClassPaths == null) {
			IClasspathEntry entry = null;
			try {
				entry = getCompilerJarEntry();
			} catch (IOException e) {
				compilerClassPaths = new IClasspathEntry[] {};
			}
			if (entry == null) {
				compilerClassPaths = new IClasspathEntry[] {};
			} else {
				compilerClassPaths = new IClasspathEntry[] { entry };
			}
		}

		return compilerClassPaths;
	}

	private IClasspathEntry getCompilerJarEntry() throws IOException {
		String jarName = "arden2bytecode.jar";
		String compilerPluginId = "arden.plugin.compiler";
		URL compilerEntry = Platform.getBundle(compilerPluginId).getEntry(jarName);
		if (compilerEntry != null) {
			URL compilerUrl = FileLocator.toFileURL(compilerEntry);
			Path jarFilePath = new Path(compilerUrl.getPath());
			return JavaCore.newLibraryEntry(jarFilePath, jarFilePath, null, new IAccessRule[] {}, null, false);
		}
		return null;
	}

	@Override
	public String getDescription() {
		return "Arden2ByteCode";
	}

	@Override
	public int getKind() {
		return IClasspathContainer.K_APPLICATION;
	}

	@Override
	public IPath getPath() {
		return containerPath;
	}

}
