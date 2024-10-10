// To create a custom exception
// Change the "CustomException" to an appropriate name (including the name of this java file)
public class CustomException extends Exception {
    public CustomException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}

// To create a custom unchecked exception
// Change the "TemplateException" to an appropriate name
class TemplateException extends RuntimeException {
    public TemplateException(String errorMessage, Throwable err) {
        super(errorMessage, err);
    }
}