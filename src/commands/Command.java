package commands;


import factory.ExecutionContext;

public interface Command {
    void execute(ExecutionContext context, String... args);
}



