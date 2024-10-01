package com.example.demo.respone;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SanPhamResponse {

    private String id;

    private String maSP;

    private String tenSP;

    private String thanhPhan;

    private String congDung;

    private Integer tuoiMin;

    private Integer tuoiMax;

    private LocalDateTime ngayTao;

    private LocalDateTime ngaySua;

    private Integer trangThai;

    private String moTa;

    private String tenDanhMuc;

    private String tenThuongHieu;

    private String tenGiamGia;
}
