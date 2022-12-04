package assemblylang;

import java.util.Objects;

import org.apache.commons.lang3.Validate;

import assemblylang.util.VariableTypeUtils;

public class Variable<T> {
	
	private T val;
	private EnumVarType type;
	private boolean changeable;
	private boolean referable;
	
	public Variable(T val, EnumVarType type, boolean changeable, boolean referable) {
		this.val = val;
		this.changeable = changeable;
		this.referable = referable;
	}
	
	public Variable(T val) {
		this(val, VariableTypeUtils.toEnumVarType(val), true, true);
	}
	
	public Variable(T val, boolean changeable) {
		this(val, VariableTypeUtils.toEnumVarType(val), changeable, true);
	}

	public T getValue() {
		return val;
	}
	
	public EnumVarType getType() {
		return type;
	}

	@SuppressWarnings("unchecked")
	public void setValue(Object value) {
		if(value instanceof Integer) value = (long)(int)value;
		Validate.isInstanceOf(this.val.getClass(), value);
		this.val = (T)value;
	}

	public boolean isChangeable() {
		return changeable;
	}

	public void setChangeable(boolean changeable) {
		this.changeable = changeable;
	}

	public boolean isReferable() {
		return referable;
	}

	public void setReferable(boolean referable) {
		this.referable = referable;
	}
	
	public boolean Instanceof(Class<?> type) {
		return type.isInstance(val);
	}

	@Override
	public String toString() {
		return "Variable [" + (val != null ? "val=" + val + ", " : "") + "changeable=" + changeable + ", referable="
				+ referable + "]";
	}

	@Override
	public int hashCode() {
		return Objects.hash(changeable, referable, val);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof Variable)) {
			return false;
		}
		Variable<?> other = (Variable<?>) obj;
		return changeable == other.changeable && referable == other.referable && Objects.equals(val, other.val);
	}
	
}
