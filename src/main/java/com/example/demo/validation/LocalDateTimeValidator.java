package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

public class LocalDateTimeValidator implements ConstraintValidator<ValidLocalDateTime, LocalDateTime> {

    private static final String FORMAT = "yyyy-MM-dd HH:mm:ss";

    @Override
    public void initialize(ValidLocalDateTime constraintAnnotation) {
    }

    @Override
    public boolean isValid(LocalDateTime value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Nếu bạn không muốn lỗi cho trường null
        }
        // Kiểm tra xem giá trị có đúng định dạng không
        try {
            value.format(DateTimeFormatter.ofPattern(FORMAT));
            return true; // Đúng định dạng
        } catch (DateTimeParseException e) {
            return false; // Sai định dạng
        }
    }
}
