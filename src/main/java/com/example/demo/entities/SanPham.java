    package com.example.demo.entities;

    import com.example.demo.respone.SanPhamResponse;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotBlank;
    import jakarta.validation.constraints.Size;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;
    import java.util.ArrayList;
    import java.util.List;
    import java.util.UUID;

    @Entity
    @Table(name = "SANPHAM")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class SanPham {
        @Id
        @Column(name = "id")
        private String id  ;

        @Column(name = "ma")
        private String maSP;

        @Column(name = "ten")
        private String tenSP;

        @Column(name = "thanhPhan")
        private String thanhPhan;

        @Column(name = "congDung")
        private String congDung;

        @Column(name = "tuoiMin")
        private Integer tuoiMin;

        @Column(name = "tuoiMax")
        private Integer tuoiMax;

        @Column(name = "hdsd")
        private String hdsd;

        @Column(name = "ngayTao")
        private LocalDateTime ngayTao;

        @Column(name = "ngaySua")
        private LocalDateTime ngaySua;


        @Column(name = "trangThai")
        private Integer trangThai;

        @Column(name = "moTa")
        private String moTa;

        @ManyToOne
        @JoinColumn(name = "idDanhMuc")
        private DanhMuc danhMuc;

        @ManyToOne
        @JoinColumn(name = "idThuongHieu")
        private ThuongHieu thuongHieu;

        @ManyToOne
        @JoinColumn(name = "idGiamGia")
        private GiamGia giamGia;

        @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
        private List<ChiTietSanPham> listCTSP = new ArrayList<>(); // Danh sách ảnh liên kết

        @PrePersist
        public void prePersist() {
            if (this.id == null) {
                this.id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            }
        }

        public SanPhamResponse toResponse() {
            String giamGiaTen = (giamGia != null) ? giamGia.getTen() : null; // Check for null in giamGia

            // Calculate total quantity where trangThai = 1
            int tongSoLuong = listCTSP.stream()
                    .filter(ctsp -> ctsp.getTrangThai() == 1) // Only include items with trangThai = 1
                    .mapToInt(ChiTietSanPham::getSoLuong)
                    .sum();

            return new SanPhamResponse(
                    id,
                    maSP,
                    tenSP,
                    thanhPhan,
                    congDung,
                    tuoiMin,
                    tuoiMax,
                    tongSoLuong,  // Include total quantity
                    ngayTao,
                    ngaySua,
                    hdsd,
                    trangThai,
                    moTa,
                    danhMuc.getTen(),
                    thuongHieu.getTen(),
                    giamGiaTen  // Pass the discount name to the response
            );
        }

    }
