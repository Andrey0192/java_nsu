package commands;

import Exeptionals.CommandCreationException;
import Exeptionals.CommandNotFoundException;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Properties;

public class CommandFactory {
    private HashMap<String, String> commandMappings = new HashMap<>();

    public CommandFactory() {
        commandMappings = loadCommandMappinds();
    }

    private HashMap<String, String> loadCommandMappinds() {
        HashMap<String, String> commandMappings = new HashMap<>();
        try ( InputStream in = getClass().getResourceAsStream("commands_config.properties")) {
            Properties prop = new Properties();
            prop.load(in);
            for (String cmd : prop.stringPropertyNames()) {
                commandMappings.put( cmd, prop.getProperty( cmd));
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return commandMappings;
    }

    public Command createCommand(String commandName) {
        try {
            String className = commandMappings.get(commandName);
            if (className != null) {
                Class<?> clazz = Class.forName(className);
                return (Command) clazz.getConstructor().newInstance();
            } else {
                throw new CommandNotFoundException("Неизвестная команда: " + commandName);
            }
        } catch (Exception e) {
            throw new CommandCreationException("Ошибка при создании команды: " + commandName, e);
        }
    }
}
