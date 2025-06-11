package commands;

import factory.ExecutionContext;
import exceptions.CommandExecutionException;

public class SqrtCommand extends AbstractCommand {
    public SqrtCommand() {
        super("sqrt");
    }

    @Override
    public void execute(ExecutionContext context, String... args) {

        if (context.getStack().isEmpty()) {
            logError("Ошибка: недостаточно элементов в стеке для операции sqrt");
            throw new CommandExecutionException("Ошибка: недостаточно элементов в стеке для операции sqrt");
        }

        double aDouble = context.getStack().pop();
        if (aDouble < 0) {
            logError("Ошибка: число отрицательное");
            throw new CommandExecutionException("Ошибка: число отрицательное");

        }

        double result = Math.sqrt(aDouble);
        context.getStack().push(result);
        logInfo("Результат операции sqrt: " + result);
    }
}