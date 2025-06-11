package exceptions;

public class CommandCreationException extends RuntimeException {
  public CommandCreationException(String message, Throwable cause) {
    super(message, cause);
  }
}
