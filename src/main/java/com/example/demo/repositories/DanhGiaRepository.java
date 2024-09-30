package com.example.demo.repositories;

import com.example.demo.entities.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DanhGiaRepository extends JpaRepository<DanhGia,String> {
}
