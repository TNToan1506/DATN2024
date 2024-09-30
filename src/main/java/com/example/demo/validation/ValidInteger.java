package com.example.demo.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = ValidatorInteger.class) // Chỉ định validator tương ứng
@Target({ElementType.METHOD, ElementType.FIELD, ElementType.ANNOTATION_TYPE, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidInteger {
    String message() default "Giá trị không hợp lệ. Phải là số nguyên.";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}
