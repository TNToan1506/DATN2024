package com.example.demo.repositories;

import com.example.demo.entities.AnhCTSP;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface AnhCTSPRepository extends JpaRepository<AnhCTSP,String> {
    @Query("SELECT actsp FROM AnhCTSP actsp WHERE actsp.link=:link AND actsp.chiTietSanPham.id=:idCTSP")
    AnhCTSP checkTrungAdd(@Param("link")String link,@Param("idCTSP")String idCTSP);
    @Query("SELECT actsp FROM AnhCTSP actsp WHERE actsp.link=:link AND actsp.chiTietSanPham.id=:idCTSP AND actsp.id<>:id")
    AnhCTSP checkTrungUpdate(@Param("link")String link,@Param("idCTSP")String idCTSP,@Param("id")String id);
}
