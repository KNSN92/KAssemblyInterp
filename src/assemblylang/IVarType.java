package assemblylang;

public interface IVarType {
	
	public Class<?> innerClass();
	public Object defaultVal();
	
	public default boolean isNumber() {
		return false;
	}
}
