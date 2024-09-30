package com.example.demo.entities;

import com.example.demo.respone.ChiTietSanPhamResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "CHITIETSANPHAM")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPham {

    @Id
    @Column(name = "id")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(name = "ma")
    private String ma;

    @Column(name = "gia")
    private String gia;


    @Column(name = "soNgaySuDung")
    private String soNgaySuDung;


    @Column(name = "HDSD")
    private String hdsd;

    @Column(name = "HSD")
    private LocalDateTime hsd;

    @Column(name = "ngaySanXuat")
    private LocalDateTime ngaySanXuat;

    @Column(name = "ngayNhap")
    private LocalDateTime ngayNhap;

    @Column(name = "soLuong")
    private int soLuong;


    @Column(name = "trangThai")
    private int trangThai;

    @Column(name = "ngayTao")
    private LocalDateTime ngayTao;

    @Column(name = "ngaySua")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "idSP")
    SanPham sanPham;

    public ChiTietSanPhamResponse toChiTietSanPhamResponse() {
        return new ChiTietSanPhamResponse(id, ma, gia, soNgaySuDung, hdsd,
                ngaySanXuat,hsd,ngayNhap,soLuong,trangThai,ngayTao,
                ngaySua,sanPham.getMaSP());
    }
}
