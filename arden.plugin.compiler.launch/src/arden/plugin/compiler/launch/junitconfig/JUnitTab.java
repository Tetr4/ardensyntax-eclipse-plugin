package arden.plugin.compiler.launch.junitconfig;

import java.net.URL;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.IJavaLaunchConfigurationConstants;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementListSelectionDialog;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.BaseWorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.xtext.ui.XtextProjectHelper;

import arden.plugin.compiler.launch.Activator;
import arden.plugin.compiler.launch.launchconfig.MainTab;

public class JUnitTab extends AbstractLaunchConfigurationTab {
	
	public static final String JAVA_PROJECT =  IJavaLaunchConfigurationConstants.ATTR_PROJECT_NAME;
	public static final String JAVA_PROJECT_DEFAULT = "";

	Text projectText;
	Text mlmText;
	Button includeMlmsCheckbox;
	Image image;

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		setControl(composite);
		GridLayout gridLayout = new GridLayout();
		composite.setLayout(gridLayout);

		// project chooser
		Group projectGroup = new Group(composite, SWT.NONE);
		projectGroup.setText("Project:");
		projectGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout projectLayout = new GridLayout();
		projectLayout.numColumns = 2;
		projectGroup.setLayout(projectLayout);

		projectText = new Text(projectGroup, SWT.SINGLE | SWT.BORDER);
		projectText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		projectText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}

		});

		Button projectBrowseButton = createPushButton(projectGroup, "&Browse...", null);
		projectBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IProject project = chooseProject();
				if (project != null) {
					projectText.setText(project.getName());
					updateLaunchConfigurationDialog();
				}
			}
		});

		// mlm chooser
		Group mlmGroup = new Group(composite, SWT.NONE);
		mlmGroup.setText("Main MLM:");
		mlmGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout mlmLayout = new GridLayout();
		mlmLayout.numColumns = 2;
		mlmGroup.setLayout(mlmLayout);

		mlmText = new Text(mlmGroup, SWT.SINGLE | SWT.BORDER);
		mlmText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		mlmText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

		Button browseMlmButton = createPushButton(mlmGroup, "&Search...", null);
		browseMlmButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				IResource mlm = chooseMlm();
				if (mlm != null) {
					mlmText.setText(mlm.getProjectRelativePath().toString());
					updateLaunchConfigurationDialog();
				}
			}
		});

		// checkboxes for options
		Group argsGroup = new Group(composite, SWT.NONE);
		argsGroup.setText("Additional options:");
		argsGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout argsLayout = new GridLayout();
		argsLayout.numColumns = 2;
		argsGroup.setLayout(argsLayout);

		includeMlmsCheckbox = new Button(argsGroup, SWT.CHECK);
		includeMlmsCheckbox.setText("Add all MLMs in project to classpath");
		includeMlmsCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		includeMlmsCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});

	}

	private IProject chooseProject() {
		// Dialog to select the project
		ILabelProvider labelProvider = new WorkbenchLabelProvider();
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), labelProvider);
		dialog.setTitle("Project Selection");
		dialog.setMessage("Select the Project containing the file you want to run.");
		dialog.setMultipleSelection(false);

		// Only show opened Xtext projects
		IProject[] projects = getValidProjects(ResourcesPlugin.getWorkspace());
		dialog.setElements(projects);

		// Initial choosen project from textfield
		IProject project = findProject(projects, projectText.getText().trim());
		if (project != null) {
			dialog.setInitialSelections(new Object[] { project });
		}

		if (dialog.open() == Window.OK) {
			return (IProject) dialog.getFirstResult();
		}
		return null;
	}

	private IProject[] getValidProjects(IWorkspace workspace) {
		IProject[] projects = workspace.getRoot().getProjects();
		// opened Xtext projects
		List<IProject> result = new LinkedList<IProject>();
		for (IProject project : projects) {
			if (project.isOpen() && XtextProjectHelper.hasNature(project)) {
				result.add(project);
			}
		}
		return result.toArray(new IProject[result.size()]);
	}

	private IProject findProject(IProject[] projects, String name) {
		for (IProject project : projects) {
			if (name.equals(project.getName())) {
				return project;
			}
		}
		return null;
	}

	private IResource chooseMlm() {
		// Dialog to select the MLM
		ILabelProvider labelProvider = new WorkbenchLabelProvider();
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(getShell(), labelProvider,
				new BaseWorkbenchContentProvider());
		dialog.setTitle("Select MLM to run");
		dialog.setMessage("Select an Medical Logic Module (MLM) to run:");
		dialog.setAllowMultiple(false);

		// Only show MLMs and folders in chosen project
		dialog.addFilter(new MLMFilter());
		String projectName = projectText.getText().trim();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		dialog.setInput(project);

		// Initial choosen mlm from textfield
		if (project != null) {
			String mlmName = mlmText.getText().trim();
			IResource mlm = project.findMember(mlmName);
			if (mlm != null) {
				dialog.setInitialSelections(new Object[] { mlm });
			}
		}

		if (dialog.open() == Window.OK) {
			return (IResource) dialog.getFirstResult();
		}
		return null;
	}

	private class MLMFilter extends ViewerFilter {
		@Override
		public boolean select(Viewer viewer, Object parentElement, Object element) {
			if (element instanceof IFile) {
				IFile f = (IFile) element;
				String ext = f.getFileExtension();
				if (Activator.MLM_EXTENSION.equalsIgnoreCase(ext) && f.exists()) {
					return true;
				}
			} else if (element instanceof IFolder) {
				return true;
			}
			return false;
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(JAVA_PROJECT, JAVA_PROJECT_DEFAULT);
		configuration.setAttribute(MainTab.MLM, MainTab.MLM_DEFAULT);
		configuration.setAttribute(MainTab.INCLUDE_ALL_MLMS, MainTab.INCLUDE_ALL_MLMS_DEFAULT);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {
		try {
			projectText.setText(configuration.getAttribute(JAVA_PROJECT, JAVA_PROJECT_DEFAULT));
			mlmText.setText(configuration.getAttribute(MainTab.MLM, MainTab.MLM_DEFAULT));
			includeMlmsCheckbox.setSelection(
					configuration.getAttribute(MainTab.INCLUDE_ALL_MLMS, MainTab.INCLUDE_ALL_MLMS_DEFAULT));
		} catch (CoreException e) {
			IStatus status = e.getStatus();
			ResourcesPlugin.getPlugin().getLog().log(status);
		}
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(JAVA_PROJECT, projectText.getText().trim());
		configuration.setAttribute(MainTab.MLM, mlmText.getText().trim());
		configuration.setAttribute(MainTab.INCLUDE_ALL_MLMS, includeMlmsCheckbox.getSelection());
	}

	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
		setErrorMessage(null);
		setMessage(null);

		// check project
		String projectName = projectText.getText().trim();
		if (projectName.isEmpty()) {
			setErrorMessage("Project not specified");
			return false;
		}
		IResource resource = ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
		if (resource == null) {
			setErrorMessage("Project not found");
			return false;
		}
		if (!resource.exists() || !(resource instanceof IProject)) {
			setErrorMessage("Invalid project");
			return false;
		}
		IProject project = (IProject) resource;
		if (!project.isOpen()) {
			setErrorMessage("Project is closed");
			return false;
		}
		if (!XtextProjectHelper.hasNature(project)) {
			setErrorMessage("Invalid project (missing Xtext nature)");
			return false;
		}
		boolean javaNature = false;
		try {
			javaNature = project.hasNature(JavaCore.NATURE_ID);
		} catch (CoreException e) {
			setErrorMessage("Could not get project nature");
			return false;
		}
		if (!javaNature) {
			setErrorMessage("Invalid project (must be a Java project)");
			return false;
		}

		// check mlm
		String mlmName = mlmText.getText().trim();
		if (mlmName.isEmpty()) {
			setErrorMessage("Main Module not specified");
			return false;
		}
		resource = project.findMember(mlmName);
		if (resource == null) {
			setErrorMessage("MLM not found");
			return false;
		}
		if (!resource.exists() || !(resource instanceof IFile)) {
			setErrorMessage("Invalid MLM file");
			return false;
		}
		IFile mlm = (IFile) resource;
		if (!mlm.getFileExtension().equalsIgnoreCase(Activator.MLM_EXTENSION)) {
			setErrorMessage("Invalid MLM file");
			return false;
		}

		return super.isValid(launchConfig);
	}

	@Override
	public String getName() {
		return "Test";
	}

	@Override
	public Image getImage() {
		if (image == null) {
			URL icon = Activator.getDefault().getBundle().getEntry("icons/julaunch.png");
			ImageDescriptor desc = ImageDescriptor.createFromURL(icon);
			image = desc.createImage();
		}
		return image;
	}

	@Override
	public void dispose() {
		if (image != null) {
			image.dispose();
		}
		super.dispose();
	}

}
