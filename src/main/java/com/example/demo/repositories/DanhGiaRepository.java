package com.example.demo.repositories;

import com.example.demo.entities.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface DanhGiaRepository extends JpaRepository<DanhGia,String> {
    @Modifying
    @Transactional
    @Query("DELETE FROM DanhGia dg WHERE dg.chiTietSanPham.id = :idCTSP")
    void deleteByIdCTSP(@Param("idCTSP") String idCTSP);
}
