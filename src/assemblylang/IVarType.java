package assemblylang;

public interface IVarType {
	
	public Class<?> innerClass();
	public Object defaultVal();
	public String getName();
	
	public default boolean isNumber() {
		return false;
	}
	
	public default boolean isArray() {
		return false;
	}
}
