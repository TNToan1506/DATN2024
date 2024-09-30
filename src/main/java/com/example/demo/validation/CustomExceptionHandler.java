package com.example.demo.validation;
import com.fasterxml.jackson.databind.JsonMappingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@ControllerAdvice
public class CustomExceptionHandler {

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<String> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        String message = "Dữ liệu đầu vào không hợp lệ.";

        // Lấy thông tin chi tiết từ JsonMappingException
        Throwable cause = ex.getCause();
        if (cause instanceof JsonMappingException) {
            JsonMappingException jsonMappingException = (JsonMappingException) cause;
            String fieldName = jsonMappingException.getPath().isEmpty() ? "unknown field" : jsonMappingException.getPath().get(0).getFieldName();
            message = "Trường '" + fieldName + "' không hợp lệ: " + jsonMappingException.getOriginalMessage();
        }

        return ResponseEntity.badRequest().body(message);
    }
    // Có thể thêm các phương thức xử lý khác ở đây
}
