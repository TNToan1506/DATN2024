package com.example.demo.request;

import com.example.demo.validation.ValidInteger;
import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class AnhCTSPRequest {
    @NotBlank(message = "Link ảnh không được để trống")
    @Size(max = 255, message = "Link ảnh không được vượt quá 255 ký tự")
    private String   link;

    @NotBlank(message = "Tên ảnh không được để trống")
    @Size(max = 255, message = "Tên ảnh không được vượt quá 255 ký tự")
    private String ten;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 10, message = "Trạng thái không hợp lệ")
    @ValidInteger
    private Integer trangThai;

    @NotNull(message = "Vui lòng nhập vào id chi tiết sản phẩm")
    private String idCTSP;
}
