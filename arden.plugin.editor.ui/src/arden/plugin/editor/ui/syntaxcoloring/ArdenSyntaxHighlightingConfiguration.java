package arden.plugin.editor.ui.syntaxcoloring;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfiguration;
import org.eclipse.xtext.ui.editor.syntaxcoloring.IHighlightingConfigurationAcceptor;
import org.eclipse.xtext.ui.editor.utils.TextStyle;

public class ArdenSyntaxHighlightingConfiguration implements
        IHighlightingConfiguration {

    public static final String KEYWORD_ID = "keyword";
    public static final String COMMENT_ID = "comment";
    public static final String TEXT_ID = "text";

    @Override
    public void configure(IHighlightingConfigurationAcceptor acceptor) {
        acceptor.acceptDefaultHighlighting(COMMENT_ID, "Comment", commentTextStyle());
        acceptor.acceptDefaultHighlighting(KEYWORD_ID, "Keyword", keywordTextStyle());
        acceptor.acceptDefaultHighlighting(TEXT_ID, "Text", textTextStyle());
        
    }

    private TextStyle textTextStyle() {
        TextStyle textStyle = new TextStyle();
        textStyle.setColor(new RGB(127, 127, 159));
        return textStyle;
    }

    private TextStyle keywordTextStyle() {
        TextStyle textStyle = new TextStyle();
        textStyle.setColor(new RGB(127, 0, 85));
        textStyle.setStyle(SWT.BOLD);
        return textStyle;
    }
    
    private TextStyle commentTextStyle() {
        TextStyle textStyle = new TextStyle();
        textStyle.setColor(new RGB(63, 127, 95));
        return textStyle;
    }
}
