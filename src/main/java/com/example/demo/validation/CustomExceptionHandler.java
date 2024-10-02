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

        // In chi tiết lỗi để debug
        ex.printStackTrace(); // hoặc sử dụng logger để log lỗi

        // Lấy thông tin chi tiết từ JsonMappingException
        Throwable cause = ex.getCause();
        if (cause instanceof JsonMappingException) {
            JsonMappingException jsonMappingException = (JsonMappingException) cause;

            // Lấy thông tin các trường không hợp lệ
            if (!jsonMappingException.getPath().isEmpty()) {
                StringBuilder invalidFields = new StringBuilder();
                for (JsonMappingException.Reference reference : jsonMappingException.getPath()) {
                    invalidFields.append(reference.getFieldName()).append(", ");
                }

                // Xóa dấu phẩy cuối cùng và thêm thông báo chi tiết
                if (invalidFields.length() > 0) {
                    invalidFields.setLength(invalidFields.length() - 2);
                    message = "Các trường không hợp lệ: " + invalidFields.toString() + ". " + jsonMappingException.getOriginalMessage();
                }
            } else {
                message = "Dữ liệu JSON không hợp lệ. " + jsonMappingException.getOriginalMessage();
            }
        } else {
            message = "Dữ liệu không thể đọc được. Vui lòng kiểm tra định dạng.";
        }

        return ResponseEntity.badRequest().body(message);
    }
}
