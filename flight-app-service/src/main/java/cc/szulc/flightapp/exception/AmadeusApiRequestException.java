package cc.szulc.flightapp.exception;

public class AmadeusApiRequestException extends RuntimeException {

    public AmadeusApiRequestException(String message, Throwable cause) {
        super(message, cause);
    }
}