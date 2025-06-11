package commands;


public interface Command {
    void execute(ExecutionContext context, String... args);
}



