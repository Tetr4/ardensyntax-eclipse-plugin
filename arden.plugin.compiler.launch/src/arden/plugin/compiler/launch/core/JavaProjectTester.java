package arden.plugin.compiler.launch.core;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.ui.IFileEditorInput;

public class JavaProjectTester extends PropertyTester {

	private final static String PROPERTY_JAVAPROJECT = "isInJavaProject";

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (PROPERTY_JAVAPROJECT.equals(property)) {
			// get file
			IFile f;
			if (receiver instanceof IFile) {
				f = (IFile) receiver;
				if (!f.exists()) {
					return false;
				}
			} else if (receiver instanceof IFileEditorInput) {
				IFileEditorInput input = (IFileEditorInput) receiver;
				f = input.getFile();
			} else {
				return false;
			}

			// get project
			IProject project = f.getProject();
			if (project == null) {
				return false;
			}

			// check java nature
			boolean javaNature = false;
			try {
				javaNature = project.hasNature(JavaCore.NATURE_ID);
			} catch (CoreException e) {
				return false;
			}
			if (javaNature) {
				return true;
			}
		}
		return false;
	}

}
