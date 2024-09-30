package com.example.demo.respone;

import com.example.demo.entities.LoaiSanPham;
import com.example.demo.entities.ThuongHieu;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    private String tenSanPham;

    private String tenThuongHieu;

    private String tenGiamGia;
}
