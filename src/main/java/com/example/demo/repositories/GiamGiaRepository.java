    package com.example.demo.repositories;

    import com.example.demo.entities.GiamGia;
    import org.springframework.data.jpa.repository.JpaRepository;
    import org.springframework.data.jpa.repository.Query;
    import org.springframework.data.repository.query.Param;

    import java.time.LocalDateTime;

    public interface GiamGiaRepository extends JpaRepository<GiamGia,String> {
        @Query("SELECT gg FROM GiamGia gg where gg.ma=:ma")
        GiamGia getByMa(@Param("ma")String ma);
        @Query("SELECT gg FROM GiamGia gg WHERE gg.ten = :ten " +
                "AND gg.id<>:id " +
                "AND " +
                "((:ngayBatDau >= gg.ngayBatDau AND :ngayBatDau <= gg.ngayKetThuc) " +
                "OR (:ngayKetThuc >= gg.ngayBatDau AND :ngayKetThuc <= gg.ngayKetThuc) " +
                "OR (gg.ngayBatDau >= :ngayBatDau AND gg.ngayBatDau <= :ngayKetThuc))")
        GiamGia getByNameAndTimeOverlap(@Param("ten") String ten,
                                        @Param("ngayBatDau") LocalDateTime ngayBatDau,
                                        @Param("ngayKetThuc") LocalDateTime ngayKetThuc,
                                        @Param("id")String id);
        @Query("SELECT gg FROM GiamGia gg where gg.ma=:ma and gg.id<>:id")
        GiamGia getByMaAndId(@Param("ma")String ma,@Param("id")String id);

    }
