package arden.plugin.compiler.launch.launchconfig;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationWorkingCopy;
import org.eclipse.debug.ui.AbstractLaunchConfigurationTab;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ILabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
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
import org.eclipse.ui.model.IWorkbenchAdapter;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.eclipse.xtext.ui.XtextProjectHelper;

import arden.plugin.compiler.launch.Activator;

/**
 * The "Main" tab in the run configuration ui. <br>
 * Contains the textfields for the project and main mlm path, the database
 * driver class, and checkboxes for compiler arguments.
 */
public class MainTab extends AbstractLaunchConfigurationTab {
	
	public static final String PROJECT =  Activator.PLUGIN_ID + ".project";
	public static final String PROJECT_DEFAULT = "";
	
	public static final String MLM = Activator.PLUGIN_ID + ".mlm";
	public static final String MLM_DEFAULT = "";
	
	public static final String DB_DRIVER = Activator.PLUGIN_ID + ".dbDriver";
	public static final String DB_DRIVER_DEFAULT = "";
	
	public static final String LOGO = Activator.PLUGIN_ID + ".displayLogo";
	public static final boolean LOGO_DEFAULT = false;
	
	public static final String DAEMON = Activator.PLUGIN_ID + ".daemon";
	public static final boolean DAEMON_DEFAULT = false;
	
	public static final String VERBOSE = Activator.PLUGIN_ID + ".verbose";
	public static final boolean VERBOSE_DEFAULT = false;
	
	public static final String INCLUDE_ALL_MLMS = Activator.PLUGIN_ID + ".includeAll";
	public static final boolean INCLUDE_ALL_MLMS_DEFAULT = true;
	
	Text projectText;
	Text mlmText;
	Text dbText;
	Button logoCheckbox;
	Button daemonCheckbox;
	Button verboseCheckbox;
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
		
		// database driver class chooser
		Group dbGroup = new Group(composite, SWT.NONE);
		dbGroup.setText("Database driver class name (optional):");
		dbGroup.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		GridLayout dbLayout = new GridLayout();
		dbLayout.numColumns = 2;
		dbGroup.setLayout(dbLayout);
		
		dbText = new Text(dbGroup, SWT.SINGLE | SWT.BORDER);
		dbText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		dbText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				updateLaunchConfigurationDialog();
			}

		});
		
		Button dbBrowseButton = createPushButton(dbGroup, "S&earch...", null);
		dbBrowseButton.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				String driver = chooseDbDriver();
				if (driver != null) {
					dbText.setText(driver);
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
       		
		logoCheckbox = new Button(argsGroup, SWT.CHECK);
		logoCheckbox.setText("Display compiler logo");
		logoCheckbox.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, true, false, 2, 1));
		logoCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		verboseCheckbox = new Button(argsGroup, SWT.CHECK);
		verboseCheckbox.setText("Verbose mode");
		verboseCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		verboseCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
		daemonCheckbox = new Button(argsGroup, SWT.CHECK);
		daemonCheckbox.setText("Run daemon for scheduled events");
		daemonCheckbox.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false, 2, 1));
		daemonCheckbox.addSelectionListener(new SelectionAdapter() {
			public void widgetSelected(SelectionEvent e) {
				updateLaunchConfigurationDialog();
			}
		});
		
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
		ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
				getShell(), 
				labelProvider, 
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
			if(mlm != null) {
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

	private String chooseDbDriver() {
		// Dialog to select a db driver class
		ElementListSelectionDialog dialog = new ElementListSelectionDialog(getShell(), new TypeLabelProvider());
		dialog.setTitle("Database driver class selection"); 
		dialog.setMessage("Select the class of the database driver.");
		dialog.setMultipleSelection(false);
		
		// Only show classes in current project
		String projectName = projectText.getText().trim();
		IProject project = ResourcesPlugin.getWorkspace().getRoot().getProject(projectName);
		IType[] types = getAllTypes(project);
		dialog.setElements(types);
		
		// Initial choosen driver class from textfield
		if (project != null) {
			String dbClassName = dbText.getText().trim();
			IType type = findType(types, dbClassName);
			if(type != null) {
				dialog.setInitialSelections(new Object[] { type });
			}
		}

		if (dialog.open() == Window.OK) {		
			IType returnType = (IType) dialog.getFirstResult();
			return returnType.getFullyQualifiedName();
		}		
		return null;
	}
	
	private IType[] getAllTypes(IProject project) {
		List<IType> types = new ArrayList<>();
		try {
			if(project != null && project.hasNature(JavaCore.NATURE_ID)) {
				IJavaProject javaProject = JavaCore.create(project);
				for (IPackageFragment fragment : javaProject.getPackageFragments()) {
					for (ICompilationUnit unit : fragment.getCompilationUnits()) {
						for(IType type : unit.getTypes()) {
							types.add(type);
						}
					};
				}
			}
		} catch (CoreException e) {
			e.printStackTrace();
		}
		return types.toArray(new IType[types.size()]);
	}
	
	private IType findType(IType[] types, String dbClassName) {
		for (IType type : types) {
			if(type.getFullyQualifiedName().equals(dbClassName)) {
				return type;
			}
		}
		return null;
	}

	private static class TypeLabelProvider extends LabelProvider {
		HashMap<ImageDescriptor, Image> fImageMap = new HashMap<ImageDescriptor, Image>();
		
		@Override
		public Image getImage(Object element) {
			IType type = ((IType) element);
			IAdaptable outer = type.getDeclaringType();
			if (outer == null) {
				outer = type.getPackageFragment();
			}
			IWorkbenchAdapter adapter = outer.getAdapter(IWorkbenchAdapter.class);
			if (adapter != null) {
				ImageDescriptor descriptor = adapter.getImageDescriptor(element);
				Image image = fImageMap.get(descriptor);
				if (image == null) {
					image = descriptor.createImage();
					fImageMap.put(descriptor, image);
				}
				return image;
			}
			return null;
		}
		
		@Override
		public String getText(Object element) {
			IType type = ((IType) element);
			return type.getFullyQualifiedName();
		}
		
		@Override
		public void dispose() {
			fImageMap.clear();
			fImageMap = null;
		}
	}

	@Override
	public void setDefaults(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(PROJECT, PROJECT_DEFAULT);
		configuration.setAttribute(MLM, MLM_DEFAULT);
		configuration.setAttribute(DB_DRIVER, DB_DRIVER_DEFAULT);
		configuration.setAttribute(LOGO, LOGO_DEFAULT);
		configuration.setAttribute(VERBOSE, VERBOSE_DEFAULT);
		configuration.setAttribute(INCLUDE_ALL_MLMS, INCLUDE_ALL_MLMS_DEFAULT);
	}

	@Override
	public void initializeFrom(ILaunchConfiguration configuration) {		
		try {
			projectText.setText(configuration.getAttribute(PROJECT, PROJECT_DEFAULT));
			mlmText.setText(configuration.getAttribute(MLM, MLM_DEFAULT));
			dbText.setText(configuration.getAttribute(DB_DRIVER, DB_DRIVER_DEFAULT));
			logoCheckbox.setSelection(configuration.getAttribute(LOGO, LOGO_DEFAULT));
			daemonCheckbox.setSelection(configuration.getAttribute(DAEMON, DAEMON_DEFAULT));
			verboseCheckbox.setSelection(configuration.getAttribute(VERBOSE, VERBOSE_DEFAULT));
			includeMlmsCheckbox.setSelection(configuration.getAttribute(INCLUDE_ALL_MLMS, INCLUDE_ALL_MLMS_DEFAULT));
		} catch (CoreException e) {
			IStatus status = e.getStatus();
			ResourcesPlugin.getPlugin().getLog().log(status);
		}		
	}

	@Override
	public void performApply(ILaunchConfigurationWorkingCopy configuration) {
		configuration.setAttribute(PROJECT, projectText.getText().trim());
		configuration.setAttribute(MLM, mlmText.getText().trim());
		configuration.setAttribute(DB_DRIVER, dbText.getText().trim());
		configuration.setAttribute(LOGO, logoCheckbox.getSelection());
		configuration.setAttribute(DAEMON, daemonCheckbox.getSelection());
		configuration.setAttribute(VERBOSE, verboseCheckbox.getSelection());
		configuration.setAttribute(INCLUDE_ALL_MLMS, includeMlmsCheckbox.getSelection());
	}
	
	@Override
	public boolean isValid(ILaunchConfiguration launchConfig) {
        setErrorMessage(null);
        setMessage(null);
        
		// check project
        String projectName = projectText.getText().trim();
        if(projectName.isEmpty()) {
        	setErrorMessage("Project not specified");
        	return false;
        }
        IResource resource  = ResourcesPlugin.getWorkspace().getRoot().findMember(projectName);
        if(resource == null) {
        	setErrorMessage("Project not found");
        	return false;
        }
        if(!resource.exists() || !(resource instanceof IProject)) {
        	setErrorMessage("Invalid project");
        	return false;
        }
    	IProject project = (IProject) resource;
        if(!project.isOpen()) {
        	setErrorMessage("Project is closed");
        	return false;
        }
    	if(!XtextProjectHelper.hasNature(project)) {
        	setErrorMessage("Invalid project (missing Xtext nature)");
        	return false;
        }
        
        // check mlm
    	String mlmName = mlmText.getText().trim();
        if(mlmName.isEmpty()) {
        	setErrorMessage("Main Module not specified");
        	return false;
        }
    	resource = project.findMember(mlmName);
        if(resource == null) {
        	setErrorMessage("MLM not found");
        	return false;
        } 
        if(!resource.exists() || !(resource instanceof IFile)) {
        	setErrorMessage("Invalid MLM file");
        	return false;
        }
    	IFile mlm = (IFile) resource;
        if(!mlm.getFileExtension().equalsIgnoreCase(Activator.MLM_EXTENSION)) {
        	setErrorMessage("Invalid MLM file");
        	return false;
        }
        
        // check driver
    	String dbClassName = dbText.getText().trim();
        if(!dbClassName.isEmpty()) {
        	IType[] types = getAllTypes(project);
        	IType type = findType(types, dbClassName);
        	if(type == null) {
            	setErrorMessage("Invalid database driver class");
            	return false;
        	}
        }
        
		return super.isValid(launchConfig);
	}
	
	@Override
	public String getName() {
		return "Main";
	}

	@Override
	public Image getImage() {
		if(image == null) {
			URL icon = Activator.getDefault().getBundle().getEntry("icons/mlm.png");
			ImageDescriptor desc = ImageDescriptor.createFromURL(icon);
			image = desc.createImage();
		}
		return image;
	}
	
	@Override
	public void dispose() {
		if(image != null) {
			image.dispose();
		}
		super.dispose();
	}

}
