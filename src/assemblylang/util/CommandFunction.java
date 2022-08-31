package assemblylang.util;

import assemblylang.Engine;

public interface CommandFunction<T> {
	public T run(T arg, Engine engine, int argCount);
}
