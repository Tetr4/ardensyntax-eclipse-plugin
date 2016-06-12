package arden.plugin.editor.ui.syntaxcoloring;

import java.util.regex.Pattern;

import org.eclipse.xtext.ide.editor.syntaxcoloring.HighlightingStyles;
import org.eclipse.xtext.ui.editor.syntaxcoloring.DefaultAntlrTokenToAttributeIdMapper;

public class ArdenSyntaxAntlrTokenToAttributeIdMapper extends DefaultAntlrTokenToAttributeIdMapper{
	
	private static final Pattern KEYWORD = Pattern.compile("'.+'");
	private static final Pattern SLOT_TERMINAL = Pattern.compile("RULE_.+_TERMINAL");
	
	@Override
	protected String calculateId(String tokenName, int tokenType) {
		if("RULE_STRING_LITERAL".equals(tokenName) || "RULE_TERM".equals(tokenName)) {
			return HighlightingStyles.STRING_ID;
		}
		if("RULE_NUMBER_LITERAL".equals(tokenName)) {
			return HighlightingStyles.NUMBER_ID;
		}
		if("RULE_ML_COMMENT".equals(tokenName) || "RULE_SL_COMMENT".equals(tokenName)) {
			return HighlightingStyles.COMMENT_ID;
		}
		if("RULE_THE".equals(tokenName) || KEYWORD.matcher(tokenName).matches() || SLOT_TERMINAL.matcher(tokenName).matches()) {
			return HighlightingStyles.KEYWORD_ID;
		}
		return HighlightingStyles.DEFAULT_ID;
	}

}
