package commands;

import java.util.HashMap;
import java.util.Stack;

public class PopCommand extends AbstractCommand {
    public PopCommand() {
        super("pop");
    }

    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args) {
        if (definitions.isEmpty()) {
            System.err.println("Ошибка: недостаточно элементов в definitions для операции PUSH");
            return;
        }
        double result = stack.pop();
        definitions.put(args[0], result);
        System.out.println("Результат операции POP : " + args[0] +" = " + result + " cнято с stack");
    }
}

