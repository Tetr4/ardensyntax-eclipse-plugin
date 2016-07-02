package arden.plugin.compiler.launch.launchconfig;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.swt.widgets.Composite;

/**
 * Run configuration tab for settings environment variables (e.g. JDBC
 * connection URL)
 */
public class EnvironmentTab extends org.eclipse.debug.ui.EnvironmentTab {

	public static final String ENVIRONMENT_VARIABLES = ILaunchManager.ATTR_ENVIRONMENT_VARIABLES;

	@Override
	public void createControl(Composite parent) {
		super.createControl(parent);
		// hide unused radiobuttons to switch between replace/append environment
		appendEnvironment.setEnabled(true);
		appendEnvironment.getParent().setVisible(false);
	}

}
