package assemblylang.util;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

import com.google.common.collect.Lists;

public class CodeFormatBuilder {
	
	public final String INT = "[+-]?\\d+";
	public final String FLOAT = "^(([1-9]\\d{0,4})|0)(\\.\\d)?$";
	public final String ALPHANUMERIC = "[a-zA-Z0-9]";
	
	private final String NG = "";
	

	private String processNameNow = "";
	private List<CodeFormatUnit> val = Lists.newArrayList();
	private int codeptr = 0;

	public static CodeFormatBuilder create() {
		return new CodeFormatBuilder();
	}

	private CodeFormatBuilder() {
		val.add(CodeFormatUnit.of(CodeFormatUnit.NONE));
	}

	public CodeFormatBuilder or(String code, boolean regex, Predicate<String[]> otherCondition, Consumer<String[]> runCodes) {
		if (!processNameNow.isEmpty() & !processNameNow.equals("or"))
			throw new IllegalStateException("Until the next method is used, only the first method used can be used.");
		if(processNameNow.isEmpty()) {
			val.set(codeptr, CodeFormatUnit.of(CodeFormatUnit.OR));
		}
		val.get(codeptr).add(code, regex, otherCondition, runCodes);
		return this;
	}

	public CodeFormatBuilder and(String code, boolean regex, Predicate<String[]> otherCondition, Consumer<String[]> runCodes) {
		if (!processNameNow.isEmpty() & !processNameNow.equals("and"))
			throw new IllegalStateException("Until the next method is used, only the first method used can be used.");
		if(processNameNow.isEmpty()) {
			val.set(codeptr, CodeFormatUnit.of(CodeFormatUnit.AND));
		}
		val.get(codeptr).add(code, regex, otherCondition, runCodes);
		return this;
	}

	public CodeFormatBuilder next() {
		processNameNow = "";
		codeptr++;
		return this;
	}
	
	private static class CodeFormatUnit {
		
		public static final String NONE = "none";
		public static final String OR = "or";
		public static final String AND = "and";
		
		private String procType;
		private List<FormatUnit> formatterList = Lists.newArrayList();
		
		public static CodeFormatUnit of(String procType) {
			return new CodeFormatUnit(procType);
		}
		
		public CodeFormatUnit(String procType ) {
			this.procType = procType;
		}
		
		public void add(String code, boolean regex, Predicate<String[]> otherCondition, Consumer<String[]> runCodes) {
			this.formatterList.add(FormatUnit.of(code, regex, otherCondition, runCodes));
		}
		
		public boolean matcheType(String type) {
			return procType.equals(type);
		}
		
		
		private static class FormatUnit {
			
			String code;
			boolean regex;
			Predicate<String[]> otherCondition;
			Consumer<String[]> runCode;
			
			public static FormatUnit of(String code, boolean regex, Predicate<String[]> otherCondition, Consumer<String[]> runCode) {
				return new FormatUnit(code, regex, otherCondition, runCode);
			}
			
			private FormatUnit(String code, boolean regex, Predicate<String[]> otherCondition, Consumer<String[]> runCode) {
				this.code = code;
				this.regex = regex;
				this.otherCondition = otherCondition;
				this.runCode = runCode;
			}
			
			public boolean runnable(String arg, String[] agoCode) {
				return this.matches(arg) && this.otherConditionTest(agoCode);
			}
			
			public boolean matches(String arg) {
				return (regex ? arg.matches(code) : code.equals(arg));
			}
			
			public boolean otherConditionTest(String[] agoCode) {
				return otherCondition.test(agoCode);
			}
			
			public void run(String[] agoCode) {
				if(otherCondition != null)this.runCode.accept(agoCode);
			}
		}
	}

}
