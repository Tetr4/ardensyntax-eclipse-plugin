package arden.plugin.compiler.launch.launchconfig;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import org.eclipse.jdt.launching.AbstractJavaLaunchConfigurationDelegate;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jdt.launching.IRuntimeClasspathEntry;
import org.eclipse.jdt.launching.IVMInstall;
import org.eclipse.jdt.launching.IVMRunner;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jdt.launching.VMRunnerConfiguration;

import arden.plugin.compiler.launch.Activator;

/** Executes a launch from a given {@link ILaunchConfiguration} */
public class Arden2ByteCodeLaunchConfiguration extends AbstractJavaLaunchConfigurationDelegate {

	@Override
	public void launch(ILaunchConfiguration config, String mode, ILaunch launch, IProgressMonitor monitor)
			throws CoreException {
		try {
			monitor.beginTask("Running MLM...", 3);
			
			// check locations
			String projName = config.getAttribute(MainTab.PROJECT, MainTab.PROJECT_DEFAULT).trim();
			IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projName);
			if (project == null) {
				monitor.setCanceled(true);
				throw new CoreException(new Status(Status.ERROR, 
						Activator.PLUGIN_ID, 
						"Could not find that project."));
			}
			String mlmPath = config.getAttribute(MainTab.MLM, MainTab.MLM_DEFAULT);
			IResource mlm = project.findMember(mlmPath);
			if (mlm == null) {
				monitor.setCanceled(true);
				throw new CoreException(new Status(Status.ERROR, 
						Activator.PLUGIN_ID, 
						"Could not find that MLM in the project."));
			}
			
			String workingDirectoryWithVars = config.getAttribute(
					ArgumentsTab.WORKING_DIRECTORY,	"${project_loc:/" + projName + "}");
			IStringVariableManager variableManager = VariablesPlugin.getDefault().getStringVariableManager();
			String workingDirectory = variableManager.performStringSubstitution(workingDirectoryWithVars);

			// collect command line options
			List<String> cliOptions = new LinkedList<String>();
			
			String programArgumentsWithVars = config.getAttribute(ArgumentsTab.PROGRAM_ARGUMENTS, "");
			if(!programArgumentsWithVars.isEmpty()) {
				cliOptions.add("--arguments");
				String programArguments = variableManager.performStringSubstitution(programArgumentsWithVars);
				cliOptions.add(programArguments);
			}
			
			boolean logo = config.getAttribute(MainTab.LOGO, MainTab.LOGO_DEFAULT);
			if (!logo) {
				cliOptions.add("--nologo");
			}
			
			boolean daemon = config.getAttribute(MainTab.DAEMON, MainTab.DAEMON_DEFAULT);
			if (daemon) {
				cliOptions.add("--daemon");
			} else {
				cliOptions.add("--run");
			}
			
			boolean verbose = config.getAttribute(MainTab.VERBOSE, MainTab.VERBOSE_DEFAULT);
			if (verbose) {
				cliOptions.add("--verbose");
			}
			
			String dbDriver = config.getAttribute(MainTab.DB_DRIVER, MainTab.DB_DRIVER_DEFAULT);
			if (!dbDriver.isEmpty()) {
				cliOptions.add("--dbdriver");
				cliOptions.add(dbDriver);
			}
			
			Map<String, String> environmentVars = config.getAttribute(EnvironmentTab.ENVIRONMENT_VARIABLES,
					Collections.<String, String>emptyMap());
			if (!environmentVars.isEmpty()) {
				cliOptions.add("--environment");
				// join with ':'
				String separator = System.getProperty("path.separator");
				StringBuilder envVarsBuilder = new StringBuilder();
				for (Entry<String, String> entry : environmentVars.entrySet()) {
					envVarsBuilder.append(separator);
					envVarsBuilder.append(entry.getKey());
					envVarsBuilder.append(':');
					String value = variableManager.performStringSubstitution(entry.getValue());
					envVarsBuilder.append(value);
				}
				envVarsBuilder.deleteCharAt(0); // remove first seperator
				cliOptions.add(envVarsBuilder.toString());
			}
			
			List<String> paths = new ArrayList<>();
			boolean includeAllMlms = config.getAttribute(MainTab.INCLUDE_ALL_MLMS, MainTab.INCLUDE_ALL_MLMS_DEFAULT);
			if (includeAllMlms) {
				final Set<String> mlmFolders  = new HashSet<>();
				project.accept(new IResourceVisitor() {
					@Override
					public boolean visit(IResource resource) throws CoreException {
						// collect all folders in project which contain mlms
						if(resource.isDerived()) {
							return false;
						}
						if(resource instanceof IFile) {
							IFile file = (IFile) resource;
							if(file.getFileExtension().equals(Activator.MLM_EXTENSION)) {
								mlmFolders.add(file.getParent().getProjectRelativePath().toOSString());
							}
							return false;
						}
						return true;
					}
				});
				paths.addAll(mlmFolders);
			}
			List<String> userPaths = config.getAttribute(
					IJavaLaunchConfigurationConstants.ATTR_CLASSPATH, Collections.<String>emptyList());
			paths.addAll(userPaths);
			if (!paths.isEmpty()) {
				cliOptions.add("--classpath");
				StringBuilder pathBuilder = new StringBuilder();
				for (String path : paths) {
					pathBuilder.append(':');
					IResource resource = project.findMember(path);
					pathBuilder.append(resource.getLocation().toOSString());
				}
				pathBuilder.deleteCharAt(0); // remove first ':'
				
				cliOptions.add(pathBuilder.toString());
			}
			
			String compilerArguments = config.getAttribute(ArgumentsTab.COMPILER_ARGUMENTS, "");
			if (!compilerArguments.isEmpty()) {
				for (String paramWithVars : compilerArguments.split("\\s+")) { // split around whitespace
					String param = variableManager.performStringSubstitution(paramWithVars);
					cliOptions.add(param);
				}
			}
			
			IPath mlmLoc = mlm.getLocation();
			if (mlmLoc == null) {
				monitor.setCanceled(true);
				throw new CoreException(new Status(Status.ERROR, 
						Activator.PLUGIN_ID, 
						"Could not find that MLM in the file system."));
			}
			cliOptions.add(mlmLoc.toOSString());
			
			monitor.worked(1);
			if (monitor.isCanceled()) {
				return;
			}
			
			// Print to console
			System.out.print("Running " + mlm.getName() + ": arden2bytecode ");
			for (String string : cliOptions) {
				System.out.print(string + " ");
			}
			System.out.println();
			
			// prepare vm runner
			VMRunnerConfiguration vmConfig = new VMRunnerConfiguration("arden.MainClass", getClasspath(config));
			vmConfig.setProgramArguments(cliOptions.toArray(new String[cliOptions.size()]));
			vmConfig.setWorkingDirectory(workingDirectory);			
			IVMInstall defaultVM = JavaRuntime.computeVMInstall(config);
			IVMRunner runner = defaultVM.getVMRunner(mode);
			
			// add debugeventsetlistener
			//DebugPlugin.getDefault().addDebugEventListener(this);
			
			monitor.worked(1);
			
			// run
			if (runner != null) {
				runner.run(vmConfig, launch, monitor);
			} else {
				monitor.setCanceled(true);
			}
			monitor.done();
			
		} catch (CoreException e) {
			monitor.setCanceled(true);
			throw e;
		}
	}

	@Override
	public String[] getClasspath(ILaunchConfiguration config) throws CoreException {
		IRuntimeClasspathEntry[] entries = JavaRuntime.computeUnresolvedRuntimeClasspath(config);
		entries = JavaRuntime.resolveRuntimeClasspath(entries, config);
		List<String> userEntries = new ArrayList<String>(entries.length);
		
		// add arden2bytecode.jar
		String jarName = "arden2bytecode.jar";
		String compilerPluginId = "arden.plugin.compiler";
		URL compilerUrl = Platform.getBundle(compilerPluginId).getEntry(jarName);
		String compilerCp;
		try {
			compilerCp = FileLocator.toFileURL(compilerUrl).getFile();
		} catch (IOException e) {
			throw new CoreException(new Status(IStatus.ERROR, 
					Activator.PLUGIN_ID, 
					0,
					"Error locating " + jarName,
					e));
		}
		userEntries.add(compilerCp);
		
		// add other
		for (int i = 0; i < entries.length; i++) {
			if (entries[i].getClasspathProperty() != IRuntimeClasspathEntry.BOOTSTRAP_CLASSES) {
				String location = entries[i].getLocation();
				if (location != null) {
					if (!userEntries.contains(location)) {
						userEntries.add(location);
					}
				}
			}
		}
		
		return (String[]) userEntries.toArray(new String[userEntries.size()]);
	}
	
}
