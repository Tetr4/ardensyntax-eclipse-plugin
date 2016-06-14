package arden.plugin.editor.ui.wizard;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.dialogs.WizardNewFileCreationPage;

/**
 * The "New" wizard page allows setting the container for the new file as well
 * as the file name. The page will only accept file name without the extension
 * OR with the extension that matches the expected one (mlm).
 */
public class ArdenSyntaxNewFileCreationPage extends WizardNewFileCreationPage {

	public ArdenSyntaxNewFileCreationPage(String pageName, ISelection selection) {
		super(pageName, getStructuredSelection(selection));
	}
	
	private static IStructuredSelection getStructuredSelection(ISelection selection) {
		if (selection != null && selection.isEmpty() == false && selection instanceof IStructuredSelection) {
			return (IStructuredSelection) selection;
		}
		return StructuredSelection.EMPTY;
	}
	
	@Override
	protected InputStream getInitialContents() {
		String contents = ArdenSyntaxNewProjectInitialContents.EMPTY_MLM;
		return new ByteArrayInputStream(contents.getBytes());
	}

}