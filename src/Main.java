import commands.Command;
import factory.CommandFactory;
import factory.ExecutionContext;

import java.io.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {

        String fileName = "commands_config.properties";
        CommandFactory factory = new CommandFactory(fileName);
        BufferedReader in = null;
        ExecutionContext context = new ExecutionContext();

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
                command.execute(context, commandArgs);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
