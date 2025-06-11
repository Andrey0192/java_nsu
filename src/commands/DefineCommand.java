package commands;

import factory.ExecutionContext;
import exceptions.CommandExecutionException;


public class DefineCommand extends AbstractCommand {

    public DefineCommand() {
        super("define");
    }
    @Override
    public void execute(ExecutionContext context, String... args){

        if (args.length != 2){
            logError("Ошибка: " +
                    "неверное количество аргументов для DEFINE");
            throw new CommandExecutionException("Ошибка: " +
                    "еверное количество аргументов для DEFINE");
        }
        try {
            double aDouble = Double.parseDouble(args[1]);
            context.getDefenitions().put(args[0], aDouble);
        } catch ( NumberFormatException e ) {
            logError("Ошибка: неверное значение для DEFINE");
            throw new CommandExecutionException("Ошибка: " +
                    "неверное значение для DEFINE");
        }

        logInfo("Результат операции DEFINE : "
                + args[0] +" = " + args[1] + " добавлено в definitions");
    }
}
