package commands;

import exceptions.CommandExecutionException;


public class MultCommand extends AbstractCommand {
    public MultCommand() {
        super("*");
    }

    @Override
    public void execute(ExecutionContext context, String... args) {

        if (context.getStack().size() < 2) {
            logError("Ошибка: " +
                    "недостаточно элементов в стеке для операции *");
           throw new CommandExecutionException("Ошибка: " +
                   "недостаточно элементов в стеке для операции *");
        }

        double result = context.getStack().pop() * context.getStack().pop();
        context.getStack().push(result);
        logInfo("Результат операции *: " + result);
    }
}

