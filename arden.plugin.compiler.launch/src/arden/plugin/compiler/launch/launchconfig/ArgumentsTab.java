package arden.plugin.compiler.launch.launchconfig;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.WorkingDirectoryBlock;
import org.eclipse.jdt.debug.ui.launchConfigurations.JavaArgumentsTab;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;

import arden.plugin.compiler.launch.Activator;

/**
 * Run configuration tab for adding program arguments, additional compiler
 * arguments and working directory.
 */
public class ArgumentsTab extends JavaArgumentsTab {
	
	public static final String WORKING_DIRECTORY = IJavaLaunchConfigurationConstants.ATTR_WORKING_DIRECTORY;
	public static final String PROGRAM_ARGUMENTS = IJavaLaunchConfigurationConstants.ATTR_PROGRAM_ARGUMENTS;
	public static final String COMPILER_ARGUMENTS = IJavaLaunchConfigurationConstants.ATTR_VM_ARGUMENTS;

	public ArgumentsTab() {
		fVMArgumentsBlock = createVMArgsBlock();
		// get working directory from all projects, not from only java projects
		fWorkingDirectoryBlock = new WorkingDirectoryBlock(WORKING_DIRECTORY,
				Activator.PLUGIN_ID+".working_directory_context") {
			
			@Override
			protected IProject getProject(ILaunchConfiguration configuration) throws CoreException {
				String projectName = configuration.getAttribute(MainTab.PROJECT, MainTab.PROJECT_DEFAULT);
				if(!projectName.isEmpty()) {
					return ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
				}
				return null;
			}
		};
	}
	
	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);

		/*
		 * Customize the JDT arguments tab by searching the group with the
		 * heading "Vm arguments" and replacing it with "Compiler arguments". We
		 * could also temporarily change
		 * LauncherMessages.JavaArgumentsTab_VM_ar_guments__6 to
		 * "Compiler arguments", though this might lead to crashes on jdt
		 * internal changes (it is not a public API).
		 */
		Control[] children = parent.getChildren();
		if (children.length > 0 && children[0] instanceof Composite) {
			// child composite
			Composite composite2 = (Composite) children[0];
			Control[] children2 = composite2.getChildren();
			if (children2.length > 1 && children2[1] instanceof Group) {
				// second group
				Group group = (Group) children2[1];
				group.setText("Compiler ar&guments:");
				updateLaunchConfigurationDialog();
			}
		}
	}
	
	

}
