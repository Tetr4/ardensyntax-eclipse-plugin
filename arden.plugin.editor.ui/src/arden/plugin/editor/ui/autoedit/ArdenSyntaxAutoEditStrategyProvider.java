package arden.plugin.editor.ui.autoedit;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.jface.text.IDocument;
import org.eclipse.xtext.ui.editor.autoedit.DefaultAutoEditStrategyProvider;

public class ArdenSyntaxAutoEditStrategyProvider extends DefaultAutoEditStrategyProvider {
	
	// Automatically enter closing terminal after entering an opening terminal,
	// e.g. add an "endif" after typing "then".
	// Also indents correctly when entering a newline while between them.
	private static final Map<String, String> TERMINALS = new HashMap<>();
	static {
		TERMINALS.put("data:", ";;");
		TERMINALS.put("evoke:", ";;");
		TERMINALS.put("logic:", ";;");
		TERMINALS.put("action:", ";;");
		TERMINALS.put("do", "enddo");
		TERMINALS.put("then", "endif");
	}

	@Override
	protected void configure(IEditStrategyAcceptor acceptor) {
		super.configure(acceptor);
		
		for (Entry<String, String> entry : TERMINALS.entrySet()) {
			String left = entry.getKey();
			String right = entry.getValue();
			acceptor.accept(compoundMultiLineTerminals.newInstanceFor(left, right), IDocument.DEFAULT_CONTENT_TYPE);
			acceptor.accept(singleLineTerminals.newInstance(left, right), IDocument.DEFAULT_CONTENT_TYPE);
		}
	}

}
