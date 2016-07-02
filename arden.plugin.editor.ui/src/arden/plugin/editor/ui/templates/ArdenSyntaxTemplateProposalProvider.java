package arden.plugin.editor.ui.templates;

import org.eclipse.jface.text.templates.ContextTypeRegistry;
import org.eclipse.jface.text.templates.Template;
import org.eclipse.jface.text.templates.persistence.TemplateStore;
import org.eclipse.xtext.ui.editor.templates.ContextTypeIdHelper;
import org.eclipse.xtext.ui.editor.templates.DefaultTemplateProposalProvider;

import com.google.inject.Inject;

public class ArdenSyntaxTemplateProposalProvider extends DefaultTemplateProposalProvider {
	
	public static final int RELEVANCE = 10000;

	@Inject
	public ArdenSyntaxTemplateProposalProvider(TemplateStore templateStore, ContextTypeRegistry registry,
			ContextTypeIdHelper helper) {
		super(templateStore, registry, helper);
	}
	
	@Override
	public int getRelevance(Template template){
		// show templates before keywords in content assist popup
		return RELEVANCE;
	}

}
