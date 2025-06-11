package logging;

public interface Logging {
    void logInfo(String message);
    void logInfo(String message, Throwable e);
    void logError(String message);
    void logError(String message, Throwable e);
}
