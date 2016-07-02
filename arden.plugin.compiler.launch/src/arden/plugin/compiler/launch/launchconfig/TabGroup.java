package arden.plugin.compiler.launch.launchconfig;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

/** The tabs for the arden2bytecode run configuration ui */
public class TabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {

		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new MainTab(),
				new ArgumentsTab(),
				// new JavaClasspathTab(),
				// new JavaJRETab(),
				// new SourceLookupTab(),
				new EnvironmentTab(),
				// new CommonTab(),
		};
		setTabs(tabs);
	}

}
