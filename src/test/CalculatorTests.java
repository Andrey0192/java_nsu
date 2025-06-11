package test;

import factory.ExecutionContext;
import exceptions.CommandExecutionException;
import commands.*;
import commands.Command;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.List;
import java.util.Stack;

import static org.junit.Assert.*;

//public class CalculatorTests {
//
//    @Test
//    public void testAddCommand() {
//        ExecutionContext context = new ExecutionContext();
//
//        context.getStack().push(2.0);
//        context.getStack().push(3.0);
//
//        Command addCommand = new AddCommand();
//        addCommand.execute(context);
//
//        assertEquals(5.0, context.getStack().peek(), 0.01);
//    }
//
//    @Test(expected = CommandExecutionException.class)
//    public void testAddCommandInsufficientStackElements() {
//        ExecutionContext context = new ExecutionContext();
//
//        context.getStack().push(2.0);
//
//        Command addCommand = new AddCommand();
//        addCommand.execute(context);
//    }
//}
//


//
public class CalculatorTests {

    private ExecutionContext context = new ExecutionContext();

//    @BeforeEach
//    public void setUp() {
//        context = new ExecutionContext();
//    }

    // -------------------
    // PUSH
    // -------------------

//    @Test
//    public void pushLiteralNumber() throws Exception {
//        new PushCommand().execute(context, String.valueOf(3.14));
//        Stack<Double> stack = context.getStack();
//        assertEquals(1, stack.size());
//        assertEquals(3.14, stack.peek(),00.1);
//    }

    @Test
    public void pushDefinedParameter() throws Exception {
        new DefineCommand().execute(context, "a", "2.5");
        new PushCommand().execute(context, "a");
        assertEquals(2.5, context.getStack().peek(),00.1);
    }

    @Test
    public void pushUndefinedParameterThrows() {
        assertThrows(Exception.class,
                () -> new PushCommand().execute(context,"unknownParam"));
    }

    @Test
    public void pushInvalidNumberThrows() {
        assertThrows(Exception.class,
                () -> new PushCommand().execute(context,"notANumber"));
    }

    @Test(expected = CommandExecutionException.class)
    public void popOnEmptyStackShouldThrow() throws Exception {
        ExecutionContext ctx = new ExecutionContext();
        new PopCommand().execute(ctx, "");
    }

//    // -------------------
//    // POP
//    // -------------------
    @Test
    public void popRemovesTop() throws Exception {
        new DefineCommand().execute(context, "a", "2.5");

        new PushCommand().execute(context, "a");
        new PopCommand().execute(context, "");
        assertTrue(context.getStack().isEmpty());
    }

    @Test(expected = CommandExecutionException.class)
    public void popEmptyStackThrows() {
        new PopCommand().execute(context, "");
    }


    // -------------------
    // ADD
    // -------------------
    @Test
    public void testAddCommand() {
        context.getStack().push(2.0);
        context.getStack().push(3.0);

        Command addCommand = new AddCommand();
        addCommand.execute(context);

        assertEquals(5.0, context.getStack().peek(), 0.01);
    }

    @Test
    public void addTwoNumbers() throws Exception {
        new DefineCommand().execute(context, "a", "2.5");
        new DefineCommand().execute(context, "b", "2.5");
        new PushCommand().execute(context, "a");
        new PushCommand().execute(context,"b");
        new AddCommand().execute(context, "");
        assertEquals(5.0, context.getStack().peek(),00.1);
    }
    @Test(expected = CommandExecutionException.class)
    public void addOneNumbers() throws Exception {
        new DefineCommand().execute(context, "a", "2.5");
        new PushCommand().execute(context, "a");
        new AddCommand().execute(context, "");
        assertEquals(5.0, context.getStack().peek(),00.1);
    }

//
//    // -------------------
//    // SUBTRACT
//    // -------------------
//
    @Test
    public void subTwoNumbers() throws Exception {
        new DefineCommand().execute(context, "a", "2.5");
        new DefineCommand().execute(context, "b", "2.5");
        new PushCommand().execute(context, "a");
        new PushCommand().execute(context,"b");
        new SubCommand().execute(context, "");
        assertEquals(5.0, context.getStack().peek(),00.1);
    }
        @Test(expected = CommandExecutionException.class)
        public void subOneNumbers() throws Exception {
            new DefineCommand().execute(context, "a", "2.5");
            new PushCommand().execute(context, "a");
            new SubCommand().execute(context, "");
            assertEquals(5.0, context.getStack().peek(),00.1);
        }

//    // -------------------
//    // MULTIPLY
//    // -------------------
//
    @Test
    public void MultTwoNumbers() throws Exception {
        new DefineCommand().execute(context, "a", "2.5");
        new DefineCommand().execute(context, "b", "2.5");
        new SubCommand().execute(context, "a");
        new SubCommand().execute(context,"b");
        new MultCommand().execute(context, "");
        assertEquals(5.0, context.getStack().peek(),00.1);
    }
        @Test(expected = CommandExecutionException.class)
        public void MultOneNumbers() throws Exception {
            new DefineCommand().execute(context, "a", "2.5");
            new PushCommand().execute(context, "a");
            new MultCommand().execute(context, "");
            assertEquals(5.0, context.getStack().peek(),00.1);
        }

//    // -------------------
//    // DIVIDE
//    // -------------------

    @Test
    public void DivTwoNumbers() throws Exception {
        new DefineCommand().execute(context, "a", "2.5");
        new DefineCommand().execute(context, "b", "2.5");
        new SubCommand().execute(context, "a");
        new SubCommand().execute(context,"b");
        new DivCommand().execute(context, "");
        assertEquals(5.0, context.getStack().peek(),00.1);
    }
        @Test(expected = CommandExecutionException.class)
        public void DivOneNumbers() throws Exception {
            new DefineCommand().execute(context, "a", "2.5");
            new PushCommand().execute(context, "a");
            new DivCommand().execute(context, "");
            assertEquals(5.0, context.getStack().peek(),00.1);
        }
        @Test(expected = CommandExecutionException.class)
        public void DivByZero() throws Exception {
            new DefineCommand().execute(context, "a", "2.5");
            new DefineCommand().execute(context, "b", "0");
            new SubCommand().execute(context, "a");
            new SubCommand().execute(context,"b");
            new DivCommand().execute(context, "");
            assertEquals(5.0, context.getStack().peek(),00.1);
        }




//    // -------------------
//    // SQRT
//    // -------------------
//



    @Test(expected = CommandExecutionException.class)
    public void SqrtOneNumbers() throws Exception {
        new DefineCommand().execute(context, "a", "2.5");
        new PushCommand().execute(context, "a");
        new SqrtCommand().execute(context, "");
        assertEquals(5.0, context.getStack().peek(),00.1);
    }
    @Test(expected = CommandExecutionException.class)
    public void SqrtByZero() throws Exception {

        new DefineCommand().execute(context, "b", "0");
        new SubCommand().execute(context,"b");
        new SqrtCommand().execute(context, "");
        assertEquals(5.0, context.getStack().peek(),00.1);
    }
    @Test(expected = CommandExecutionException.class)
    public void SqrtByNegative() throws Exception {
        new DefineCommand().execute(context, "a", "-2.5");
        new SubCommand().execute(context, "a");
        new SqrtCommand().execute(context, "");
        assertEquals(5.0, context.getStack().peek(),00.1);
    }

//    // -------------------
//    // DEFINE
//    // -------------------


    @Test(expected = CommandExecutionException.class)
    public void defineAndRedefine() throws Exception {
        new DefineCommand().execute(context, "a");

        assertEquals(2.5, context.getDefenitions().get("a"),00.1);
        new DefineCommand().execute(context, "a", "5");
        assertEquals(5, context.getDefenitions().get("a"),00.1);

    }

    @Test(expected = CommandExecutionException.class)
    public void defineMissingArgsThrows() throws Exception {
        new DefineCommand().execute(context, "a");

    }
    @Test(expected = CommandExecutionException.class)
    public void defineInvalidValueThrows() throws Exception {
        new DefineCommand().execute(context, "a" ,"FALSE");

    }

//    // -------------------
//    // PRINT
//    // -------------------



    @Test
    public void printTopValue() throws Exception {
        // Перехватим вывод в консоль
        var oldOut = System.out;
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        new DefineCommand().execute(context, "a", "7");
        new PushCommand().execute(context, "a");
        new PrintCommands().execute(context, "");

        System.out.flush();
        System.setOut(oldOut);

        String out = baos.toString().trim();
        assertEquals("7.0", out);
        // Проверяем что элемент остался на стеке
        assertEquals(7.0, context.getStack().peek(),00.1);
    }

    @Test
    public void printEmptyStackThrows() {
        assertThrows(Exception.class,
                () -> new PrintCommands().execute(context, ""));
    }

//    // -------------------
//    // КОМПЛЕКСНЫЙ СЦЕНАРИЙ
//    // -------------------

    @Test
    public void complexSequence() throws Exception {
        // DEFINE a 16
        new DefineCommand().execute(context, "a", "16");
        // PUSH a; SQRT -> 4
        new PushCommand().execute(context, "a");
        new SqrtCommand().execute(context, "");
        // PUSH 2; * -> 8
        new DefineCommand().execute(context, "b", "2");
        new PushCommand().execute(context, "b");
        new MultCommand().execute(context, "");
        // PRINT -> 8.0
        var oldOut = System.out;
        var baos = new ByteArrayOutputStream();
        System.setOut(new PrintStream(baos));

        new PrintCommands().execute(context, "");

        System.out.flush();
        System.setOut(oldOut);

        assertEquals("8.0", baos.toString().trim());
        assertEquals(8.0, context.getStack().peek(),00.1);
    }
}

