package assemblylang;

import java.util.Map;

import org.apache.commons.lang3.Validate;

public abstract class CommandMultiLine implements ICommand {
	
	public abstract Map<String, IEndCommand> getEndCommands();

	@Override
	public void registered(Engine engine) {
		Validate.notNull(getEndCommands());
		
		for(Map.Entry<String, IEndCommand> entry:this.getEndCommands().entrySet()) {
			if (engine.getCommand(entry.getKey()) == null) {
				Validate.notNull(entry.getKey());
				Validate.notNull(entry.getValue());
				engine.registerCommand(entry.getKey(), entry.getValue());
			}
		}
	}
	
	
	public interface IEndCommand extends ICommand{

		@Override
		public void RunWhenNotExec(Engine engine);
		
	}
	
	public abstract class CommandEndMultiLine extends CommandMultiLine implements IEndCommand{
		@Override
		public abstract void RunWhenNotExec(Engine engine);
	}
}
