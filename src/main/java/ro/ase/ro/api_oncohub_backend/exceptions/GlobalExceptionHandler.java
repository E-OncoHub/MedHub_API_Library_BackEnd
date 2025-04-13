package ro.ase.ro.api_oncohub_backend.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ConsultationNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleConsultationNotFound(ConsultationNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("No consultation found!"));
    }

    @ExceptionHandler(ConsultationAlreadyViewedException.class)
    public ResponseEntity<ErrorResponse> handleConsultationAlreadyViewed(ConsultationAlreadyViewedException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ErrorResponse("Consultation already viewed."));
    }

    @ExceptionHandler(InvalidConsultationDataException.class)
    public ResponseEntity<ErrorResponse> handleInvalidConsultationData(InvalidConsultationDataException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(DuplicateApiKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateApiKey(DuplicateApiKeyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(NullApiKeyException.class)
    public ResponseEntity<ErrorResponse> handleNullApiKey(NullApiKeyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGenericException(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(ex.getMessage()));
    }
}