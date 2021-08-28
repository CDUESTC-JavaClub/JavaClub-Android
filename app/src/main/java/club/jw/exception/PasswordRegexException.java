package club.jw.exception;

public class PasswordRegexException extends RuntimeException{
    public PasswordRegexException(String reason){
        super(reason);
    }
}
