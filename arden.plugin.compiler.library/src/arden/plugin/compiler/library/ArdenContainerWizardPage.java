package arden.plugin.compiler.library;

import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.ui.wizards.IClasspathContainerPage;
import org.eclipse.jdt.ui.wizards.NewElementWizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * A page to create a new Arden2ByteCode library entry. <br>
 * It can be opened via rightclicking on a Java project &rArr; Properties &rArr;
 * Java Build Path &rArr; Libraries &rArr; Add Library&hellip; &rArr;
 * Arden2ByteCode.
 */
public class ArdenContainerWizardPage extends NewElementWizardPage implements IClasspathContainerPage {

	private static final String[] USEFUL_PACKAGES = new String[] { "arden", "arden.codegenerator", "arden.compiler",
			"arden.constants", "arden.runtime" };

	private IClasspathEntry containerEntry;

	@SuppressWarnings("restriction")
	public ArdenContainerWizardPage() {
		super("ArdenClasspathContainer");
		setTitle("Arden2ByteCode");
		setImageDescriptor(org.eclipse.jdt.internal.ui.JavaPluginImages.DESC_WIZBAN_ADD_LIBRARY);
		setDescription("Add Arden2ByteCode as a library to the project.");
		containerEntry = JavaCore.newContainerEntry(ArdenClasspathContainerInitializer.LIBRARY_PATH);
	}

	@Override
	public void createControl(Composite parent) {
		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(new FillLayout());
		Label label = new Label(composite, SWT.NONE);
		StringBuilder textBuilder = new StringBuilder();
		textBuilder.append("This allows running MLMs, as well as accessing Arden2ByteCode.");
		textBuilder.append(System.lineSeparator());
		textBuilder.append(System.lineSeparator());
		textBuilder.append("The following packages are available:");
		textBuilder.append(System.lineSeparator());
		for (String bundle : USEFUL_PACKAGES) {
			textBuilder.append('\t');
			textBuilder.append(bundle);
			textBuilder.append(System.lineSeparator());
		}
		label.setText(textBuilder.toString());
		setControl(composite);
	}

	@Override
	public boolean finish() {
		return true;
	}

	@Override
	public IClasspathEntry getSelection() {
		return containerEntry;
	}

	@Override
	public void setSelection(IClasspathEntry containerEntry) {
		// do nothing
	}

}