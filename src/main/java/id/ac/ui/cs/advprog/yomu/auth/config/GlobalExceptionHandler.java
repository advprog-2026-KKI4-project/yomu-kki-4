package id.ac.ui.cs.advprog.yomu.auth.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String, String>> handleRuntimeException(RuntimeException ex) {
        String message = ex.getMessage() == null ? "" : ex.getMessage();
        HttpStatus status;
        String clientMessage;

        if (message.contains("already registered")) {
            status = HttpStatus.CONFLICT;
            clientMessage = "Resource already registered";
        } else if (message.contains("Invalid credentials")) {
            status = HttpStatus.UNAUTHORIZED;
            clientMessage = "Invalid credentials";
        } else if (message.contains("Unauthorized")) {
            status = HttpStatus.UNAUTHORIZED;
            clientMessage = "Unauthorized";
        } else if (message.contains("not found")) {
            status = HttpStatus.NOT_FOUND;
            clientMessage = "Resource not found";
        } else {
            status = HttpStatus.INTERNAL_SERVER_ERROR;
            clientMessage = "Internal server error";
            log.error("Unhandled runtime exception", ex);
        }

        return ResponseEntity.status(status).body(Map.of("error", clientMessage));
    }
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> 
            errors.merge(error.getField(), error.getDefaultMessage(), (existing, next) -> existing + "; " + next));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errors);
    }
}
