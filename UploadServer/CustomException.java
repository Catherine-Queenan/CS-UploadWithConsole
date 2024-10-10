public class CustomException extends Exception {
    public CustomException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}

// Custom exception for servlet not found
class ServletNotFoundException extends Exception {
    public ServletNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}

// Custom exception for method not found
class MethodNotFoundException extends Exception {
    public MethodNotFoundException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}
// To create a custom unchecked exception
class ServletInvocationException extends RuntimeException {
    public ServletInvocationException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}