package com.example.demo.repositories;

import com.example.demo.entities.AnhCTSP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

public interface AnhCTSPRepository extends JpaRepository<AnhCTSP,String> {
    @Query("SELECT actsp FROM AnhCTSP actsp WHERE actsp.link=:link AND actsp.chiTietSanPham.id=:idCTSP")
    AnhCTSP checkTrungAdd(@Param("link")String link,@Param("idCTSP")String idCTSP);
    @Query("SELECT actsp FROM AnhCTSP actsp WHERE actsp.link=:link AND actsp.chiTietSanPham.id=:idCTSP AND actsp.id<>:id")
    AnhCTSP checkTrungUpdate(@Param("link")String link,@Param("idCTSP")String idCTSP,@Param("id")String id);
    @Query("SELECT actsp FROM AnhCTSP actsp WHERE actsp.chiTietSanPham.id=:idCTSP")
    AnhCTSP getByIdCTSP(@Param("idCTSP")String idCTSP);
    @Modifying
    @Transactional
    @Query("DELETE FROM AnhCTSP actsp WHERE actsp.chiTietSanPham.id = :idCTSP")
    void deleteByIdCTSP(@Param("idCTSP") String idCTSP);
}
