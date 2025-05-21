package commands;
import java.util.logging.*;

abstract class AbstractCommand implements Command , Logging{
    protected String commandName;

    public AbstractCommand(String commandName) {
        this.commandName = commandName;
    }

    @Override
    public void logInfo(String message) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, message);
    }

    @Override
    public void logInfo(String message, Throwable e) {
        Logger.getLogger(this.getClass().getName()).log(Level.INFO, message, e);
    }

    @Override
    public void logError(String message) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, message);
    }

    @Override
    public void logError(String message, Throwable e) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, message, e);
    }

    @Override
    public void logExeption(Exception ex) {
        Logger.getLogger(this.getClass().getName()).log(Level.SEVERE, ex.getMessage(), ex);
    }
}
