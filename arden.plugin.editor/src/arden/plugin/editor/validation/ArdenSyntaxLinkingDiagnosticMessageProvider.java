package arden.plugin.editor.validation;

import org.eclipse.emf.ecore.EClass;
import org.eclipse.xtext.diagnostics.Diagnostic;
import org.eclipse.xtext.diagnostics.DiagnosticMessage;
import org.eclipse.xtext.diagnostics.Severity;
import org.eclipse.xtext.linking.impl.LinkingDiagnosticMessageProvider;

public class ArdenSyntaxLinkingDiagnosticMessageProvider extends LinkingDiagnosticMessageProvider {
	
	@Override
    public DiagnosticMessage getUnresolvedProxyMessage(ILinkingDiagnosticContext context) {
		// undefined variables/references are allowed in Arden Syntax and 
		// return NULL, so change the severity from ERROR to WARNING
        EClass referenceType = context.getReference().getEReferenceType();
        String msg = "Couldn't resolve reference to " + referenceType.getName() + " '" + context.getLinkText() + "'.";
        return new DiagnosticMessage(msg, Severity.WARNING, Diagnostic.LINKING_DIAGNOSTIC);
    }
}
