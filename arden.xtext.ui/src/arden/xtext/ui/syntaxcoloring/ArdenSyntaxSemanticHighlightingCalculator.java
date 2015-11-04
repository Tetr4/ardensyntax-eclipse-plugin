package arden.xtext.ui.syntaxcoloring;

import java.util.Arrays;
import java.util.List;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ui.editor.syntaxcoloring.ISemanticHighlightingCalculator;

public class ArdenSyntaxSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {

    private static List<String> highlightNames = Arrays.asList(new String[]{
            "TITLE_SLOT",
            "INSTITUTION_SLOT", 
            "AUTHOR_SLOT",
            "SPECIALIST_SLOT",
            "PURPOSE_SLOT",
            "EXPLANATION_SLOT",
            "KEYWORDS_SLOT",
            "CITATIONS_SLOT",
            "LINKS_SLOT"});
    
    private static int[] highlightLengths = new int[] {
            6,
            12,
            7,
            11,
            8,
            12,
            9,
            10,
            6 };

    @Override
    public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
        if (resource == null || resource.getParseResult() == null) {
            // parser error
            return;
        }

        INode root = resource.getParseResult().getRootNode();
        
        for (ILeafNode node : root.getLeafNodes()) {
            EObject current = node.getGrammarElement();
            if (node.isHidden()) {
                // comments
                acceptor.addPosition(node.getOffset(), node.getLength(),
                  ArdenSyntaxHighlightingConfiguration.COMMENT_ID);
            }
            if (current instanceof Keyword) {
                // keywords
                acceptor.addPosition(node.getOffset(), node.getLength(),
                        ArdenSyntaxHighlightingConfiguration.KEYWORD_ID);
            } else if (current instanceof RuleCall) {
                // rules
                RuleCall rulecall = (RuleCall) current;
                AbstractRule called = rulecall.getRule();
                if (called instanceof TerminalRule) {
                    // terminals
                    TerminalRule t = (TerminalRule) called;
                    String name = t.getName();
                    int index = highlightNames.indexOf(name);
                    if (index != -1) {
                        // special terminal rules
                        acceptor.addPosition(node.getOffset(), highlightLengths[index],
                                ArdenSyntaxHighlightingConfiguration.KEYWORD_ID);
                        // also highlight the ";;" part
                        acceptor.addPosition(node.getTotalEndOffset()-2, 2,
                                ArdenSyntaxHighlightingConfiguration.KEYWORD_ID);
                    }
                }
            }
        }
    }
   

}
