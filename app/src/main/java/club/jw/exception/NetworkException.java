package club.jw.exception;

public class NetworkException extends RuntimeException{
    public NetworkException(String reason){
        super(reason);
    }
}
