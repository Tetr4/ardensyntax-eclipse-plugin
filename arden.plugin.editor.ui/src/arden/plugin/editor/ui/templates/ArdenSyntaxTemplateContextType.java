package arden.plugin.editor.ui.templates;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import org.eclipse.jface.text.templates.SimpleTemplateVariableResolver;
import org.eclipse.jface.text.templates.TemplateContext;
import org.eclipse.xtext.ui.editor.templates.XtextTemplateContextType;

public class ArdenSyntaxTemplateContextType extends XtextTemplateContextType {
	
	// Allows the "${date}" variable in templates to be formatted with dashes instead of dots
    private static class DateResolver extends SimpleTemplateVariableResolver {
 
    	protected DateResolver() {
    		super("date", "date");
    	}

    	@Override
    	protected String resolve(TemplateContext context) {
    		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
    		return df.format(new java.util.Date());
    	}
    	
    }
 
    @Override
    protected void addDefaultTemplateVariables() {
        super.addDefaultTemplateVariables();
        addResolver(new DateResolver());
    }
}
