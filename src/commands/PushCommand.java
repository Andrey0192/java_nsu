package commands;

import factory.ExecutionContext;
import exceptions.CommandExecutionException;


public class PushCommand extends AbstractCommand {
    public PushCommand() {
        super("push");
    }

    @Override
    public void execute(ExecutionContext context, String... args) {

        if (context.getDefenitions().isEmpty()) {
            logError("Ошибка: " +
                    "недостаточно элементов в стеке для операции PUSH");
            throw new CommandExecutionException("Ошибка: " +
                   "недостаточно элементов в definitions для операции PUSH");

        }

        double result = context.getDefenitions().get(args[0]);
        context.getStack().push(result);
       logInfo("Результат операции PUSH : " + args[0] +" = " + result + " Добавлено в context.getStack()");
    }

    
}

