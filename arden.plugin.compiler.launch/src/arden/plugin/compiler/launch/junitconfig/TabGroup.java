package arden.plugin.compiler.launch.junitconfig;

import org.eclipse.debug.ui.AbstractLaunchConfigurationTabGroup;
import org.eclipse.debug.ui.ILaunchConfigurationDialog;
import org.eclipse.debug.ui.ILaunchConfigurationTab;

import arden.plugin.compiler.launch.launchconfig.ArgumentsTab;

public class TabGroup extends AbstractLaunchConfigurationTabGroup {

	@Override
	public void createTabs(ILaunchConfigurationDialog dialog, String mode) {

		ILaunchConfigurationTab[] tabs = new ILaunchConfigurationTab[] {
				new JUnitTab(),
				new ArgumentsTab(),
				// new JavaClasspathTab(),
				// new JavaJRETab(),
				// new SourceLookupTab(),
				// new EnvironmentTab(),
				// new CommonTab(),
		};
		setTabs(tabs);
	}

}
