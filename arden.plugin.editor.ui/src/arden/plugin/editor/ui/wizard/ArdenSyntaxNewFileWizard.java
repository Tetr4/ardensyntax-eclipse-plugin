package arden.plugin.editor.ui.wizard;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.wizards.newresource.BasicNewResourceWizard;

/**
 * The wizard creates one file with the extension "mlm".
 */
public class ArdenSyntaxNewFileWizard extends BasicNewResourceWizard implements INewWizard {
	private ArdenSyntaxNewFileCreationPage page;
	private ISelection selection;

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		super.init(workbench, selection);
		this.selection = selection;
		setNeedsProgressMonitor(true);
	}
	
	@Override
	public void addPages() {
		page = new ArdenSyntaxNewFileCreationPage("Arden Syntax MLM", selection);
		page.setTitle("Arden Syntax MLM");
		page.setDescription("Create a new Medical Logic Module.");
		page.setFileName("new_mlm");
		page.setFileExtension("mlm");
		addPage(page);
	}

	/**
	 * This method is called when 'Finish' button is pressed in the wizard.
	 */
	@Override
	public boolean performFinish() {
		if(page.getErrorMessage() != null){
			return false;
		}
		
		final IFile file = page.createNewFile();
		if(file == null) {
			return false;
		}
		selectAndReveal(file);
		
		IRunnableWithProgress op = new IRunnableWithProgress() {
			public void run(IProgressMonitor monitor) throws InvocationTargetException {
				try {
					monitor.beginTask("Opening file for editing..." + file.getName(), 1);
					
					// open file
					getShell().getDisplay().asyncExec(new Runnable() {
						public void run() {
							IWorkbenchPage page =
								PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
							try {
								IDE.openEditor(page, file, true);
							} catch (PartInitException e) {
							}
						}
					});
					monitor.worked(1);
				} finally {
					monitor.done();
				}
			}
		};
		
		try {
			getContainer().run(true, false, op);
		} catch (InterruptedException e) {
			return false;
		} catch (InvocationTargetException e) {
			Throwable realException = e.getTargetException();
			MessageDialog.openError(getShell(), "Error", realException.getMessage());
			return false;
		}

		return true;
	}
	
}