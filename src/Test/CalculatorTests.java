package Test;

import commands.ExecutionContext;
import exceptions.CommandExecutionException;
import commands.AddCommand;
import commands.Command;
import org.junit.Test;

import java.util.HashMap;
import java.util.Stack;

import static org.junit.Assert.*;

public class CalculatorTests {

    @Test
    public void testAddCommand() {
        ExecutionContext context = new ExecutionContext();

        context.getStack().push(2.0);
        context.getStack().push(3.0);

        Command addCommand = new AddCommand();
        addCommand.execute(context);

        assertEquals(5.0, context.getStack().peek(), 0.01);
    }

    @Test(expected = CommandExecutionException.class)
    public void testAddCommandInsufficientStackElements() {
        ExecutionContext context = new ExecutionContext();

        context.getStack().push(2.0);

        Command addCommand = new AddCommand();
        addCommand.execute(context);
    }
}
