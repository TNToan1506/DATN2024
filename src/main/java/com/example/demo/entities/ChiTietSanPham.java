package com.example.demo.entities;

import com.example.demo.respone.ChiTietSanPhamResponse;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Entity
@Table(name = "CHITIETSANPHAM")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChiTietSanPham {

    @Id
    @Column(name = "id")
    private String id ;

    @Column(name = "ma")
    private String ma;

    @Column(name = "gia")
    private String gia;


    @Column(name = "soNgaySuDung")
    private String soNgaySuDung;

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
    @JsonIgnore // Bỏ qua tham chiếu này khi serialize
    SanPham sanPham;

    @OneToMany(mappedBy = "chiTietSanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AnhCTSP> anhCTSP = new ArrayList<>(); // Danh sách ảnh liên kết

    @PrePersist
    public void prePersist() {
        if (this.id == null) {
            this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        }
    }

    public ChiTietSanPhamResponse toChiTietSanPhamResponse() {
        List<String> linkAnhList = anhCTSP != null
                ? anhCTSP.stream().map(AnhCTSP::getLink).collect(Collectors.toList())
                : new ArrayList<>(); // Trả về danh sách rỗng nếu không có hình ảnh
        if (sanPham.getGiamGia() != null &&
                sanPham.getGiamGia().getNgayKetThuc().isAfter(LocalDateTime.now()) &&
                sanPham.getGiamGia().getNgayBatDau().isBefore(LocalDateTime.now())) {
            double giaGiam = Double.valueOf(sanPham.getGiamGia().getGiaGiam()) / 100;
            gia = String.valueOf(Double.valueOf(this.gia) * (1 - giaGiam));
        }
        return new ChiTietSanPhamResponse(
                id,
                ma,
                gia,
                soNgaySuDung,
                ngaySanXuat,
                hsd,
                ngayNhap,
                soLuong,
                trangThai,
                ngayTao,
                ngaySua,
                sanPham.getMaSP(),
                 linkAnhList // Include the image link in the response
        );
    }
}
