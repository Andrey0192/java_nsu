package commands;
import Exeptionals.CommandExecutionException;

import java.util.HashMap;
import java.util.Stack;

public class AddCommand extends AbstractCommand {

    public AddCommand() {
        super("+");
    }

    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args) {
        try {

            if (stack.size() < 2) {
                logError("Ошибка: недостаточно элементов в стеке для операции +");
                throw new CommandExecutionException("Недостаточно элементов в стеке для операции +");
            }
            double result = stack.pop() + stack.pop();
            stack.push(result);
            logInfo("Результат операции + : "  + result);
        } catch (CommandExecutionException e) {
//            logExeption(e);
//            throw e;
        } catch (Exception e) {
            logExeption(e);
            throw new RuntimeException("Неизвсетная ошибка во время выполнения",e);
        }
    }
}


