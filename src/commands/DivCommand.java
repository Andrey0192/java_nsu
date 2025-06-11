package commands;

import exceptions.CommandExecutionException;


public class DivCommand extends AbstractCommand {
    public DivCommand() {
        super("/");
    }

    @Override
    public void execute(ExecutionContext context, String... args) {

        if (context.getStack().size() < 2) {
            logError("Ошибка: " +
                    "недостаточно элементов в стеке для операции /");
            throw new CommandExecutionException("Ошибка: " +
                    "недостаточно элементов в стеке для операции /");
        }
        double a = context.getStack().pop();

        if (context.getStack().peek()  == 0 ){
            context.getStack().push(a);
            throw new CommandExecutionException("Ошибка: b == 0 ");
        }

        double b = context.getStack().pop();
        double result = a /  b;

        context.getStack().push(result);

        logInfo("Результат операции / : "  + result);
    }
}
