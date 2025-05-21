package commands;

import java.util.HashMap;
import java.util.Stack;

public interface Command {
    void execute(Stack<Double> stack, HashMap<String, Double> definitions, String... args);
}



