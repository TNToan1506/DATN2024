package com.example.demo.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class ValidatorInteger implements ConstraintValidator<ValidInteger, Integer> {

    @Override
    public void initialize(ValidInteger constraintAnnotation) {
        // Có thể để trống
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {
        if (value == null) {
            return true; // Nếu bạn không muốn lỗi cho trường null
        }

        // Kiểm tra xem giá trị có phải là số nguyên không
        return true; // Chỉ trả về true, bạn có thể thêm logic nếu cần
    }
}
