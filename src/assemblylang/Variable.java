package assemblylang;

import java.util.Objects;

public class Variable<T> {
	
	private T val;
	private boolean changeable;
	private boolean referable;
	
	public Variable(T val, boolean changeable, boolean referable) {
		this.val = val;
		this.changeable = changeable;
		this.referable = referable;
	}
	
	public Variable(T val) {
		this(val, true, true);
	}
	
	public Variable(T val, boolean changeable) {
		this(val, changeable, true);
	}

	public T getValue() {
		return val;
	}

	public void setValue(T val) {
		this.val = val;
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
