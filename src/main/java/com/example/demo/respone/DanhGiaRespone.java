package com.example.demo.respone;

import com.example.demo.entities.ChiTietSanPham;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DanhGiaRespone {
    private String id;

    private int sao;

    private String nhanXet;


    private int trangThai;

    private LocalDateTime ngayDanhGia;

    private LocalDateTime ngaySua;

    private String maCTSP;
}
