package ro.ase.ro.api_oncohub_backend.exceptions;

public class DuplicateApiKeyException extends RuntimeException {
    public DuplicateApiKeyException(String message) {
        super(message);
    }
}
