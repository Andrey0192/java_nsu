package commands;

import java.util.HashMap;
import java.util.Stack;

public class PrintCommands extends AbstractCommand{
    public PrintCommands() {
        super("print");
    }
    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args) {
        if (stack.isEmpty()) {
            System.err.println("Ошибка: недостаточно элементов в стеке для операции PRINT");
            return;
        }
        double result = stack.peek();
        System.out.println("Результат операции PRINT: " + result);
    }
}
