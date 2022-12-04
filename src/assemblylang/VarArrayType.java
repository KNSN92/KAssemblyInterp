package assemblylang;

public class VarArrayType implements IVarType {
	
	private Object defaultVal;
	private EnumVarType type;
	
	public static IVarType create(EnumVarType innerType) {
		return new VarArrayType(innerType);
	}

	private VarArrayType(EnumVarType type) {
		this.type = type;
		this.defaultVal = type.defaultVal();
	}

	@Override
	public Class<?> innerClass() {
		return type.innerClass();
	}

	@Override
	public Object defaultVal() {
		return defaultVal;
	}

}
