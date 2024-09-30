package com.example.demo.repositories;

import com.example.demo.entities.LoaiSanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoaiSanPhamRepository extends JpaRepository<LoaiSanPham,String> {
    @Query("SELECT loaiSP FROM LoaiSanPham loaiSP where loaiSP.ma=:ma")
    LoaiSanPham getLSPByMa(@Param("ma")String ma);
    @Query("SELECT loaiSP FROM LoaiSanPham loaiSP where loaiSP.ten=:ten")
    LoaiSanPham getLSPByName(@Param("ten")String ten);
    @Query("SELECT loaiSP FROM LoaiSanPham loaiSP where loaiSP.id<>:id and  loaiSP.ma=:ma ")
    LoaiSanPham getLSPByMaAndId(@Param("ma")String ma,@Param("id")String id);
    @Query("SELECT loaiSP FROM LoaiSanPham loaiSP where loaiSP.ten=:ten and loaiSP.id<>:id")
    LoaiSanPham getLSPByNameAndId(@Param("ten")String ten,@Param("id")String id);
}
