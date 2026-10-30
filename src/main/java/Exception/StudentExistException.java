package Exception;

public class StudentExistException extends RuntimeException {
    public StudentExistException(String message) {
        super(message);
    }
}
