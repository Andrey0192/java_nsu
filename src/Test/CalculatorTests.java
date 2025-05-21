package Test;

import Exeptionals.CommandExecutionException;
import commands.AddCommand;
import commands.Command;
import org.junit.Test;

import java.util.HashMap;
import java.util.Stack;

import static org.junit.Assert.*;

public class CalculatorTests {

    @Test
    public void testAddCommand() {
        Stack<Double> stack = new Stack<>();
        HashMap<String, Double> definitions = new HashMap<>();
        stack.push(2.0);
        stack.push(3.0);

        Command addCommand = new AddCommand();
        addCommand.execute(stack, definitions);

        assertEquals(5.0, stack.peek(), 0.01);
    }

    @Test(expected = CommandExecutionException.class)
    public void testAddCommandInsufficientStackElements() {
        Stack<Double> stack = new Stack<>();
        HashMap<String, Double> definitions = new HashMap<>();
        stack.push(2.0);

        Command addCommand = new AddCommand();
        addCommand.execute(stack, definitions);
    }
}
