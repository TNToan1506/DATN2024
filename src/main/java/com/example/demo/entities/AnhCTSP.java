package com.example.demo.entities;

import com.example.demo.respone.AnhCTSPResponse;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "ANHCTSP")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnhCTSP {
    @Id
    @Column(name = "ID")
    private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(name = "LINK")
    private String link;

    @Column(name = "TEN")
    private String ten;


    @Column(name = "TRANGTHAI")
    private int trangThai;

    @Column(name = "NGAYTAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGAYSUA")
    private LocalDateTime ngaySua;

    @ManyToOne
    @JoinColumn(name = "IDCTSP")
    private ChiTietSanPham chiTietSanPham;

    public AnhCTSPResponse toAnhSPResponse() {
        return new AnhCTSPResponse(
                id,
                link,
                ten,
                trangThai,
                ngayTao,
                ngaySua,
                chiTietSanPham != null ? chiTietSanPham.getMa() : null
        );
    }
}
