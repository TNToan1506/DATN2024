package com.example.demo.repositories;

import com.example.demo.entities.LoaiSanPham;
import com.example.demo.entities.ThuongHieu;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ThuongHieuRepository extends JpaRepository<ThuongHieu,String> {
    @Query("SELECT th FROM ThuongHieu th where th.ma=:ma")
    ThuongHieu getByMa(@Param("ma")String ma);
    @Query("SELECT th FROM ThuongHieu th where th.ten=:ten")
    ThuongHieu getByName(@Param("ten")String ten);
    @Query("SELECT th FROM ThuongHieu th where th.id<>:id and th.ma=:ma")
    ThuongHieu getByIdAndMa(@Param("id")String id,@Param("ma")String ma);
    @Query("SELECT th FROM ThuongHieu th where th.id<>:id and th.ten=:ten")
    ThuongHieu getByNameAndId(@Param("ten")String ten,@Param("id")String id);
}
