package commands;

import java.util.HashMap;
import java.util.Stack;

public class SqrtCommand extends AbstractCommand {
    public SqrtCommand() {
        super("sqrt");
    }

    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args) {
        if (stack.isEmpty()) {
            System.err.println("Ошибка: недостаточно элементов в стеке для операции sqrt");
            return;
        }
        double result = Math.sqrt(stack.pop());
        stack.push(result);
        System.out.println("Результат операции sqrt: " + result);
    }
}