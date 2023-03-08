package assemblylang.commands;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Map;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ClassUtils;

import com.google.common.collect.Maps;

import assemblylang.Engine;
import assemblylang.EnumVarType;
import assemblylang.ICommand;
import assemblylang.IVarType;
import assemblylang.util.CmdStrUtil;
import assemblylang.util.VariableTypeUtils;

public class CommandNATIVEFUNC implements ICommand {

	private Class<?>[] varTypes;
	private String asName = "";
	
	public Map<String, ICommand> makedCommands = Maps.newHashMap();

	@Override
	public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		return null;
	}

	@Override
	public void initRun(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
		String className = (String) input[0];
		String methodName = (String) input[1];
		Method method = null;
		try {
			Class<?> funcClass = Class.forName(className);
			Class<?>[] wrappedClasses = wrappersToPrimitivesButOtherClassDontChangeForNull(varTypes);
			method = funcClass.getMethod(methodName, wrappedClasses);
		} catch (Exception e) {
			engine.throwError(e.getMessage());
		}
		String methodCmdName = method.getName();
		if(!asName.isEmpty()) {
			methodCmdName = asName;
		}
		ICommand cmd = new CommandNATIVEFUNCOBJECT(method);
		engine.registerCommand(methodCmdName, cmd);
		makedCommands.put(methodCmdName, cmd);
	}

	@Override
	public String[] getInitResult(String[] args, Engine engine, int argCount, boolean isInit) {
		int i = 2;
		varTypes = new Class<?>[0];
		while (i < argCount && VariableTypeUtils.varTypeSymbols.get(args[i]) != null) {
			IVarType type = VariableTypeUtils.varTypeSymbols.get(args[i]);
			varTypes = ArrayUtils.add(varTypes, type.innerClass());
			if (type == EnumVarType.Void) {
				engine.throwError("Type void is an invalid type.");
				return args;
			}
			args = CmdStrUtil.replaceZero(args, i);
			i++;
		}
		asName = "";
		if(i < argCount) {
			String argsi = "";
			if(VariableTypeUtils.parseType(args[i]) == EnumVarType.String) {
				argsi = (String)VariableTypeUtils.parse(args[i]);
			}else {
				return args;
			}
			
			if(argsi.matches("^[A-Za-z]\\w*$")) {
				asName = argsi;
				args = CmdStrUtil.replaceZero(args, i);
				i++;
			}else {
				engine.throwError("A function name is required.");
				return args;
			}
		}
		if(i < argCount) {
			engine.throwError("Invalid argument length.");
		}
		return args;
	}

	@Override
	public boolean isRunnable(Object[] input, Engine engine, int argCount) {
		return true;
	}

	@Override
	public int[] getArgCounts() {
		return null;
	}

	@Override
	public int getMinArgCount() {
		return 2;
	}

	@Override
	public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
		return argCount < 3 ? new IVarType[] {EnumVarType.String, EnumVarType.String} : 
					new IVarType[] {EnumVarType.String, EnumVarType.String, EnumVarType.Int};
	}

	@Override
	public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
		return EnumVarType.Void;
	}

	@Override
	public String getReturnRegName() {
		return null;
	}
	
	public class CommandNATIVEFUNCOBJECT implements ICommand {
		
		private Method method = null;

		public CommandNATIVEFUNCOBJECT(Method method) {
			if(!Modifier.isStatic(method.getModifiers())) {
				throw new IllegalArgumentException("The only method object that can be assigned is the static method.");
			}
			this.method = method;
		}

		@Override
		public Object runCommand(Object[] input, Engine engine, IVarType[] argTypes, int argCount) {
			try {
				return method.invoke(null, input);
			} catch (IllegalAccessException e) {
				engine.throwError(e.getMessage());
			} catch (IllegalArgumentException e) {
				engine.throwError(e.getMessage());
			} catch (InvocationTargetException e) {
				engine.throwError(e.getMessage());
			}
			return null;
		}

		@Override
		public boolean isRunnable(Object[] input, Engine engine, int argCount) {
			return true;
		}

		@Override
		public int[] getArgCounts() {
			return new int[] {method.getParameterCount()};
		}

		@Override
		public IVarType[] getArgVarTypes(IVarType[] argTypes, Engine engine, int argCount) {
			Class<?>[] paramTypes = method.getParameterTypes();
			IVarType[] returnArgTypes = new IVarType[0];
			for(Class<?> paramType:paramTypes) {
				IVarType convertedType = VariableTypeUtils.toEnumVarTypeFromClass(paramType);
				returnArgTypes = ArrayUtils.add(returnArgTypes, convertedType);
			}
			return returnArgTypes;
		}

		@Override
		public IVarType getReturnVarType(IVarType[] argTypes, IVarType resultType, Engine engine, int argCount) {
			Class<?> returnType = ClassUtils.primitiveToWrapper(method.getReturnType());
			IVarType returnVarType = VariableTypeUtils.toEnumVarTypeFromClass(returnType); 
			return returnVarType;
		}

		@Override
		public String getReturnRegName() {
			return method.getReturnType() == void.class ? null : Engine.DEFAULT_RETURN_REG_NAME;
		}
		
	}
	
	private static Class<?> wrapperToPrimitiveButOtherClassDontChangeForNull(Class<?> param) {
		if(ClassUtils.isPrimitiveWrapper(param)) {
			return ClassUtils.wrapperToPrimitive(param);
		}else {
			return param;
		}
	}
	
	private static Class<?>[] wrappersToPrimitivesButOtherClassDontChangeForNull(Class<?>... param) {
		Class<?>[] res = new Class<?>[0];
		for(Class<?> paramUnit:param) {
			res = ArrayUtils.add(res, wrapperToPrimitiveButOtherClassDontChangeForNull(paramUnit));
		}
		return res;
	}

}
