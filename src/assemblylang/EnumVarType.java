package assemblylang;

public enum EnumVarType implements IVarType{
	Int(Long.class, 0L),
	Float(Double.class, 0d),
	Boolean(Boolean.class, false),
	String(String.class, ""),
	Void(Void.class, null),
	None(null, null);
	
	private final Class<?> Type;
	private final Object defaultVal;
	

	private EnumVarType(Class<?> type, Object defaultVal) {
		Type = type;
		this.defaultVal = defaultVal;
	}

	@Override
	public Class<?> innerClass() {
		return this.Type;
	}
	
	@Override
	public Object defaultVal() {
		return defaultVal;
	}
	
	public boolean isNumber() { return this == EnumVarType.Int || this == EnumVarType.Float; }

	@Override
	public String getName() {
		return this.toString();
	}
	
}
