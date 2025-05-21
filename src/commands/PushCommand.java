package commands;

import java.util.HashMap;
import java.util.Stack;

public class PushCommand extends AbstractCommand {
    public PushCommand() {
        super("push");
    }

    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args) {
        if (definitions.isEmpty()) {
            System.err.println("Ошибка: недостаточно элементов в definitions для операции PUSH");
            return;
        }
        double result = definitions.get(args[0]);
        stack.push(result);
        System.out.println("Результат операции PUSH : " + args[0] +" = " + result + " Добавлено в stack");
    }
}

