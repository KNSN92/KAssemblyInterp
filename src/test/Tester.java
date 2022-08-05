package test;

import java.io.File;
import java.io.IOException;

import assemblylang.Engine;
import assemblylang.util.StringQuotationUtil;

public class Tester {

	public static void main(String[] args) {
		debugPrint();
		//runTestScript();
	}
	
	private static int[] runTestScript() {
		Engine engine = new Engine(8);
		int[] result = new int[0];
		engine.addReg("OPR");
		try {
			result = engine.run(new File("src/test/testcode.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return result;
	}
	
	private static void debugPrint() {
		System.out.println(StringQuotationUtil.isStringFormat("\"teNst\""));
	}

}
