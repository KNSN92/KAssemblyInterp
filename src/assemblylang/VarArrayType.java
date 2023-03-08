package assemblylang;

import java.lang.reflect.Array;

public class VarArrayType implements IVarType {
	
	private Object defaultVal;
	private IVarType type;
	
	public static IVarType create(IVarType innerType) {
		return new VarArrayType(innerType);
	}

	private VarArrayType(IVarType type) {
		this.type = type;
		this.defaultVal = Array.newInstance(type.defaultVal().getClass(),0);
	}

	@Override
	public Class<?> innerClass() {
		return type.innerClass();
	}

	@Override
	public Object defaultVal() {
		return defaultVal;
	}
	
	public boolean isArray() {
		return true;
	}

	@Override
	public String getName() {
		/*String childName = type.getName();
		if(childName.endsWith("Array")) {
			String nonArrayChildName = childName.substring(0, (childName.length()-"Array".length()));
			if(nonArrayChildName.endsWith("-Dimensional")) {
				String nonArrayDimensionChildName = nonArrayChildName.substring(0, (childName.length()-"-Dimensional".length()));
				nonArrayChildName = nonArrayDimensionChildName.match
			}
			
		}*/
		return type.getName()+" Array";
	}

}
