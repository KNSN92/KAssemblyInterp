package assemblylang;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import assemblylang.CommandMultiLine.CommandEndMultiLine;
import assemblylang.CommandMultiLine.IEndCommand;
import assemblylang.commands.CommandABS;
import assemblylang.commands.CommandADD;
import assemblylang.commands.CommandAND;
import assemblylang.commands.CommandCompare;
import assemblylang.commands.CommandDIV;
import assemblylang.commands.CommandDSP;
import assemblylang.commands.CommandEQRLGOTO;
import assemblylang.commands.CommandEXIT;
import assemblylang.commands.CommandEXPORT;
import assemblylang.commands.CommandGOTO;
import assemblylang.commands.CommandIF;
import assemblylang.commands.CommandINPUT;
import assemblylang.commands.CommandLABEL;
import assemblylang.commands.CommandMLT;
import assemblylang.commands.CommandMOD;
import assemblylang.commands.CommandMOV;
import assemblylang.commands.CommandNOT;
import assemblylang.commands.CommandOR;
import assemblylang.commands.CommandPOW;
import assemblylang.commands.CommandSELECT;
import assemblylang.commands.CommandSET;
import assemblylang.commands.CommandSUB;
import assemblylang.commands.CommandSWP;
import assemblylang.commands.CommandVAR;
import assemblylang.commands.CommandWHILE;
import assemblylang.commands.CommandXOR;
import assemblylang.commands.joke.Command0401;
import assemblylang.commands.joke.CommandHALLOWEEN;
import assemblylang.util.CmdStrUtil;
import assemblylang.util.VariableTypeUtils;

@SuppressWarnings("unused")
public final class Engine {

	public static final String DEFAULT_RETURN_REG_NAME = "OP";
	public static final int GLOBAL_SCOPE = -1;

	//datas
	private Table<Integer, String, Variable<?>> Regs = HashBasedTable.create();
	private List<Boolean> ScopeNotExecutionInfo = Lists.newArrayList();
	private List<String> MultiLineCmdHeadStr = Lists.newArrayList();
	private List<String> RegNames = Lists.newArrayList();
	private Map<String, Integer> RegIDs = Maps.newHashMap();
	private Map<String, ICommand> commands = Maps.newHashMap();

	//run first data
	private String[] codes = ArrayUtils.EMPTY_STRING_ARRAY;
	private String code = "";
	private String commandname = "";
	private int codeLen = 0;

	//error
	private String lastErrorMessage = "";
	private String lastErrorCode = "";
	private int lastErrorLine = 0;

	//run data
	private int runCount = 0;
	private int scope = -1;//global:-1 local:0 or more

	//save run data
	private int saveReg = -1;
	private String saveCode = "";
	private String saveCmdName = "";
	private int saveScope = -1;

	private boolean isRunningNow = false;
	private boolean isGoto = false;
	private boolean isExit = false;
	private boolean isInit = false;
	private boolean isRecursiveExec = false;

	public boolean isOutError = true;
	public String[] keyWordList = { "true", "false", "null", "nil", "none", "void", "const", "final" };

	/**
	 * Constructor
	 * @param Register(variable)size
	 */
	public Engine() {
		initRegMap();
		commandRegister();
		init();
	}
	
	public Engine(Engine engine) {
		Table<Integer, String, Variable<?>> Regs = HashBasedTable.create(engine.Regs);
		Regs.remove(Engine.GLOBAL_SCOPE, "C");
		this.Regs = Regs;
		
		List<String> RegNames = Lists.newArrayList(engine.RegNames);
		RegNames.remove(RegNames.indexOf("C"));
		this.RegNames = RegNames;
		
		Map<String, Integer> RegIDs = Maps.newHashMap(engine.RegIDs);
		RegIDs.remove("C");
		this.RegIDs = RegIDs;
		
		this.commands = Maps.newHashMap(engine.commands);
	}

	/**
	 * Register a CommandMap
	 */
	private void commandRegister() {
		//calc
		this.registerCommand("add", new CommandADD());//addition
		this.registerCommand("sub", new CommandSUB());//subtraction
		this.registerCommand("mlt", new CommandMLT());//multiplication
		this.registerCommand("div", new CommandDIV());//division
		this.registerCommand("mod", new CommandMOD());//mod
		this.registerCommand("abs", new CommandABS());//abs
		this.registerCommand("pow", new CommandPOW());//pow
		//CommandCompare
		this.registerCommand("eq", new CommandCompare((l,r)->l.equals(r)));//equals
		this.registerCommand("eqnot", new CommandCompare((l,r)-> !l.equals(r)));//equals not
		this.registerCommand("lt", new CommandCompare((l,r)-> l < r));//less than
		this.registerCommand("gt", new CommandCompare((l,r)-> l > r));//greater than
		this.registerCommand("lteq", new CommandCompare((l,r)-> l <= r));//less than or equal to
		this.registerCommand("gteq", new CommandCompare((l,r)-> l >= r));//less than or equal to
		//bool calc
		this.registerCommand("not", new CommandNOT());//not
		this.registerCommand("or", new CommandOR(false));//or
		this.registerCommand("and", new CommandAND(false));//and
		this.registerCommand("xor", new CommandXOR(false));//xor
		this.registerCommand("nor", new CommandOR(true));//nor
		this.registerCommand("nand", new CommandAND(true));//nand
		this.registerCommand("xnor", new CommandXOR(true));//xnor
		//register
		this.registerCommand("set", new CommandSET());//reg value set
		this.registerCommand("mov", new CommandMOV());//reg value move
		this.registerCommand("swp", new CommandSWP());//reg value swap
		this.registerCommand("var", new CommandVAR()); //new variable
		//control
		this.registerCommand("goto", new CommandGOTO());//goto
		this.registerCommand("exit", new CommandEXIT());//exit
		this.registerCommand("while", new CommandWHILE(false));//while
		this.registerCommand("until", new CommandWHILE(true));//until
		//condition
		this.registerCommand("equal", new CommandEQRLGOTO(false));//equal
		this.registerCommand("equalnot", new CommandEQRLGOTO(true));//equal not
		this.registerCommand("if", new CommandIF());//if
		//other
		this.registerCommand("dsp", new CommandDSP());//display(print)
		this.registerCommand("export", new CommandEXPORT());//export
		this.registerCommand("label", new CommandLABEL());//label
		this.registerCommand("input", new CommandINPUT());//input
		this.registerCommand("select", new CommandSELECT());//select
		//joke
		this.registerCommand("APRIL", new Command0401());
		this.registerCommand("HALLOWEEN", new CommandHALLOWEEN());

	}

	private void init() {
		ScopeNotExecutionInfo.add(true);
	}

	/**
	 * running code one line
	 * @param code
	 * @return result
	 */
	private Object[] run(String code) {

		this.isRunningNow = true;
		this.code = code;
		this.codes[this.getCodeCount() - 1] = this.code;
		
		code = cutComment(code);

		if (code.contains("\n") || code.contains(";")) {
			this.isRecursiveExec = true;
			Object[] results = this.run(splitCode(code, true));
			this.isRecursiveExec = false;
			return results;
		}

		if (isInit) {
			this.codes = ArrayUtils.add(this.codes, code);
		}

		if (code.length() <= 0) {
			this.setReg("C", GLOBAL_SCOPE, this.getCodeCount() + 1L, true);
			return new Object[0];
		}

		//code = code.toUpperCase();
		code = StringUtils.trim(code);
		String[] StrArr = this.splitCode(code, false);
		StrArr = removeBlank(StrArr);
		commandname = StrArr[0];

		if (!commands.containsKey(commandname)) {
			throwError("Command not found.\nCommandName: " + commandname);
		}

		ICommand command = commands.get(commandname);

		StrArr = ArrayUtils.subarray(StrArr, 1, StrArr.length);
		String[] lineCode = StrArr.clone();

		StrArr = replaceNestedSystem(StrArr);

		argsSizeCheck(StrArr, command);

		StrArr = command.getInitResult(StrArr, this,
				StrArr.length, isInit);

		int[] convlocation = command.getNoConversionLocations();
		//StrArr = this.replaceKeyWord(StrArr, convlocation, this.commandname);

		for (int i = 0; i < StrArr.length; i++) {
			replaceVar(StrArr, convlocation, i);
		}

		Object[] Args = new Object[0];
		EnumVarType[] types = new EnumVarType[0];

		for (int i = 0; i < StrArr.length; i++) {
			types = ArrayUtils.add(types, VariableTypeUtils.ParseType(StrArr[i]));
			Args = ArrayUtils.add(Args, VariableTypeUtils.Parse(StrArr[i]));
		}

		if (ArrayUtils.contains(types, EnumVarType.None)) {
			throwError(
					"Incorrect argument. We looked for that argument as a variable or keyword, but could not find it. \nErrorArg:"
							+ lineCode[ArrayUtils.indexOf(types, EnumVarType.None)]);
		}

		IVarType[] argTypes = command.getArgVarTypes(types, this, Args.length);
		argTypes = argsTypeCheck(types, argTypes);

		Object result = null;

		if (isInit) {
			result = initRunning(command, Args, types, result);
		} else {

			result = runProcessing(command, Args, types, argTypes, result);

			if (isGoto) {
				this.isGoto = false;
			} else {
				this.setReg("C", GLOBAL_SCOPE, this.getCodeCount() + 1L, true);
			}

			if (isExecution()) {
				if (command.getReturnRegName() == null) {
					return new Object[0];
				} else if (this.hasRegName(command.getReturnRegName())) {
					setOP(command, result);
				}
			}

		}

		this.code = "";
		this.commandname = "";
		this.isGoto = false;
		this.isExit = false;
		this.isRunningNow = false;

		return new Object[] { result };
	}



	/**
	 * running code multi line
	 * @param codes
	 * @return result
	 */
	public Object[] run(String[] codes) {
		if (isRecursiveExec) {
			this.saveReg = this.getCodeCount();
			this.saveCode = this.code;
			this.saveCmdName = this.commandname;
			this.saveScope = this.scope;
		}
		this.setReg("C", GLOBAL_SCOPE, 1L, true);
		if (!isRecursiveExec) {
			initRegMap();
			init();
			if (runCount > 0) {
				System.out.println("");
			}

			this.runCount++;
			this.isGoto = false;
			this.isExit = false;

			this.codeLen = codes.length;
			this.codes = codes;

			for (ICommand command : commands.values()) {
				command.init(this);
			}
		}

		Object[] results = new Object[0];
		results = new Object[codeLen];
		if (!isRecursiveExec)
			isInit = true;
		iar:
		for (int i = 0; i < 2; i++) {
			while (this.getCodeCount() <= codes.length) {
				try {
					if (isInit) {
						this.run(codes[this.getCodeCount() - 1]);
					} else {
						results = ArrayUtils.addAll(results, this.run(codes[this.getCodeCount() - 1]));
					}
				} catch (Exception e) {
					if (!(e instanceof KAsmException)) {
						printFatalError(e);
					} else {
						e.printStackTrace();
						break iar;
					}
				}
				if (isExit) {
					break;
				}
			}
			if (!isRecursiveExec) {
				isInit = false;
				this.initRegMap();
			}

			if (!isExit & this.scope != -1) {
				this.throwError("End commands for multiple line commands are missing.\nCommand:"
						+ this.MultiLineCmdHeadStr.get(MultiLineCmdHeadStr.size() - 1), false, true);
				break;
			}
			this.setReg("C", GLOBAL_SCOPE, 1L, true);
		}
		if (!isRecursiveExec) {
			this.codeLen = 0;
		} else {
			this.setReg("C", GLOBAL_SCOPE, this.saveReg + 1L, true);
			this.saveReg = -1;
			this.code = this.saveCode;
			this.commandname = this.saveCmdName;
			this.scope = this.saveScope;
		}

		return results;
	}



	/**
	 * running command import from text file
	 * @param file
	 * @return
	 * @throws IOException
	 */
	public Object[] run(File file) throws IOException {
		String[] codes = FileUtils.readLines(file, StandardCharsets.UTF_8).toArray(new String[0]);
		Object[] results = this.run(codes);
		return results;
	}

	/*public void throwError(String format, Object... obj) {
		System.out.printf(format, obj);
		this.lastErrorMessage = format;
		this.lastErrorCode = this.code.trim();
		this.lastErrorLine = -1;
		this.isExit = true;
		this.isRunningNow = false;
	}*/

	public void throwError(String errorMessage, int errorLine, boolean code, boolean line) {
		if (isRecursiveExec) {
			this.code = this.saveCode;
			//this.commandname = this.saveCmdName;
			this.scope = this.saveScope;
			errorLine--;
		}
		if (this.isOutError) {
			System.err.println("Error:" + errorMessage);
			System.err.println("Code:" + (code ? this.code.trim() : "None"));
			System.err.println("CmdName:" + (code ? commandname : "None"));
			System.err.println("Line:" + (line ? errorLine : "None"));
		}
		this.lastErrorMessage = errorMessage;
		this.lastErrorCode = this.code;
		this.lastErrorLine = errorLine;
		this.isExit = true;
		this.isRunningNow = false;

		throw new KAsmException();
	}

	public void throwError(String errorMessage, boolean code, boolean line) {
		int errline = this.getCodeCount();
		if (isRecursiveExec & this.saveReg != -1)
			errline = this.saveReg;
		this.throwError(errorMessage, errline, code, line);
	}

	/**
	 * throw error
	 * @param errorMessage
	 * @param errorLine
	 */
	public void throwError(String errorMessage, int errorLine) {
		this.throwError(errorMessage, errorLine, true, true);
	}

	/**
	 * throw error
	 * @param errorMessage
	 */
	public void throwError(String errorMessage) {
		int line = this.getCodeCount();
		if (isRecursiveExec & this.saveReg != -1)
			line = this.saveReg;
		this.throwError(errorMessage, line);
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

	/**
	 * get error_message + error_code + error_line string
	 * @return result
	 */
	public String getAllLastError() {
		return "Error:" + this.lastErrorMessage + "\nCode:" + this.lastErrorCode + "\nLine:" + this.lastErrorLine;
	}

	/**
	 * goto
	 * @param index
	 */
	public void Goto(int index) {
		if (index <= this.codeLen & index >= 1) {
			this.setReg("C", GLOBAL_SCOPE, (long) index, true);
			this.isGoto = true;
		} else {
			this.throwError("The number of lines of code specified does not exist.");
		}
	}

	/**
	 * exiting
	 */
	public void Exit() {
		this.isExit = true;
		this.isRunningNow = false;
	}

	/**
	 * replace_key_word True = 1 False = 0 Null = 0 Nil = 0 None = 0 Void = 0
	 * @param strarr
	 * @param convlocation
	 * @param command
	 * @return
	 */
	private String[] replaceKeyWord(String[] strarr, int[] convlocation, String command) {
		if (strarr == null)
			return strarr;
		for (int i = 0; i < strarr.length; i++) {
			if (ArrayUtils.contains(convlocation, i)) {
			} else {
				switch (strarr[i]) {
				case "null":
				case "nil":
				case "none":
					strarr[i] = "0";
					break;
				}
			}
		}

		return strarr;

	}

	/**
	 * set reg using reg index
	 * @param index
	 * @param value
	 */
	public void setReg(int index, Object value) {
		this.setReg(this.toRegName(index), value);
	}

	public void setReg(String index, Object result) {
		this.setReg(index, result, false);
	}

	public void setReg(String index, Object value, boolean enforcement) {
		setReg(index, this.scope, value, enforcement);
	}

	public void setReg(String index, int scope, Object value, boolean enforcement) {
		Validate.isTrue(-1 <= scope);
		Variable<?> Var = this.Regs.get(this.getMostNearVarScope(index), index);

		if (this.getRegChange(index, this.getMostNearVarScope(index)) || enforcement) {
			Var.setValue(value);
		} else {
			if (this.isRunningNow) {
				this.throwError("That register cannot be changed.");
			} else {
				throw new IllegalArgumentException("That register cannot be changed.");
			}
		}
	}

	/**
	 * get reg using reg index
	 * @param index
	 * @return
	 */
	public Object getReg(int index) {
		return this.getReg(this.toRegName(index), false);
	}

	public Object getReg(String index) {
		return this.getReg(index, this.scope);
	}

	public Object getReg(String index, int scope) {
		return this.getReg(index, scope, false);
	}

	public Object getReg(String index, boolean enforcement) {
		return this.getReg(index, this.scope, enforcement);
	}

	public Object getReg(String index, int scope, boolean enforcement) {
		Validate.isTrue(-1 <= scope);
		if (this.getRegReference(index, this.getMostNearVarScope(index)) || enforcement) {
			return this.Regs.get(this.getMostNearVarScope(index), index).getValue();
		} else {
			if (this.isRunningNow) {
				this.throwError("Elements of this register are not available");
				return 0;
			} else {
				throw new IllegalArgumentException("Elements of this register are not available");
			}
		}
	}

	public boolean hasRegName(String str, int scope) {
		return this.Regs.contains(scope, str);
	}

	public boolean hasRegName(String str) {
		return this.Regs.containsColumn(str);
	}

	public boolean hasRegName(int name) {
		return name >= 0 && name < this.RegNames.size();
	}

	/**
	 * reg index to reg name
	 * @param regindex
	 * @return
	 */
	public String toRegName(int regindex) {
		return this.RegNames.get(regindex);
	}

	/**
	 * set reg changeable
	 * @param Reg
	 * @param value
	 */
	public void setRegChange(String Reg, int scope, boolean value) {
		this.Regs.get(scope, Reg).setChangeable(value);
	}

	/**
	 * set reg referable
	 * @param Reg
	 * @param value
	 */
	public void setRegReference(String Reg, int scope, boolean value) {
		this.Regs.get(scope, Reg).setReferable(value);
	}

	/**
	 * get reg changeable
	 * @param Reg
	 * @param value
	 */
	public boolean getRegChange(String Reg, int scope) {
		return this.Regs.get(scope, Reg).isChangeable();
	}

	/**
	 * get reg referable
	 * @param Reg
	 * @param value
	 */
	public boolean getRegReference(String Reg, int scope) {
		return this.Regs.get(scope, Reg).isReferable();
	}

	private void initRegMap() {

		this.Regs.clear();
		this.RegNames.clear();

		this.addReg(Engine.DEFAULT_RETURN_REG_NAME, Engine.GLOBAL_SCOPE, EnumVarType.Int, 0L);
		this.setRegChange(Engine.DEFAULT_RETURN_REG_NAME, Engine.GLOBAL_SCOPE, false);

		this.addReg("C", Engine.GLOBAL_SCOPE, EnumVarType.Int, 1L);
		this.setRegChange("C", Engine.GLOBAL_SCOPE, false);
	}

	/**
	 * add new register
	 * @param RegName
	 * @param defaultValue
	 */
	public void addReg(String RegName, IVarType type, Object defaultValue) {
		this.addReg(RegName, this.scope, type, defaultValue);
	}

	public void addReg(String RegName, int scope, IVarType type, Object defaultValue) {
		this.Regs.put(scope, RegName, new Variable<>(defaultValue));
		this.RegNames.add(RegName);
	}

	public void addReg(String RegName, int scope, IVarType type) {
		this.addReg(RegName, scope, type, type.defaultVal());
	}

	/**
	 * add new register
	 * @param RegName
	 */
	public void addReg(String RegName, IVarType type) {
		this.addReg(RegName, type, type.defaultVal());
	}

	public Table<Integer, String, Variable<?>> getRegs() {
		return HashBasedTable.create(Regs);
	}

	/**
	 * register a command
	 * @param commandName
	 * @param command
	 */
	public void registerCommand(String commandName, ICommand command) {
		this.commands.put(commandName, command);
		command.registered(this);
	}

	public ICommand getCommand(String commandName) {
		return commands.get(commandName);
	}

	public Map<String, ICommand> getCommands() {
		return Maps.newHashMap(commands);
	}

	/**
	 * remove command
	 * @param commandName
	 */
	public void removeCommand(String commandName) {
		this.commands.remove(commandName);
	}

	public void clearCommands() {
		this.commands.clear();
	}

	/**
	 * set command registry default
	 */
	public void resetDefaultCommand() {
		this.commands.clear();
		this.commandRegister();
	}

	public Object[] getExportValues() {
		return commands.containsKey("EXPORT") ? ((CommandEXPORT) this.commands.get("EXPORT")).ExportInfos
				: new Object[0];
	}

	public boolean isExecution() {
		return !ScopeNotExecutionInfo.contains(false);
	}

	public boolean isLastExecution() {
		return ScopeNotExecutionInfo.get(ScopeNotExecutionInfo.size() - 1);
	}

	public void setExecution(int scope, boolean bool) {
		this.ScopeNotExecutionInfo.set(scope + 1, bool);
	}

	public int getScope() {
		return this.scope;
	}

	public int getMostNearVarScope(String varName) {
		int countScope = this.scope;
		while (!hasRegName(varName, countScope)) {
			countScope--;
			if (countScope < -1) {
				this.throwError("Variable not found.");
				return 0;
			}
		}
		return countScope;
	}

	public int getCodeCount() {
		return (int) (long) this.getReg("C");
	}

	private String[] splitCode(String arg, boolean nonNewLine) {
		char[] splitedArgs = arg.toCharArray();
		String hold = "";
		boolean inNestedBoxes = false;
		int nestedBoxesCount = 0;
		boolean inQuatations = false;
		ArrayList<String> result = Lists.newArrayList();
		for (char splitedArg : splitedArgs) {
			if (!inQuatations) {
				if (hold.length() >= 1) {
					if ((hold.charAt(hold.length()-1)+(splitedArg+"")).equals("f:")) {
						nestedBoxesCount++;
					}
				}
				if (hold.length() != 0) {
					if (splitedArg == ':' & (hold.charAt(hold.length() - 1) != 'f')) {
						nestedBoxesCount--;
					}
				}else {
					if (splitedArg == ':') {
						nestedBoxesCount--;
					}
				}
			}
			if (nestedBoxesCount < 0) {
				throwError("The number of \"f:\" or \":\" is not 1 to 1.\nresult:" + result.toString());
			}
			inNestedBoxes = (nestedBoxesCount != 0);
			//System.out.println(hold + splitedArg+"\n"+nestedBoxesCount);
			if ((nonNewLine ? " " : " \n").contains(splitedArg + "") & !inNestedBoxes) {
				if (!inQuatations) {
					result.add(hold.trim());
					hold = "";
					continue;
				}
			}
				if(splitedArg == ':' & !inNestedBoxes) {
					if (!inQuatations) {
						result.add(hold.trim()+splitedArg);
						hold = "";
						continue;
					}
				}
			
			if (splitedArg == '"')
				inQuatations = !inQuatations;
			
			hold += splitedArg;
		}
		if (!hold.isEmpty())
			result.add(hold);

		if (nestedBoxesCount != 0) {
			throwError("The number of \"f:\" or \":\" is not 1 to 1.\nresult:" + result.toString());
		}
		//System.out.println(result);
		return result.toArray(new String[0]);

	}

	private static boolean isNestedFunction(String arg) {
		if (arg.length() < 3)
			return false;
		return arg.substring(0, 2).equals("f:") & arg.charAt(arg.length() - 1) == ':';
	}
	
	
	
	/*============================== Engine Running Refactoring Methods ==============================*/
	
	
	private Object runProcessing(ICommand command, Object[] Args, IVarType[] types, IVarType[] argTypes,
			Object result) {
		if (command instanceof CommandMultiLine & !(command instanceof CommandEndMultiLine)) {
			this.ScopeNotExecutionInfo.add(true);
			this.MultiLineCmdHeadStr.add(this.commandname);
			this.scope++;
		}

		result = running(command, Args, types, argTypes, result);

		if (command instanceof IEndCommand & !(command instanceof CommandEndMultiLine)) {
			this.ScopeNotExecutionInfo.remove(ScopeNotExecutionInfo.size() - 1);
			this.MultiLineCmdHeadStr.remove(this.MultiLineCmdHeadStr.size() - 1);
			this.Regs.row(this.scope).clear();
			this.scope--;
		}

		if (command instanceof CommandEndMultiLine) {
			this.MultiLineCmdHeadStr.remove(this.MultiLineCmdHeadStr.size() - 1);
			this.MultiLineCmdHeadStr.add(this.commandname);
		}
		return result;
	}

	private Object running(ICommand command, Object[] Args, IVarType[] types, IVarType[] argTypes,
			Object result) {
		if (this.isExecution()) {
			if (command.isRunnable(Args, this, Args.length)) {
				result = command.runCommand(Args, this, argTypes, Args.length);

				returnTypeCheck(command, types, result);
			} else {
				throwError("The conditions for executing the command are not in place.");
			}
		} else {
			command.RunWhenNotExec(this);
		}
		return result;
	}

	private Object initRunning(ICommand command, Object[] Args, IVarType[] types, Object result) {
		initMultiLineCmdFieldProcessing(command);

		command.initRun(Args, this, Args.length);
		result = command.getReturnVarType(types,
				result == null ? EnumVarType.Void : VariableTypeUtils.toEnumVarType(result), this,
				runCount).defaultVal();
		this.setReg("C", GLOBAL_SCOPE, this.getCodeCount() + 1L, true);
		return result;
	}

	private void setOP(ICommand command, Object result) {
		String ReturnRegName = command.getReturnRegName();
		if (this.getReg(ReturnRegName).getClass() != result) {

			this.Regs.remove(GLOBAL_SCOPE, ReturnRegName);

			this.addReg(ReturnRegName, Engine.GLOBAL_SCOPE,
					VariableTypeUtils.toEnumVarType(result));

			this.setRegChange(ReturnRegName, Engine.GLOBAL_SCOPE, false);
		}
		this.setReg(ReturnRegName, result, true);
	}

	private void returnTypeCheck(ICommand command, IVarType[] types, Object result) {
		IVarType resultType = command.getReturnVarType(types,
				result == null ? EnumVarType.Void : VariableTypeUtils.toEnumVarType(result), this,
				runCount);
		if (resultType != (result == null ? EnumVarType.Void : VariableTypeUtils.toEnumVarType(result))) {
			throwError("The return value is different from the specified return value.\nCorrect type:"
					+ resultType.toString() + "\nThis time type:"
					+ VariableTypeUtils.toEnumVarType(result).toString());
		}
	}

	private void replaceVar(String[] StrArr, int[] convlocation, int index) {
		if (this.hasRegName(StrArr[index])) {
			if (this.getRegReference(StrArr[index], this.getMostNearVarScope(StrArr[index]))) {
				if (ArrayUtils.contains(convlocation, index)) {
					StrArr[index] = Integer.toString(this.RegNames.indexOf(StrArr[index]));
				} else {
					Object val = this.getReg(StrArr[index]);
					StrArr[index] = val instanceof String ? "\"" + val.toString() + "\"" : val.toString();
				}
			} else {
				throwError("This variable cannot be referenced.");
			}
		}else if(ArrayUtils.contains(convlocation, index)) {
			this.throwError("This argument must specify a variable that exists.");
		}
	}

	private IVarType[] argsTypeCheck(IVarType[] types, IVarType[] argTypes) {
		if (argTypes != null) {
			if (types.length <= 0) {
				throwError("The length of the argument is set to zero.");
			} else {
				while (argTypes.length < types.length) {
					argTypes = ArrayUtils.add(argTypes, argTypes[argTypes.length - 1]);
				}
				for (int i = 0; i < argTypes.length; i++) {
					argTypes[i] = argTypes[i] == EnumVarType.None ? types[i] : argTypes[i];
				}
				if (!Objects.deepEquals(argTypes, types)) {
					int differentIndex = VariableTypeUtils.differentIndexOf(types, argTypes);
					throwError("The argument types are different.\nCorrect type:" + argTypes[differentIndex].toString()
							+ "\nThis time type:" + types[differentIndex].toString());
				}
			}
		}
		return argTypes;
	}

	private String[] replaceNestedSystem(String[] strArr) {
		for (int i = 0; i < strArr.length; i++) {
			if (Engine.isNestedFunction(strArr[i])) {

				boolean isMostUpper = !isRecursiveExec;
				if (isMostUpper) {
					this.saveCode = this.code;
					this.saveCmdName = this.commandname;
					this.saveScope = this.scope;
					this.isRecursiveExec = true;
				}
				Object results[] = this.run(strArr[i].substring(2, (strArr[i].length() - 1)));
				if (isMostUpper)
					this.isRecursiveExec = false;

				this.code = this.saveCode;
				this.commandname = this.saveCmdName;
				this.scope = this.saveScope;
				this.setReg("C", GLOBAL_SCOPE, this.getCodeCount() - 1, true);

				if (results.length <= 0) {
					throwError("Commands with a return value of void cannot be used for nested systems.");
				}

				String toStrRes = results[0] instanceof String ? "\"" + results[0].toString() + "\""
						: results[0].toString();
				strArr[i] = toStrRes;
			}
		}
		return strArr;
	}

	private String[] removeBlank(String[] StrArr) {
		while (CmdStrUtil.containsBlank(StrArr)) {
			StrArr = ArrayUtils.remove(StrArr, CmdStrUtil.indexOfBlank(StrArr));
		}
		return StrArr;
	}

	private void argsSizeCheck(String[] StrArr, ICommand command) {
		if (!ArrayUtils.contains(command.getArgCounts(), StrArr.length) &
				!(command.getArgCounts() == null)) {
			throwError("The number of arguments does not match the number of values set.");
		} else if (StrArr.length < command.getMinArgCount() & command.getArgCounts() == null) {
			throwError("The number of arguments does not match the number of values set.");
		}
	}

	private void initMultiLineCmdFieldProcessing(ICommand command) {
		if (command instanceof CommandMultiLine & !(command instanceof CommandEndMultiLine)) {
			this.ScopeNotExecutionInfo.add(true);
			this.MultiLineCmdHeadStr.add(this.commandname);
			this.scope++;
		}

		if (command instanceof IEndCommand & !(command instanceof CommandEndMultiLine)) {
			this.ScopeNotExecutionInfo.remove(this.ScopeNotExecutionInfo.size() - 1);

			if (this.MultiLineCmdHeadStr.size() < 1)
				throwError("This command cannot be called by itself.");
			String lastEndCmd = this.MultiLineCmdHeadStr.get(this.MultiLineCmdHeadStr.size() - 1);
			if (!((CommandMultiLine) commands.get(lastEndCmd)).getEndCommands().containsKey(this.commandname)) {
				this.throwError("End command is different.");
			}
			this.MultiLineCmdHeadStr.remove(this.MultiLineCmdHeadStr.size() - 1);
			this.scope--;
		}

		if (command instanceof CommandEndMultiLine) {
			if (this.MultiLineCmdHeadStr.size() < 1)
				throwError("This command cannot be called by itself.");
			String lastEndCmd = this.MultiLineCmdHeadStr.get(this.MultiLineCmdHeadStr.size() - 1);
			if (!((CommandMultiLine) commands.get(lastEndCmd)).getEndCommands().containsKey(this.commandname)) {
				this.throwError("End command is different.");
			}
			this.MultiLineCmdHeadStr.remove(this.MultiLineCmdHeadStr.size() - 1);
			this.MultiLineCmdHeadStr.add(this.commandname);
		}
	}
	
	

	private String cutComment(String code) {
		if (code.contains("#")) {
			if (StringUtils.countMatches(code, '#') >= 2) {
				code = code.substring(0, code.indexOf("#")) + code.substring(code.lastIndexOf("#") + 1);
			} else {
				code = code.substring(0, code.indexOf("#"));
			}
		}
		return code;
	}
	
	private void printFatalError(Exception e) {
		boolean available = true;

		if (isRecursiveExec) {
			this.code = this.saveCode;
			this.commandname = this.saveCmdName;
			this.scope = this.saveScope;
		}

		try {
			this.getReg("C", GLOBAL_SCOPE, true);
		} catch (Exception e1) {
			available = false;
		}

		System.err.println("\nOh my god...\n" + "It looks like an error occurred in java.\n");

		if (available) {
			System.err.println("Code:" + code.trim() + "\nLine:"
					+ (this.getCodeCount())
					+ "\nisInit:" + isInit
					+ "\nScope:" + this.scope
					+ "\n");
		} else {
			System.err.println(
					"Code:Error\nLine:Error\nisInit:" + isInit + "\nScope:" + this.scope + "\n");
		}

		System.err.println("");

		e.printStackTrace();

		System.exit(0);
	}

}
