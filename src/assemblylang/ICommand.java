package assemblylang;

public interface ICommand {
	
	public Object runCommand(Object[]input, Engine engine, IVarType[] argTypes, int argCount);
	
	public boolean isRunnable(Object[]input, Engine engine, int argCount);
	
	public int[] getArgCounts();
	
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount);
	
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount);
	
	public String getReturnRegName();
	
	default public int[] getNoConversionLocations() {
		return null;
	}
	
	default public int getMinArgCount() {
		return 0;
	}
	
	default public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		return args;
	}
	
	default public void init(Engine engine, String[] codes) {}
	
	default public void initRun(Object[]input, Engine engine, IVarType[] argTypes, int argCount) {}
	
	default public void registered(Engine engine) {};
	
	default public void RunWhenNotExec(Engine engine) {}
	
	default public void contentExecutingIf(Engine engine, String code) {}
}
