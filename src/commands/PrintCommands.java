package commands;

import exceptions.CommandExecutionException;


public class PrintCommands extends AbstractCommand{
    public PrintCommands() {
        super("print");
    }
    @Override
    public void execute(ExecutionContext context, String... args) {

        if (context.getStack().isEmpty()) {
            logError("Ошибка: " +
                    "недостаточно элементов в стеке для операции PRINT");
            throw new CommandExecutionException("Ошибка: " +
                   "недостаточно элементов в стеке для операции PRINT");
        }

        double result = context.getStack().peek();
       logInfo("Результат операции PRINT: " + result);
    }
}
