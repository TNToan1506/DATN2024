package com.example.demo.repositories;

import com.example.demo.entities.DanhMuc;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DanhMucRepository extends JpaRepository<DanhMuc,String> {
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.ma=:ma")
    DanhMuc getByMa(@Param("ma")String ma);
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.ten=:ten")
    DanhMuc getByName(@Param("ten")String ten);
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.ma=:ma AND dm.id<>:id")
    DanhMuc getByMaAndId(@Param("ma")String ma,@Param("id")String id);
    @Query("SELECT dm FROM DanhMuc dm WHERE dm.ten=:ten AND dm.id<>:id")
    DanhMuc getByNameAndId(@Param("id")String id,@Param("ten")String ten);

}
