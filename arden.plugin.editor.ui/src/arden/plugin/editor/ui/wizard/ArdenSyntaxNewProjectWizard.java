package arden.plugin.editor.ui.wizard;

import org.eclipse.xtext.ui.wizard.DefaultProjectInfo;
import org.eclipse.xtext.ui.wizard.IProjectCreator;
import org.eclipse.xtext.ui.wizard.IProjectInfo;
import org.eclipse.xtext.ui.wizard.XtextNewProjectWizard;

import com.google.inject.Inject;

public class ArdenSyntaxNewProjectWizard extends XtextNewProjectWizard {

	private ArdenSyntaxNewProjectCreationPage mainPage;

	@Inject
	public ArdenSyntaxNewProjectWizard(IProjectCreator projectCreator) {
		super(projectCreator);
		setWindowTitle("New Arden Syntax Project");
	}

	protected ArdenSyntaxNewProjectCreationPage getMainPage() {
		return mainPage;
	}

	@Override
	public void addPages() {
		mainPage = createMainPage("basicNewProjectPage");
		mainPage.setTitle("Arden Syntax Project");
		mainPage.setDescription("Create a new ArdenSyntax project.");
		addPage(mainPage);
	}

	protected ArdenSyntaxNewProjectCreationPage createMainPage(String pageName) {
		return new ArdenSyntaxNewProjectCreationPage(pageName);
	}

	@Override
	protected IProjectInfo getProjectInfo() {
//		IProjectInfo projectInfo = new IProjectInfo() {
//			String projectName;
//			
//			@Override
//			public void setProjectName(String projectName) {
//				this.projectName = projectName;
//			}
//			
//			@Override
//			public String getProjectName() {
//				return projectName;
//			}
//		};
//		projectInfo.setProjectName(mainPage.getProjectName());
		DefaultProjectInfo projectInfo = new DefaultProjectInfo();
		projectInfo.setProjectName(mainPage.getProjectName());
		if (!mainPage.useDefaults()) {
			projectInfo.setLocationPath(mainPage.getLocationPath());
		}
		return projectInfo;
	}

}
