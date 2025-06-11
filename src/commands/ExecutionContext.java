package commands;

import java.util.*;

public class ExecutionContext {
    Stack<Double> stack = new Stack<>();
    HashMap<String, Double> definitions = new HashMap<>();

    public Stack<Double> getStack() {
        return stack;
    }

    public Map<String, Double> getDefenitions() {
        return definitions;
    }
}