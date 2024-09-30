package com.example.demo.entities;

import com.example.demo.respone.LoaiSanPhamResponse;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "LOAISP")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoaiSanPham {
    @Id
    @Column(name = "ID")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

   @Column(name = "MA")
    private String ma;

    @Column(name = "TEN")
    private String ten;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;


    @Column(name = "TRANGTHAI")
    private Integer trangThai;
    @ManyToOne
    @JoinColumn(name = "IDDANHMUC")
    private DanhMuc danhMuc;
    public LoaiSanPhamResponse toResponse(){
        return new LoaiSanPhamResponse(id, ma,ten,ngayTao,ngaySua, trangThai, danhMuc.getTen());
    }
}
