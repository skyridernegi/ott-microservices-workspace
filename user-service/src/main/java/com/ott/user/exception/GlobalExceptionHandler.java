package com.ott.user.exception;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.*;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<Map<String,Object>> handle(RuntimeException ex, HttpServletRequest req) {
        Map<String,Object> err = new LinkedHashMap<>();
        err.put("status", 400); err.put("message", ex.getMessage());
        err.put("path", req.getRequestURI()); err.put("timestamp", LocalDateTime.now());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(err);
    }
}