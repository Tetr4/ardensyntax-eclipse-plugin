package arden.plugin.editor.ui.syntaxcoloring;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.xtext.AbstractRule;
import org.eclipse.xtext.Keyword;
import org.eclipse.xtext.RuleCall;
import org.eclipse.xtext.TerminalRule;
import org.eclipse.xtext.ide.editor.syntaxcoloring.IHighlightedPositionAcceptor;
import org.eclipse.xtext.ide.editor.syntaxcoloring.ISemanticHighlightingCalculator;
import org.eclipse.xtext.nodemodel.ILeafNode;
import org.eclipse.xtext.nodemodel.INode;
import org.eclipse.xtext.resource.XtextResource;
import org.eclipse.xtext.util.CancelIndicator;

public class ArdenSyntaxSemanticHighlightingCalculator implements ISemanticHighlightingCalculator {
    
    private static List<String> slotTerminals = Arrays.asList(
            "TITLE_TERMINAL",
            "INSTITUTION_TERMINAL",
            "MLMNAME_TERMINAL",
            "FILENAME_TERMINAL",
            "AUTHOR_TERMINAL",
            "SPECIALIST_TERMINAL",
            "PURPOSE_TERMINAL",
            "EXPLANATION_TERMINAL",
            "KEYWORDS_TERMINAL",
            "CITATIONS_TERMINAL",
            "LINKS_TERMINAL"
    );
    
    private static String scenarioTerminal = "SCENARIO_TERMINAL";
    
    private static Set<String> slotKeywords = new HashSet<String>(Arrays.asList(
            "arden:",
            "version:",
            "date:",
            "type:"
    ));
    
    private static Set<String> validationCodes = new HashSet<>(Arrays.asList(
    	    "production",
    	    "research",
    	    "testing",
    	    "expired"		
	));

    @Override
    public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor, CancelIndicator indicator) {
        if (resource == null || resource.getParseResult() == null) {
            // parser error
            return;
        }

        INode root = resource.getParseResult().getRootNode();

        // State, which is true while between "<highlightKeyword>:" and ";;"
        // Allows slot content (even keywords) to be colored differently
        boolean inSlot = false;

        for (ILeafNode node : root.getLeafNodes()) {
        	if(indicator.isCanceled()) {
        		return;
        	}
        	
            if (node.isHidden()) {
                // comment
                continue;
            }

            EObject element = node.getGrammarElement();
            if (element instanceof Keyword) {
                // keyword
                Keyword keyword = (Keyword) element;
                if (validationCodes.contains(keyword.getValue())) {
                	highlightAsText(node, acceptor);
                } else if (slotKeywords.contains(keyword.getValue())) {
                    // start of slot content
                    inSlot = true;
                } else if (keyword.getValue().equals(";;")) {
                    // end of slot content
                    inSlot = false;
                } else if (inSlot) {
                    highlightAsText(node, acceptor);
                } 
            } else if (inSlot) {
                // something between "<highlightKeyword>:" and ";;"
                highlightAsText(node, acceptor);
            } else if (element instanceof RuleCall) {
                // rule
                RuleCall rulecall = (RuleCall) element;
                AbstractRule called = rulecall.getRule();

                if (called instanceof TerminalRule) {
                    // terminal
                    TerminalRule terminalRule = (TerminalRule) called;
                    String name = terminalRule.getName();

                    if (slotTerminals.contains(name)) {
                        // terminal for slots
                        highlightAsSlotTerminal(acceptor, node, 2);
                    } else if (scenarioTerminal.equals(name)) {
                    	highlightAsSlotTerminal(acceptor, node, 1);
                    }
                }
            }
        }
    }

    private void highlightAsSlotTerminal(IHighlightedPositionAcceptor acceptor, ILeafNode node, int nrSemicolons) {
    	int labelLength = node.getText().indexOf(':')+1;
        // highlight "<highlightName>:"
        acceptor.addPosition(node.getOffset(), labelLength,
                ArdenSyntaxHighlightingConfiguration.KEYWORD_ID);
        // highlight slot content
        int textlength = node.getLength() - labelLength - nrSemicolons;
        acceptor.addPosition(node.getOffset() + labelLength, textlength,
                ArdenSyntaxHighlightingConfiguration.TASK_ID);
        // highlight the ";;" or ";"
        acceptor.addPosition(node.getTotalEndOffset() - nrSemicolons, nrSemicolons,
                ArdenSyntaxHighlightingConfiguration.KEYWORD_ID);
    }

    private void highlightAsText(ILeafNode node, IHighlightedPositionAcceptor acceptor) {
        acceptor.addPosition(node.getOffset(), node.getLength(), ArdenSyntaxHighlightingConfiguration.TASK_ID);
    }
   

}
