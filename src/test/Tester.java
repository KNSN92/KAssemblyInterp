package test;

import java.io.File;
import java.io.IOException;

import assemblylang.Engine;

public class Tester {

	public static void main(String[] args) {
		Engine engine = new Engine(8);
		engine.addReg("OPR");
		try {
			engine.run(new File("src/test/testcode.txt"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
