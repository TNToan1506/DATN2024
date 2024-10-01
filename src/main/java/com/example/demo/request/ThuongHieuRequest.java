package com.example.demo.request;

import com.example.demo.validation.ValidInteger;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ThuongHieuRequest {

//    @Size(max = 10, message = "Mã thương hiệu không được vượt quá 10 ký tự")
////    @Pattern(regexp = "^TH[A-Z0-9]{8}$", message = "Mã phải có định dạng THXXXXXXXX (X là chữ cái hoặc số)!")
//    private String ma;

    @NotBlank(message = "Tên thương hiệu không được để trống")
    @Size(max = 255, message = "Tên thương hiệu không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-z A-Z À-ỹ à-ỹ\\s]+$", message = "Tên thương hiệu chỉ được chứa chữ cái!")
    private String ten;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    @ValidInteger
    private Integer trangThai;

    @NotBlank(message = "Xuất xứ không được để trống")
    @Size(max = 255, message = "Xuất xứ không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-z A-Z À-ỹ à-ỹ\\s]+$", message = "Xuất xứ chỉ được chứa chữ cái!")
    private String xuatXu;

    @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
    private String moTa;
}
