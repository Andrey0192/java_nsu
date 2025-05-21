package Exeptionals;

public class CommandNotFoundException extends RuntimeException {
  public CommandNotFoundException(String message) {
    super(message);
  }
}
