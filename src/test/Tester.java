package test;

import java.io.File;
import java.io.IOException;

import assemblylang.Engine;

public class Tester {

	public static void main(String[] args) {
		//debugPrint();
		runTestScript();
		//System.exit(0);
	}
	
	@SuppressWarnings("unused")
	private static Object[] runTestScript() {
		Engine engine = new Engine();
		Object[] result = new Object[0];
		//engine.addReg("OPR");
		try {
			//result = engine.run(new File("src/test/testcode.txt"));
			//result = engine.run(new File("src/test/Fibonacci.txt"));
			//result = engine.run(new File("src/test/test_IF.txt"));
			//result = engine.run(new File("src/test/Manju.txt"));
			//result = engine.run(new File("src/test/train_bus.txt"));
			result = engine.run(new File("src/test/functiontest.txt"));
			//result = engine.run(new File("src/test/Fibonacci.txt"));
			//result = engine.run(new File("src/test/alphabet.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		//System.out.println(ArrayUtils.toString(engine.getExportValues()));
		return result;
	}
	
	@SuppressWarnings("unused")
	private static void debugPrint() {
		//System.out.println(Arrays.toString(Engine.splitNonQuoted("こんちは〜っす;\"hello !! ; !!!!!\"ほいじゃ;んじゃ;;;",";")));
		//System.out.println(ArrayUtils.contains(new EnumVarType[] {EnumVarType.Int, EnumVarType.Float, EnumVarType.String, EnumVarType.Boolean}, EnumVarType.String));
		//String arg = "f:sgsasegase:";
		//System.out.println(isNestedFunction(arg));
		//System.out.println(VariableTypeUtils.ParseType("1").toString());
		//System.out.println(StringQuotationUtils.isInQuatations("dsp \"#test\"", 7));
	}
	
	private static boolean isNestedFunction(String arg) {
		if(arg.length() < 3) return false;
		return arg.substring(0, 2).equals("f:") & arg.charAt(arg.length()-1)==':';
	}

}
