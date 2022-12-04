package assemblylang.util;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.Validate;
import org.apache.commons.lang3.tuple.Pair;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;
import com.google.common.primitives.Longs;

import assemblylang.EnumVarType;
import assemblylang.IVarType;

public final class VariableTypeUtils {
	
	public static final List<Pair<EnumVarType, Function<String, ?>>> convTypes = Lists.newArrayList();
	public static final Map<String, EnumVarType> varTypeSymbols = ImmutableMap.of("int",EnumVarType.Int,"float", EnumVarType.Float,"boolean", EnumVarType.Boolean,"string", EnumVarType.String,"void", EnumVarType.Void);

	static {
		convTypes.add(Pair.of(EnumVarType.Int, Longs::tryParse));
		convTypes.add(Pair.of(EnumVarType.Float, Doubles::tryParse));
		convTypes.add(Pair.of(EnumVarType.Boolean, (str -> (str.equals("true") ? (Boolean) true
				: str.equals("false") ? (Boolean) false : (Boolean) null))));
		convTypes.add(Pair.of(EnumVarType.String,
				(str -> (StringQuotationUtils.isStringFormat(str)
						? str.length() <= 2 ? "" : str.substring(1, str.length() - 1)
						: null))));
	}

	public static EnumVarType ParseType(String arg) {
		if(arg==null) return EnumVarType.None;
		EnumVarType type = EnumVarType.None;
		for (Map.Entry<EnumVarType, Function<String, ?>> entry : convTypes) {
			type = (entry.getValue().apply(arg) == null ? EnumVarType.None : entry.getKey());
			if (type != EnumVarType.None)
				return type;
		}
		return EnumVarType.None;
	}

	public static Object Parse(String arg) {
		Object result = null;
		for (Map.Entry<EnumVarType, Function<String, ?>> entry : convTypes) {
			result = entry.getValue().apply(arg);
			if (result != null)
				return result;
		}
		return arg;
	}
	
	public static boolean isParsable(String arg) {
		return ParseType(arg) != EnumVarType.None;
	}
	
	public static EnumVarType toEnumVarType(Object obj) {
		if(obj == null) return EnumVarType.None;
		if(obj instanceof Integer) obj = (Long)(long)(int)(Integer)obj;
		EnumVarType[] values = EnumVarType.values();
		values = ArrayUtils.remove(values,ArrayUtils.indexOf(values, EnumVarType.None));
		for(EnumVarType enumVarType : values) {
			if(enumVarType.innerClass().isInstance(obj)) {
				 return enumVarType;
			}
		}
		return EnumVarType.None;
	}
	
	@SuppressWarnings("unchecked")
	public static <T> T[] toTypeArray(Object[] array, Function<Object,T> convFunc) {
		Object[] result = new Object[array.length];
		for(Object obj : array) {
			result = ArrayUtils.add(result, convFunc.apply(obj));
		}
		return (T[])result;
	}
	
	public static EnumVarType toEnumVarTypeFromClass(Class<?> type) {
		Validate.notNull(type);
		for(EnumVarType enumVarType : EnumVarType.values()) {
			if(enumVarType.innerClass().equals(type)) {
				 return enumVarType;
			}
		}
		return EnumVarType.None;
	}

	public static boolean isArrayEquals(IVarType[] enumVarType1, IVarType[] argTypes) {
		Validate.isTrue(enumVarType1.length == argTypes.length);
		for(int i = 0; i<enumVarType1.length; i++) {
			if(enumVarType1[i] != EnumVarType.None & argTypes[i] != EnumVarType.None)
			if(enumVarType1[i] != argTypes[i]) { return false; }
		}
		return true;
	}
	
	public static int differentIndexOf(IVarType [] enumVarType1, IVarType[] enumVarType2) {
		Validate.isTrue(enumVarType1.length == enumVarType2.length);
		if(isArrayEquals(enumVarType1, enumVarType2)) {
			return -1;
		}
		for(int i = 0; i<enumVarType1.length;i++) {
			if(enumVarType1[i] != enumVarType2[i]) { return i;}
		}
		return -1;
	}
	
	public static boolean objectEquals(EnumVarType type, Object obj) {
		if(type == EnumVarType.None) throw new IllegalArgumentException("EnumVarType.None is not allowed to be used with this method.");
		return type == EnumVarType.Void ? obj==null : type.innerClass() == obj.getClass();
	}
	
	public static EnumVarType stringToEnumVarType(String str) {
		return varTypeSymbols.get(str);
	}

}
