package com.example.demo.request;

import com.example.demo.entities.DanhMuc;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LoaiSanPhamRequest {

    @Size(max = 10, message = "Mã sản phẩm không được vượt quá 10 ký tự")
//    @Pattern(regexp = "^LSP[A-Z0-9]{7}$", message = "Mã phải có định dạng CTSPXXXXXXX (X là chữ cái hoặc số)!")
    private String ma;

    @NotBlank(message = "Tên sản phẩm không được để trống")
    @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
    @Pattern(regexp = "^[a-zA-ZÀ-ỹà-ỹ\\s]+$", message = "Tên sản phẩm chỉ được chứa chữ cái!")
    private String ten;

    @NotNull(message = "Trạng thái không được để trống")
    @Min(value = 0, message = "Trạng thái không hợp lệ")
    @Max(value = 4, message = "Trạng thái không hợp lệ")
    private Integer trangThai;

    @NotBlank(message = "Danh mục sản phẩm không được để trống")
    private String idDanhMuc;
}
