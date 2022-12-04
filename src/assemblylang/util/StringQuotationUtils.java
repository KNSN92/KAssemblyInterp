package assemblylang.util;

import java.util.ArrayList;

import org.apache.commons.lang3.Validate;

import com.google.common.collect.Lists;

public final class StringQuotationUtils {
	
	public static final String DOUBLE_QUOTATION_STR = "\\\"";
	public static final String Single_QUOTATION_STR = "\\\'";
	public static final char DOUBLE_QUOTATION = '"';
	
	public static boolean isStringFormat(String str) {
		String inStr = str.trim();
		if(inStr.charAt(0)=='"' & inStr.charAt(inStr.length()-1)=='"'){
			inStr = inStr.substring(1, inStr.length()-1);
			char previousCharacter = 0;
			boolean isPreviousCharacterBackSlash = false;
			for(int i = 0; i < inStr.length(); i++) {
				switch(inStr.charAt(i)) {
				case '"':
					if(previousCharacter != '\\') {
						return false;
					}
					break;
				case '\\':
					if(!isPreviousCharacterBackSlash) {
						isPreviousCharacterBackSlash = true;
					}
				}
				previousCharacter = inStr.charAt(i);
				isPreviousCharacterBackSlash = inStr.charAt(i)=='\\';
			}
			return true;
		}
		return false;
	}

	public static boolean isInQuatations(String arg, int index) {
		Validate.notNull(arg);
		Validate.validIndex(arg, index);
	
		boolean inQuatations = false;
		for (int i = 0; i < index; i++)
			if (arg.charAt(index) == '"')
				inQuatations = !inQuatations;
	
		return inQuatations;
	}

	public static String[] splitNonQuoted(String arg, String splitChars) {
		if (splitChars.contains(DOUBLE_QUOTATION+""))
			throw new IllegalArgumentException("The argument splitChar must be a non-double quotation character.");
		char[] splitedArgs = arg.toCharArray();
		String hold = "";
		boolean inQuatations = false;
		ArrayList<String> result = Lists.newArrayList();
		for (char splitedArg : splitedArgs) {
			if (splitChars.contains(splitedArg+"")) {
				if (!inQuatations) {
					result.add(hold);
					hold = "";
					continue;
				}
			}
			if(splitedArg == DOUBLE_QUOTATION) inQuatations = !inQuatations;
			
			hold += splitedArg;
		}
		if (!hold.isEmpty())
			result.add(hold);
	
		return result.toArray(new String[0]);
	
	}
	
	public static String toStringQuotationFormat(String arg) {
		String result = "\""+arg+"\"";
		Validate.isTrue(isStringFormat(result));
		return result;
	}
	
}
