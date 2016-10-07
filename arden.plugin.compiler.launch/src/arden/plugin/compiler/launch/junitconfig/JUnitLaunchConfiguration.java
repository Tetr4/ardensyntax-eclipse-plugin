package arden.plugin.compiler.launch.junitconfig;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.variables.IStringVariableManager;
import org.eclipse.core.variables.VariablesPlugin;
import org.eclipse.debug.core.ILaunch;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.jdt.internal.junit.JUnitCorePlugin;
import org.eclipse.jdt.internal.junit.launcher.ITestKind;
import org.eclipse.jdt.internal.junit.launcher.JUnitLaunchConfigurationConstants;
import org.eclipse.jdt.internal.junit.launcher.JUnitRuntimeClasspathEntry;
import org.eclipse.jdt.internal.junit.launcher.TestKindRegistry;
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.ExecutionArguments;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.SocketUtil;
import org.eclipse.jdt.launching.VMRunnerConfiguration;
import org.osgi.framework.Bundle;

import com.google.common.io.Files;

import arden.compiler.CompiledMlm;
import arden.compiler.Compiler;
import arden.compiler.CompilerException;
import arden.plugin.compiler.launch.Activator;
import arden.plugin.compiler.launch.launchconfig.ArgumentsTab;
import arden.plugin.compiler.launch.launchconfig.MainTab;

/** Executes a launch from a given {@link ILaunchConfiguration} */
public class JUnitLaunchConfiguration extends AbstractJavaLaunchConfigurationDelegate {

	private static CoreException abort(String message, Exception e) throws CoreException {
		return new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, 0, message, e));
	}

	private static CoreException abort(String message) throws CoreException {
		return new CoreException(new Status(IStatus.ERROR, Activator.PLUGIN_ID, message));
	}

	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask("Testing MLM...", 3);

			// project
			String projName = config.getAttribute(JUnitTab.JAVA_PROJECT, JUnitTab.JAVA_PROJECT_DEFAULT).trim();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
			if (project == null) {
				monitor.setCanceled(true);
				throw abort("Could not find that project.");
			}

			// MLM
			String mlmPath = config.getAttribute(MainTab.MLM, MainTab.MLM_DEFAULT);
			IResource mlm = project.findMember(mlmPath);
			if (mlm == null) {
				monitor.setCanceled(true);
				throw abort("Could not find that MLM in the project.");
			}
			IPath mlmLoc = mlm.getLocation();
			if (mlmLoc == null) {
				monitor.setCanceled(true);
				throw abort("Could not find that MLM in the file system.");
			}
			CompiledMlm compiledMlm = compileMLM(mlmLoc.toOSString());
			String mlmClassName = compiledMlm.getName();
			File classFile = saveMlm(compiledMlm, mlmClassName);

			// working dir
			String workingDirectoryWithVars = config.getAttribute(ArgumentsTab.WORKING_DIRECTORY,
					"${project_loc:/" + projName + "}");
			IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
			String workingDirectory = variableManager.performStringSubstitution(workingDirectoryWithVars);

			// classpath
			Set<String> paths = new HashSet<>();
			paths.add(classFile.getParent());
			Collections.addAll(paths, getClasspath(config));
			boolean includeAllMlms = config.getAttribute(MainTab.INCLUDE_ALL_MLMS, MainTab.INCLUDE_ALL_MLMS_DEFAULT);
			if (includeAllMlms) {
				paths.addAll(getMlmFolders(project));
			}
			List<String> userPaths = config.getAttribute(IJavaLaunchConfigurationConstants.ATTR_CLASSPATH,
					Collections.<String>emptyList());
			paths.addAll(userPaths);
			String[] classpath = paths.toArray(new String[paths.size()]);

			// junit args
			ExecutionArguments execArgs = new ExecutionArguments(getVMArguments(config), getProgramArguments(config));
			ArrayList<String> programArguments = new ArrayList<>();
			Collections.addAll(programArguments, execArgs.getProgramArgumentsArray());
			programArguments.add("-version");
			programArguments.add("3");
			programArguments.add("-port");
			int junitPort = SocketUtil.findFreePort();
			if (junitPort == -1) {
				monitor.setCanceled(true);
				throw abort("Not free sockets available for remote junit runner.");
			}
			launch.setAttribute(JUnitLaunchConfigurationConstants.ATTR_PORT, String.valueOf(junitPort));
			programArguments.add(String.valueOf(junitPort));
			ITestKind testRunnerKind = getTestRunnerKind();
			programArguments.add("-testLoaderClass");
			programArguments.add(testRunnerKind.getLoaderClassName());
			programArguments.add("-loaderpluginname");
			programArguments.add(testRunnerKind.getLoaderPluginId());
			programArguments.add("-classNames");
			programArguments.add(mlmClassName);

			monitor.worked(1);
			if (monitor.isCanceled()) {
				return;
			}

			// Create VM config
			String runnerType = "org.eclipse.jdt.internal.junit.runner.RemoteTestRunner";
			VMRunnerConfiguration vmConfig = new VMRunnerConfiguration(runnerType, classpath);
			vmConfig.setProgramArguments((String[]) programArguments.toArray(new String[programArguments.size()]));
			vmConfig.setVMArguments((String[]) execArgs.getVMArgumentsArray());
			vmConfig.setVMSpecificAttributesMap(getVMSpecificAttributesMap(config));
			vmConfig.setBootClassPath(getBootpath(config));
			vmConfig.setWorkingDirectory(workingDirectory);
			IVMInstall defaultVM = JavaRuntime.computeVMInstall(config);
			IVMRunner runner = defaultVM.getVMRunner(mode);
			setDefaultSourceLocator(launch, config);

			// check for cancellation
			if (monitor.isCanceled()) {
				return;
			}
			monitor.worked(1);

			// run
			if (runner != null) {
				runner.run(vmConfig, launch, monitor);
			} else {
				monitor.setCanceled(true);
			}
			monitor.done();

		} finally {
			monitor.done();
		}
	}

	private Set<String> getMlmFolders(IProject project) throws CoreException {
		final Set<String> mlmFolders = new HashSet<>();
		project.accept(new IResourceVisitor() {
			@Override
			public boolean visit(IResource resource) throws CoreException {
				// collect all folders in project which contain mlms
				if (resource.isDerived()) {
					return false;
				}
				if (resource instanceof IFile) {
					IFile file = (IFile) resource;
					if (file.getFileExtension().equals(Activator.MLM_EXTENSION)) {
						mlmFolders.add(file.getParent().getProjectRelativePath().toOSString());
					}
					return false;
				}
				return true;
			}
		});
		return mlmFolders;
	}

	private CompiledMlm compileMLM(String fileLocation) throws CoreException {
		Compiler compiler = new Compiler();
		compiler.enableDebugging(fileLocation);
		try {
			return compiler.compileMlm(new FileReader(fileLocation));
		} catch (CompilerException e) {
			throw abort("Could not compile " + fileLocation, e);
		} catch (FileNotFoundException e) {
			throw abort("File not found: " + fileLocation, e);
		} catch (IOException e) {
			throw abort("Could not read " + fileLocation, e);
		}

	}

	private File saveMlm(CompiledMlm mlm, String classname) throws CoreException {
		// write compiled MLM to temp file.
		File classFileDir = Files.createTempDir();
		classFileDir.deleteOnExit();
		File classFile = new File(classFileDir.getAbsolutePath(), classname + ".class");
		try {
			classFile.createNewFile();
			classFile.deleteOnExit();
			BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(classFile));
			mlm.saveClassFile(bos);
			bos.close();
		} catch (IOException e) {
			throw abort("Could not write compiled MLM to temporary file", e);
		}
		return classFile;
	}

	private ITestKind getTestRunnerKind() {
		return TestKindRegistry.getDefault().getKind(TestKindRegistry.JUNIT4_TEST_KIND_ID);
	}

	public String[] getClasspath(ILaunchConfiguration configuration) throws CoreException {
		String[] cp = super.getClasspath(configuration);
		List<String> junitEntries = getJUnitEntries(getTestRunnerKind());
		String arden2ByteCode = getArden2ByteCodePath();

		Set<String> classpath = new HashSet<>();
		Collections.addAll(classpath, cp);
		classpath.add(arden2ByteCode);
		classpath.addAll(junitEntries);

		return classpath.toArray(new String[classpath.size()]);
	}

	private String getArden2ByteCodePath() throws CoreException {
		String jarName = "arden2bytecode.jar";
		String compilerPluginId = "arden.plugin.compiler";
		URL compilerUrl = Platform.getBundle(compilerPluginId).getEntry(jarName);
		try {
			return FileLocator.toFileURL(compilerUrl).getFile();
		} catch (IOException e) {
			throw abort("Error locating " + jarName, e);
		}
	}

	public List<String> getJUnitEntries(ITestKind kind) throws CoreException {
		JUnitRuntimeClasspathEntry[] entries = kind.getClasspathEntries();
		boolean fInDevelopmentMode = Platform.inDevelopmentMode();

		List<String> junitEntries = new ArrayList<>();
		for (int i = 0; i < entries.length; i++) {
			try {
				String entryString = null;
				if (fInDevelopmentMode) {
					try {
						entryString = localURL(entries[i].developmentModeEntry());
					} catch (IOException e) {
						entryString = localURL(entries[i]);
					}
				} else {
					entryString = localURL(entries[i]);
				}
				if (entryString != null)
					junitEntries.add(entryString);
			} catch (IOException e) {
				throw abort(entries[i].getPluginId() + " is not available (required JAR)");
			}
		}

		return junitEntries;
	}

	private String localURL(JUnitRuntimeClasspathEntry jar) throws IOException, MalformedURLException {
		Bundle bundle = JUnitCorePlugin.getDefault().getBundle(jar.getPluginId());
		URL url;
		if (jar.getPluginRelativePath() == null) {
			url = bundle.getEntry("/");
		} else {
			url = bundle.getEntry(jar.getPluginRelativePath());
		}
		if (url == null) {
			throw new IOException();
		}
		return FileLocator.toFileURL(url).getFile();
	}

}
