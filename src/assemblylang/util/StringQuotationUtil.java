package assemblylang.util;

public final class StringQuotationUtil {
	
	public static final String DOUBLE_QUOTATION_STR = "\\\"";
	public static final String Single_QUOTATION_STR = "\\\'";
	
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
	
}
