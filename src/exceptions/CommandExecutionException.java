package exceptions;


public class CommandExecutionException extends RuntimeException {
  public CommandExecutionException(String message) {
    super(message);
  }
}

