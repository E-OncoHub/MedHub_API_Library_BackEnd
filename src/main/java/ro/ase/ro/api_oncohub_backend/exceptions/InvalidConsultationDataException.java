package ro.ase.ro.api_oncohub_backend.exceptions;

public class InvalidConsultationDataException extends RuntimeException {
    public InvalidConsultationDataException(String message) {
        super(message);
    }
}
