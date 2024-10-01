    package com.example.demo.entities;

    import com.example.demo.respone.SanPhamResponse;
    import jakarta.persistence.*;
    import lombok.AllArgsConstructor;
    import lombok.Data;
    import lombok.NoArgsConstructor;

    import java.time.LocalDateTime;
    import java.util.UUID;

    @Entity
    @Table(name = "SANPHAM")
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public class SanPham {
        @Id
        @Column(name = "id")
        private String id = UUID.randomUUID().toString().substring(0, 8).toUpperCase();

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

        public SanPhamResponse toResponse(){
            return new SanPhamResponse(id, maSP,tenSP,thanhPhan,congDung,tuoiMin,tuoiMax,
                    ngayTao,ngaySua, trangThai,moTa,
                    danhMuc.getTen(), thuongHieu.getTen(),giamGia.getTen());
        }
    }
