package arden.plugin.editor.tests.specification.testcompiler.impl;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.junit4.InjectWith;
import org.eclipse.xtext.junit4.util.ParseHelper;
import org.eclipse.xtext.junit4.validation.ValidationTestHelper;
import org.eclipse.xtext.validation.Issue;
import org.eclipse.xtext.xbase.lib.Extension;

import com.google.inject.Inject;

import arden.plugin.editor.ardenSyntax.mlms;
import arden.plugin.editor.tests.ArdenSyntaxInjectorProvider;
import arden.plugin.editor.tests.specification.testcompiler.ArdenVersion;
import arden.plugin.editor.tests.specification.testcompiler.TestCompiler;
import arden.plugin.editor.tests.specification.testcompiler.TestCompilerCompiletimeException;
import arden.plugin.editor.tests.specification.testcompiler.TestCompilerException;
import arden.plugin.editor.tests.specification.testcompiler.TestCompilerMappings;
import arden.plugin.editor.tests.specification.testcompiler.TestCompilerResult;
import arden.plugin.editor.tests.specification.testcompiler.TestCompilerRuntimeException;

@InjectWith(ArdenSyntaxInjectorProvider.class)
public class TestCompilerPluginImpl implements TestCompiler {
	
	@Inject
	@Extension
	ParseHelper<mlms> parseHelper;
	
	@Inject
	@Extension
	ValidationTestHelper validationHelper;
	
	public TestCompilerPluginImpl() {
		ArdenSyntaxInjectorProvider injectorProvider = new ArdenSyntaxInjectorProvider();
		injectorProvider.getInjector().injectMembers(this);
		injectorProvider.setupRegistry();
	}

	@Override
	public boolean isRuntimeSupported() {
		return false;
	}

	@Override
	public boolean isVersionSupported(ArdenVersion version) {
		// backwards compatible to all versions up to v2.5
		return version.ordinal() <= ArdenVersion.V2_5.ordinal();
	}

	@Override
	public void compile(String code) throws TestCompilerCompiletimeException {
		try {
			mlms ast = parseHelper.parse(code);
			List<Issue> issues = validationHelper.validate(ast);
			
			List<Issue> severeIssues = new ArrayList<>();
			for(Issue issue:issues) {
				if(issue.getSeverity() == Severity.WARNING && issue.getCode().equals(Diagnostic.LINKING_DIAGNOSTIC)) {
					// skip warnings for unresolved variables			
				} else {
					severeIssues.add(issue);
				}
			}
			if (!severeIssues.isEmpty()) {
				StringBuilder issuesBuilder = new StringBuilder();
				for (Issue issue : severeIssues) {
					issuesBuilder.append(issue.toString());
					issuesBuilder.append('\n');
				}
				throw new TestCompilerCompiletimeException(issuesBuilder.toString());
			}
		} catch (Exception e) {
			throw new TestCompilerCompiletimeException(e);
		}
		
	}

	@Override
	public TestCompilerResult compileAndRun(String code) throws TestCompilerException {
		throw new TestCompilerRuntimeException("Runtime not supported");
	}

	@Override
	public TestCompilerMappings getMappings() {
		return new TestCompilerMappings("INTERFACE MAPPING", "EVENT MAPPING", "MESSAGE MAPPING", 
				"DESTINATION MAPPING", "READ MAPPING", "READ MULTIPLE MAPPING");
	}

}
