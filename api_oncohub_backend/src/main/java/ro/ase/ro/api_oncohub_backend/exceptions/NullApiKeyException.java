package ro.ase.ro.api_oncohub_backend.exceptions;

public class NullApiKeyException extends RuntimeException {
    public NullApiKeyException(String message) {
        super(message);
    }
}
