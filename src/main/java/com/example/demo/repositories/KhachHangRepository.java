package com.example.demo.repositories;

import com.example.demo.entities.KhachHang;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KhachHangRepository extends JpaRepository<KhachHang,String> {
}
