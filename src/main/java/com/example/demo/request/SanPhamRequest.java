    package com.example.demo.request;

    import com.example.demo.validation.ValidInteger;
    import jakarta.validation.constraints.*;
    import lombok.AllArgsConstructor;
    import lombok.Getter;
    import lombok.NoArgsConstructor;
    import lombok.Setter;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public class SanPhamRequest {


//        @Size(max = 10, message = "Mã sản phẩm không được vượt quá 10 ký tự")
////        @Pattern(regexp = "^SP[A-Z0-9]{8}$", message = "Mã phải có định dạng SPXXXXXXXX (X là chữ cái hoặc số)!")
//        private String maSP;
        private String id;

        @Size(max = 255, message = "Tên sản phẩm không được vượt quá 255 ký tự")
        @Pattern(regexp = "^[a-zA-Z0-9À-ỹà-ỹ\\s]+$", message = "Tên sản phẩm chỉ được chứa chữ cái, số và khoảng trắng!")
        @NotBlank(message = "Tên sản phẩm không được để trống")
        private String tenSP;

        @NotBlank(message = "Thành phần không được để trống")
        @Size(max = 255, message = "Thành phần không được vượt quá 255 ký tự")
        private String thanhPhan;

        @NotBlank(message = "Công dụng không được để trống")
        @Size(max = 255, message = "Công dụng không được vượt quá 255 ký tự")
        private String congDung;

        @Min(value = 0, message = "Tuổi tối thiểu không hợp lệ")
        @Max(value = 100, message = "Tuổi tối thiểu không hợp lệ")
        @ValidInteger
        private Integer tuoiMin;

        @Min(value = 0, message = "Tuổi tối đa không hợp lệ")
        @Max(value = 200, message = "Tuổi tối đa không hợp lệ")
        @ValidInteger
        private Integer tuoiMax;

        @NotBlank(message = "Hướng dẫn sử dụng không được để trống")
        @Size(max = 255, message = "Hướng dẫn sử dụng không được vượt quá 255 ký tự")
        private String hdsd;

        @NotNull(message = "Trạng thái không được để trống")
        @Min(value = 0, message = "Trạng thái không hợp lệ")
        @Max(value = 4, message = "Trạng thái không hợp lệ")
        private Integer trangThai;

        @Size(max = 255, message = "Mô tả không được vượt quá 255 ký tự")
        private String moTa;


        @NotNull(message = "Danh mục không được để trống")
        private String idDanhMuc;

        @NotNull(message = "Thương hiệu không được để trống")
        private String idThuongHieu;

        private String idGiamGia;
    }
