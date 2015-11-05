package arden.xtext.ui.syntaxcoloring;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
    
    private static List<String> slotTerminals = Arrays.asList(
            "TITLE_SLOT_TERMINAL",
            "INSTITUTION_SLOT_TERMINAL", 
            "AUTHOR_SLOT_TERMINAL",
            "SPECIALIST_SLOT_TERMINAL",
            "PURPOSE_SLOT_TERMINAL",
            "EXPLANATION_SLOT_TERMINAL",
            "KEYWORDS_SLOT_TERMINAL",
            "CITATIONS_SLOT_TERMINAL",
            "LINKS_SLOT_TERMINAL"
    );
    
    private static int[] slotTerminalLabelLengths = new int[] {
            6,
            12,
            7,
            11,
            8,
            12,
            9,
            10,
            6
    };
    
    private static Set<String> slotKeywords = new HashSet<String>(Arrays.asList(
            "mlmname:",
            "filename:",
            "arden:",
            "version:",
            "date:",
            "validation:",
            "type:",
            "priority:",
            "urgency:"
    ));

    @Override
    public void provideHighlightingFor(XtextResource resource, IHighlightedPositionAcceptor acceptor) {
        if (resource == null || resource.getParseResult() == null) {
            // parser error
            return;
        }

        INode root = resource.getParseResult().getRootNode();

        // State, which is true while between "<highlightKeyword>:" and ";;"
        // Allows slot content (even keywords) to be colored differently
        boolean inSlot = false;

        for (ILeafNode node : root.getLeafNodes()) {
            if (node.isHidden()) {
                // comment
                highlightAsComment(node, acceptor);
                continue;
            }

            EObject element = node.getGrammarElement();
            if (element instanceof Keyword) {
                // keyword
                Keyword keyword = (Keyword) element;
                if (slotKeywords.contains(keyword.getValue())) {
                    // start of slot content
                    inSlot = true;
                    highlightAsKeyword(node, acceptor);
                } else if (keyword.getValue().equals(";;")) {
                    // end of slot content
                    inSlot = false;
                    highlightAsKeyword(node, acceptor);
                } else if (inSlot) {
                    highlightAsText(node, acceptor);
                } else {
                    highlightAsKeyword(node, acceptor);
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
                    int index = slotTerminals.indexOf(name);

                    if (index != -1) {
                        // terminal for slots
                        int labelLength = slotTerminalLabelLengths[index];
                        highlightAsSlotTerminal(acceptor, node, labelLength);
                    }
                }
            }
        }
    }

    private void highlightAsSlotTerminal(IHighlightedPositionAcceptor acceptor, ILeafNode node, int labelLength) {
        // highlight "<highlightName>:"
        acceptor.addPosition(node.getOffset(), labelLength,
                ArdenSyntaxHighlightingConfiguration.KEYWORD_ID);
        // highlight slot content
        int textlength = node.getLength() - labelLength - 2;
        acceptor.addPosition(node.getOffset() + labelLength, textlength,
                ArdenSyntaxHighlightingConfiguration.TEXT_ID);
        // highlight the ";;"
        acceptor.addPosition(node.getTotalEndOffset() - 2, 2,
                ArdenSyntaxHighlightingConfiguration.KEYWORD_ID);
    }

    private void highlightAsComment(ILeafNode node, IHighlightedPositionAcceptor acceptor) {
        acceptor.addPosition(node.getOffset(), node.getLength(), ArdenSyntaxHighlightingConfiguration.COMMENT_ID);
    }
    
    private void highlightAsKeyword(ILeafNode node, IHighlightedPositionAcceptor acceptor) {
        acceptor.addPosition(node.getOffset(), node.getLength(), ArdenSyntaxHighlightingConfiguration.KEYWORD_ID);
    }
    
    private void highlightAsText(ILeafNode node, IHighlightedPositionAcceptor acceptor) {
        acceptor.addPosition(node.getOffset(), node.getLength(), ArdenSyntaxHighlightingConfiguration.TEXT_ID);
    }
   

}
