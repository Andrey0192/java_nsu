package commands;

import java.util.HashMap;
import java.util.Stack;

public class DivCommand extends AbstractCommand {
    public DivCommand() {
        super("/");
    }

    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args) {
        if (stack.size() < 2) {
            System.err.println("Ошибка: недостаточно элементов в стеке для операции +");
            return;
        }
        double a = stack.pop();
        if (stack.peek()  == 0 ){
            stack.push(a);
            System.err.println("Ошибка: b == 0 ");
            return;
        }
        double b = stack.pop();
        double result = a /  b;
        stack.push(result);
        System.out.println("Результат операции +: " + result);
    }
}
