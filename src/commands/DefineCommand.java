package commands;

import java.util.HashMap;
import java.util.Stack;

public class DefineCommand extends AbstractCommand {

    public DefineCommand() {
        super("define");
    }
    @Override
    public void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args){
        definitions.put(args[0], Double.parseDouble(args[1]));
        System.out.println("Результат операции DEFINE : " + args[0] +" = " + args[1] + " добавлено в definitions");
    }
}
