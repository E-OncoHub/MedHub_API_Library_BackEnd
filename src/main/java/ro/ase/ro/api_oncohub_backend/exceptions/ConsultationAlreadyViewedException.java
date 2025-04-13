package ro.ase.ro.api_oncohub_backend.exceptions;

public class ConsultationAlreadyViewedException extends RuntimeException {
    public ConsultationAlreadyViewedException(String message) {
        super(message);
    }
}
