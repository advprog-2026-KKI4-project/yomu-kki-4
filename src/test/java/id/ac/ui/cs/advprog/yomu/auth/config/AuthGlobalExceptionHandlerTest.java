package id.ac.ui.cs.advprog.yomu.auth.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthGlobalExceptionHandlerTest {

    @InjectMocks
    private AuthGlobalExceptionHandler handler;

    @Test
    void handleRuntimeException_shouldReturnConflict_whenAlreadyRegistered() {
        RuntimeException ex = new RuntimeException("Email is already registered");

        ResponseEntity<Map<String, String>> result = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.CONFLICT, result.getStatusCode());
        assertEquals("Resource already registered", result.getBody().get("error"));
    }

    @Test
    void handleRuntimeException_shouldReturnUnauthorized_whenInvalidCredentials() {
        RuntimeException ex = new RuntimeException("Invalid credentials");

        ResponseEntity<Map<String, String>> result = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Invalid credentials", result.getBody().get("error"));
    }

    @Test
    void handleRuntimeException_shouldReturnUnauthorized_whenUnauthorized() {
        RuntimeException ex = new RuntimeException("Unauthorized");

        ResponseEntity<Map<String, String>> result = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.UNAUTHORIZED, result.getStatusCode());
        assertEquals("Unauthorized", result.getBody().get("error"));
    }

    @Test
    void handleRuntimeException_shouldReturnNotFound_whenNotFound() {
        RuntimeException ex = new RuntimeException("Resource not found");

        ResponseEntity<Map<String, String>> result = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
        assertEquals("Resource not found", result.getBody().get("error"));
    }

    @Test
    void handleRuntimeException_shouldReturnInternalServerError_whenUnknown() {
        RuntimeException ex = new RuntimeException("Something unexpected happened");

        ResponseEntity<Map<String, String>> result = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Internal server error", result.getBody().get("error"));
    }

    @Test
    void handleRuntimeException_shouldHandleNullMessage() {
        RuntimeException ex = new RuntimeException();

        ResponseEntity<Map<String, String>> result = handler.handleRuntimeException(ex);

        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
        assertEquals("Internal server error", result.getBody().get("error"));
    }

    @Test
    void handleValidationExceptions_shouldReturnBadRequestWithFieldErrors() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("object", "email", "Email is invalid"),
                new FieldError("object", "password", "Password is too short")
        ));

        ResponseEntity<Map<String, String>> result = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertTrue(result.getBody().containsKey("email"));
        assertTrue(result.getBody().containsKey("password"));
        assertEquals("Email is invalid", result.getBody().get("email"));
        assertEquals("Password is too short", result.getBody().get("password"));
    }

    @Test
    void handleValidationExceptions_shouldHandleNullDefaultMessage() {
        MethodArgumentNotValidException ex = mock(MethodArgumentNotValidException.class);
        BindingResult bindingResult = mock(BindingResult.class);
        when(ex.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(
                new FieldError("object", "username", null)
        ));

        ResponseEntity<Map<String, String>> result = handler.handleValidationExceptions(ex);

        assertEquals(HttpStatus.BAD_REQUEST, result.getStatusCode());
        assertEquals("Invalid value", result.getBody().get("username"));
    }
}