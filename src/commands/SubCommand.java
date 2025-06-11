package commands;

import exceptions.CommandExecutionException;


public class SubCommand extends AbstractCommand{
    public SubCommand() {
        super("-");
    }

    @Override
    public void execute(ExecutionContext context,String... args) {

        if (context.getStack().size() < 2) {
            logError("Ошибка: " +
                    "недостаточно элементов в стеке для операции -");
            throw new CommandExecutionException("Ошибка: " +
                    "недостаточно элементов в стеке для операции -");
        }

        double result = context.getStack().pop() - context.getStack().pop();
        context.getStack().push(result);

        logInfo("Результат операции -: " + result);
    }
}

