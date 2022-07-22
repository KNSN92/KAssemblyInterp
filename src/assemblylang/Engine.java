package assemblylang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import assemblylang.commands.CommandADD;
import assemblylang.commands.CommandDIV;
import assemblylang.commands.CommandDSP;
import assemblylang.commands.CommandEQRL;
import assemblylang.commands.CommandEXIT;
import assemblylang.commands.CommandGOTO;
import assemblylang.commands.CommandMLT;
import assemblylang.commands.CommandMOD;
import assemblylang.commands.CommandMOV;
import assemblylang.commands.CommandSET;
import assemblylang.commands.CommandSUB;
import assemblylang.commands.CommandSWP;

public final class Engine {

	private Map<String, Integer> Regs = new HashMap<>();
	private Map<String, Boolean[]> RegSetting = new HashMap<>();
	private List<String> RegNames = new ArrayList<>();
	private Map<String, Integer> RegIDs = new HashMap<>();
	private Map<String, ICommand> commands = new HashMap<>();
	private String[] commandMultiLineCount = new String[0];
	private Map<String,Map<String,?>> allCustomValueMap = new HashMap<>();

	private String code = "";
	private String commandname = "";
	private int codeLen = 0;

	private String lastErrorMessage = "";
	private String lastErrorCode = "";
	private int lastErrorLine = 0;

	private boolean isRunningNow = false;
	private boolean isGoto = false;
	private boolean isExit = false;

	public boolean isOutError = true;

	public Engine(int regCount) {
		initRegMap(regCount);
		commandRegister();
		init();
	}

	private void commandRegister() {
		//calc
		commands.put("ADD", new CommandADD()); //addition
		commands.put("SUB", new CommandSUB());//subtraction
		commands.put("MLT", new CommandMLT());//multiplication
		commands.put("DIV", new CommandDIV());//division
		commands.put("MOD", new CommandMOD());//mod
		//display
		commands.put("DSP", new CommandDSP());//display(print)
		//register
		commands.put("SET", new CommandSET());//reg set
		commands.put("MOV", new CommandMOV());//reg value move
		commands.put("SWP", new CommandSWP());//reg value swap
		//control
		commands.put("GOTO", new CommandGOTO());//goto
		commands.put("EXIT", new CommandEXIT());//exit
		//condition
		commands.put("EQRL", new CommandEQRL());//equal
	}

	private void init() {

	}

	public int run(String code) {
		this.isRunningNow = true;
		if (code.contains("#")) {
			int hashcount = 0;
			for (char Char : code.toCharArray()) {
				if (Char == '#') {
					hashcount++;
				}
			}
			if (hashcount >= 2) {
				code = code.substring(0, code.indexOf("#")) + code.substring(code.lastIndexOf("#") + 1);
			} else {
				code = code.substring(0, code.indexOf("#"));
			}
		}
		this.code = code;
		code = code.toUpperCase();
		code = StringUtils.trim(code);
		String[] StrArr = StringUtils.split(code);
		if (!commands.containsKey(StrArr[0])) {
			boolean isFoundEndCommand = false;
			for (ICommand command : commands.values()) {
				if (command instanceof IEncloseCommand) {
					if (StrArr[0].equals(((IEncloseCommand) command).getEndEncloseCommand())
							& this.commandMultiLineCount[this.commandMultiLineCount.length - 1]
									.equals(((IEncloseCommand) command).getEndEncloseCommand())) {

					}
				}
			}
		} else {
			throwError("\"" + commandname + "\"Command not found.");
		}
		commandname = StrArr[0];
		ICommand command = commands.get(commandname);
		StrArr = command.getInitResult(StrArr, this);

		if (isExit) {
			this.isRunningNow = false;
			return -1;
		}

		int[] IntArr = this.extractArgAndToIntAndCheck(StrArr, command.getNoConversionLocations());
		
		if (!ArrayUtils.contains(command.getArgCounts(), IntArr.length) &
				!(command.getArgCounts() == null)) {
			throwError("The number of arguments does not match the number of values set.", this.getReg("C"));
		}
		if (IntArr.length < command.getMinArgCount()) {
			throwError("The number of arguments does not match the number of values set.", this.getReg("C"));
		}

		if (isExit) {
			this.isRunningNow = false;
			return -1;
		}

		int result = 0;
		if (command instanceof IEncloseCommand) {
			if (commandname.equals(((IEncloseCommand) command).getEndEncloseCommand())) {
				result = ((IEncloseCommand) command).runEndEncloseCommand(IntArr, this, this.allCustomValueMap);
			} else {
				result = command.runCommand(IntArr, this, this.allCustomValueMap.get(commandname));
			}
		} else {
			result = command.runCommand(IntArr, this, this.allCustomValueMap.get(commandname));
		}

		if (isExit) {
			this.isRunningNow = false;
			return -1;
		}

		if (isGoto) {
			this.isGoto = false;
		} else {
			this.setReg("C", this.getReg("C") + 1);
		}

		if (command.getReturnRegName() == null) {
			return 0;
		} else if (this.Regs.containsKey(command.getReturnRegName())) {
			this.Regs.put(command.getReturnRegName(), result);
		}

		this.code = "";
		this.commandname = "";
		this.isGoto = false;
		this.isExit = false;
		this.isRunningNow = false;

		return result;
	}

	public int[] run(String[] codes) {
		this.codeLen = codes.length;
		int[] results = new int[codes.length];
		while (this.getReg("C") <= codes.length) {
			int result = 0;
			result = this.run(codes[this.getReg("C") - 1]);
			ArrayUtils.add(results, result);
			if (isExit) {
				break;
			}
		}
		this.codeLen = 0;
		return results;
	}

	public int[] run(File file) throws IOException {
		String[] codes = FileUtils.readLines(file, StandardCharsets.UTF_8).toArray(new String[0]);
		int[] results = this.run(codes);
		return results;
	}

	public void throwError(String errorMessage, int errorLine) {
		if (this.isOutError)
			System.out.println("Error:" + errorMessage + "\nCode:" + code + "\nLine:" + errorLine);
		this.lastErrorMessage = errorMessage;
		this.lastErrorCode = this.code;
		this.lastErrorLine = errorLine;
		this.isExit = true;
		this.isRunningNow = false;
	}

	public void throwError(String errorMessage) {
		this.throwError(errorMessage, this.getReg("C"));
	}

	public String getLastErrorMessage() {
		return this.lastErrorMessage;
	}

	public String getLastErrorCode() {
		return this.lastErrorCode;
	}

	public int getLastErrorLine() {
		return this.lastErrorLine;
	}

	public String getAllLastError() {
		return "Error:" + this.lastErrorMessage + "\nCode:" + this.lastErrorCode + "\nLine:" + this.lastErrorLine;
	}

	public void Goto(int index) {
		if (index <= this.codeLen) {
			this.setReg("C", index);
			this.isGoto = true;
		} else {
			this.throwError("The number of lines of code specified does not exist.");
		}
	}

	public void Exit() {
		this.isExit = true;
		this.isRunningNow = false;
	}

	private int[] extractArgAndToIntAndCheck(String[] strarr, int[] convlocation) {
		strarr = ArrayUtils.subarray(strarr, 1, strarr.length);
		strarr = this.replaceKeyWord(strarr, convlocation, this.commandname);

		for (int i = 0; i < strarr.length; i++) {
			if (Regs.containsKey(strarr[i])) {
				if (this.getRegReference(strarr[i])) {
					if (ArrayUtils.contains(convlocation, i)) {
						strarr[i] = Integer.toString(this.RegNames.indexOf(strarr[i]));
					} else {
						strarr[i] = Integer.toString(this.getReg(strarr[i]));
					}
				} else {

				}
			}
			if (commands.get(strarr[i]) instanceof IEncloseCommand) {
				ArrayUtils.add(this.commandMultiLineCount,
						((IEncloseCommand) this.commands.get(strarr[i])).getEndEncloseCommand());
			}
		}

		if (!isAllNum(strarr)) {
			throwError("Incorrect argument.\n" + ArrayUtils.toString(strarr));
			return new int[0];
		}
		int[] outarr = new int[0];
		for (String str : strarr) {
			outarr = ArrayUtils.add(outarr, Integer.parseInt(str));
		}
		return outarr;
	}

	private String[] replaceKeyWord(String[] strarr, int[] convlocation, String command) {
		for (int i = 0; i < strarr.length; i++) {
			if (ArrayUtils.contains(convlocation, i)) {
			} else {
				switch (strarr[i]) {
				case "TRUE":
					strarr[i] = "1";
					break;
				case "FALSE":
				case "NULL":
				case "NIL":
				case "NONE":
				case "VOID":
					strarr[i] = "0";
					break;
				}
			}
		}

		return strarr;

	}

	public void setReg(int index, int value) {
		this.setReg(this.toRegName(index), value);
	}

	public int getReg(int index) {
		return this.getReg(this.toRegName(index));
	}

	public void setReg(String index, int value) {
		if (this.getRegChange(index)) {
			this.Regs.put(index, value);
		} else {
			if (this.isRunningNow) {
				this.throwError("That register cannot be changed.");
			} else {
				throw new IllegalArgumentException("That register cannot be changed.");
			}
		}
	}

	public int getReg(String index) {
		if (this.getRegReference(index)) {
			return this.Regs.get(index);
		} else {
			if (this.isRunningNow) {
				this.throwError("Elements of this register are not available");
				return 0;
			} else {
				throw new IllegalArgumentException("Elements of this register are not available");
			}
		}
	}

	public boolean hasRegName(String str) {
		return this.Regs.containsKey(str);
	}

	public String toRegName(int regindex) {
		return this.RegNames.get(regindex);
	}

	public void setRegChange(String Reg, boolean value) {
		this.setRegSetting(Reg, 1, value);
	}

	public void setRegReference(String Reg, boolean value) {
		this.setRegSetting(Reg, 0, value);
	}

	private void setRegSetting(String Reg, int index, boolean value) {
		boolean boollist[] = ArrayUtils.toPrimitive(this.RegSetting.get(Reg));
		boollist[index] = value;
		this.RegSetting.put(Reg, ArrayUtils.toObject(boollist));
	}

	public boolean getRegChange(String Reg) {
		return this.RegSetting.get(Reg)[1];
	}

	public boolean getRegReference(String Reg) {
		return this.RegSetting.get(Reg)[0];
	}

	private boolean isAllNum(String[] strarr) {
		for (String str : strarr) {
			if (!NumberUtils.isParsable(str)) {
				return false;
			}
		}
		return true;
	}

	private void initRegMap(int size) {
		for (int i = 1; i <= size; i++) {
			this.addReg("R" + i);
		}
		this.addReg("OP");
		this.addReg("C", 1);

		this.setRegChange("OP", false);
		//this.setRegChange("C", false);
	}

	public void addReg(String RegName, int defaultValue) {
		this.Regs.put(RegName, defaultValue);
		this.RegNames.add(RegName);
		this.RegSetting.put(RegName, new Boolean[] { true, true }); //参照可能か　変更可能か
	}

	public void addReg(String RegName) {
		this.addReg(RegName, 0);
	}

	public void addCommand(String commandName, ICommand command) {
		this.commands.put(commandName, command);
	}

	public void removeCommand(String commandName) {
		this.commands.remove(commandName);
	}

	public void resetDefaultCommand() {
		this.commands.clear();
		this.commandRegister();
	}
}
