package com.example.demo.repositories;

import com.example.demo.entities.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface SanPhamRepository extends JpaRepository<SanPham,String> {
    @Query("SELECT sp FROM SanPham  sp where sp.maSP=:ma")
    SanPham getByMa(@Param("ma")String ma);
    @Query("SELECT sp FROM SanPham sp WHERE sp.tenSP = :tenSP")
    SanPham getByName(@Param("tenSP") String tenSP);
    @Query("SELECT sp FROM SanPham  sp where sp.maSP=:ma and sp.id<>:id")
    SanPham getByMaAndId(@Param("ma")String ma,@Param("id")String id);
    @Query("SELECT sp FROM SanPham sp WHERE sp.tenSP = :ten AND sp.id <> :id")
    SanPham getByNameAndId(@Param("ten") String ten, @Param("id") String id);
}
