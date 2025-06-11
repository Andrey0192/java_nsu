package commands;

import factory.ExecutionContext;
import exceptions.CommandExecutionException;


public class PopCommand extends AbstractCommand {
    public PopCommand() {
        super("pop");
    }

    @Override
    public void execute(ExecutionContext context, String... args) {
        if (context.getDefenitions().isEmpty()) {
            logError("Ошибка: " +
                    "недостаточно элементов в стеке для операции POP");

           throw new CommandExecutionException("Ошибка: " +
                   "недостаточно элементов в definitions для операции POP");
        }
        double result = context.getStack().pop();
        context.getDefenitions().put(args[0], result);
       logInfo("Результат операции POP : " + args[0] +" = " + result + " cнято с context.getStack()");
    }
}

