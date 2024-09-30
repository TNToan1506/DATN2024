package com.example.demo.entities;

import com.example.demo.respone.DanhGiaRespone;
import com.example.demo.validation.ValidLocalDateTime;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "DANHGIA")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanhGia {

    @Id
    @Column(name = "ID")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(name = "SAO")
    private int sao;

    @Column(name = "NHANXET")
    private String nhanXet;


    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "NGAYDANHGIA")
    @ValidLocalDateTime
    private LocalDateTime ngayDanhGia;

    @Column(name = "NGAYSUA")
    @ValidLocalDateTime
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    private ChiTietSanPham chiTietSanPham;

    public DanhGiaRespone toRespone(){
        return new DanhGiaRespone(id, sao, nhanXet,trangThai, ngayDanhGia, ngaySua, chiTietSanPham != null ? chiTietSanPham.getMa() : null);
    }
}
