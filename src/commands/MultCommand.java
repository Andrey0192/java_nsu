package commands;

import java.util.HashMap;
import java.util.Stack;

public class MultCommand extends AbstractCommand {
    public MultCommand() {
        super("+");
    }

    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args) {
        if (stack.size() < 2) {
            System.err.println("Ошибка: недостаточно элементов в стеке для операции +");
            return;
        }
        double result = stack.pop() * stack.pop();
        stack.push(result);
        System.out.println("Результат операции +: " + result);
    }
}

