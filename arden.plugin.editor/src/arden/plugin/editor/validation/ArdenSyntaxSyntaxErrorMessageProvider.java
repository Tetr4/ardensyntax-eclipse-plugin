package arden.plugin.editor.validation;

import org.antlr.runtime.EarlyExitException;
import org.antlr.runtime.RecognitionException;
import org.eclipse.xtext.nodemodel.SyntaxErrorMessage;
import org.eclipse.xtext.parser.antlr.SyntaxErrorMessageProvider;

public class ArdenSyntaxSyntaxErrorMessageProvider extends SyntaxErrorMessageProvider {
    public static final String MISSING_MODULE = "MISSING_MODULE";
    
    @Override
    public SyntaxErrorMessage getSyntaxErrorMessage(IParserErrorContext context) {
        RecognitionException exception = context.getRecognitionException();
        if (exception != null && exception.line == 0 && exception instanceof EarlyExitException) {
            return new SyntaxErrorMessage("At least one Module is required", MISSING_MODULE);
        }
            
        return super.getSyntaxErrorMessage(context);
    }
}
