package assemblylang.util;

import java.util.Map;

import assemblylang.Engine;

public interface CommandFunction<T> {
	public T run(T arg, Engine engine, Map<String,?> customValueMap, int argCount);
}
