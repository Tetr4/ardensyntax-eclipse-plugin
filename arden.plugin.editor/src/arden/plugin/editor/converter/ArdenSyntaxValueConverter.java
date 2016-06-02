package arden.plugin.editor.converter;

import org.eclipse.xtext.common.services.DefaultTerminalConverters;
import org.eclipse.xtext.conversion.IValueConverter;
import org.eclipse.xtext.conversion.ValueConverter;
import org.eclipse.xtext.conversion.ValueConverterException;
import org.eclipse.xtext.nodemodel.INode;

/**
 * Convert the string values of terminals 
 * @see https://wiki.eclipse.org/Xtext/Documentation/ValueConverter
 */
public class ArdenSyntaxValueConverter extends DefaultTerminalConverters {
	
	private static IValueConverter<String> oneWaySlotConverter = new IValueConverter<String>() {

		@Override
		public String toValue(String string, INode node) throws ValueConverterException {
			if(string == null || string.isEmpty()) {
				return "";
			}
			// "title: My MLM;;" -> "My MLM"
			return string.substring(string.indexOf(':')+1, string.length()-2).trim();
		}

		@Override
		public String toString(String value) throws ValueConverterException {
			throw new ValueConverterException("Backwards conversion not implemented", null, null);
		}
		
	};
	
	@ValueConverter(rule = "TITLE_TERMINAL")
	public IValueConverter<String> title_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "MLMNAME_TERMINAL")
	public IValueConverter<String> mlmname_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "FILENAME_TERMINAL")
	public IValueConverter<String> filename_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "INSTITUTION_TERMINAL")
	public IValueConverter<String> institution_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "AUTHOR_TERMINAL")
	public IValueConverter<String> author_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "SPECIALIST_TERMINAL")
	public IValueConverter<String> specialist_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "PURPOSE_TERMINAL")
	public IValueConverter<String> purpose_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "EXPLANATION_TERMINAL")
	public IValueConverter<String> explanation_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "KEYWORDS_TERMINAL")
	public IValueConverter<String> keywords_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "CITATIONS_TERMINAL")
	public IValueConverter<String> citations_terminal() {
		return oneWaySlotConverter;
	}
	@ValueConverter(rule = "LINKS_TERMINAL")
	public IValueConverter<String> links_terminal() {
		return oneWaySlotConverter;
	}
	
}
