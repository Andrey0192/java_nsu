package commands;

import java.util.HashMap;
import java.util.Stack;

public class SubCommand extends AbstractCommand{
    public SubCommand() {
        super("-");
    }

    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions,String... args) {
        if (stack.size() < 2) {
            return;
        }
        double result = stack.pop() - stack.pop();
        stack.push(result);
        System.out.println("Результат операции +: " + result);
    }
}

