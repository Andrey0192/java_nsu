package commands;
import java.util.logging.*;

abstract class AbstractCommand implements Command , Logging{
    protected String commandName;
    private static final Logger logger = Logger.getLogger(AbstractCommand.class.getName());
    public AbstractCommand(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public void logInfo(String message) {
        logger.log(Level.INFO, message);
    }

    @Override
    public void logInfo(String message, Throwable e) {
        logger.log(Level.INFO, message, e);
    }

    @Override
    public void logError(String message) {
        logger.log(Level.SEVERE, message);
    }

    @Override
    public void logError(String message, Throwable e) {
        logger.log(Level.SEVERE, message, e);
    }

    @Override
    public void logExeption(Exception ex) {
        logger.log(Level.SEVERE, ex.getMessage(), ex);
    }
}
