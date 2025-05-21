import commands.Command;
import commands.CommandFactory;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Stack;

public class Main {
    public static void main(String[] args) {
        Stack<Double> stack = new Stack<>();
        HashMap<String, Double> definitions = new HashMap<>();
        CommandFactory factory = new CommandFactory();
        BufferedReader in = null;

        try {
            if (args.length > 0) {
                in = new BufferedReader(new FileReader(args[0]));
            } else {
                in = new BufferedReader(new InputStreamReader(System.in));
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        String line;
        try {
            while ((line = in.readLine()) != null) {
                String[] tokens = line.split("\\s+");
                String commandName = tokens[0];
                Command command = factory.createCommand(commandName);
                String[] commandArgs = Arrays.copyOfRange(tokens, 1, tokens.length);
                command.execute(stack, definitions, commandArgs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
