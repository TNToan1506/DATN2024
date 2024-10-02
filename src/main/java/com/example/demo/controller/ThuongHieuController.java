package com.example.demo.controller;

import com.example.demo.entities.SanPham;
import com.example.demo.entities.ThuongHieu;
import com.example.demo.repositories.ThuongHieuRepository;
import com.example.demo.request.ThuongHieuRequest;
import com.example.demo.respone.ThuongHieuResponse;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@RestController
@RequestMapping("thuong-hieu")
public class ThuongHieuController {

    @Autowired
    ThuongHieuRepository thuongHieuRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        return ResponseEntity.ok(thuongHieuRepository.findAll().stream()
                .map(ThuongHieu::toResponse).collect(Collectors.toList()));
    }
    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        Optional<ThuongHieu> existingThuonHieu = thuongHieuRepository.findById(id);
        if (existingThuonHieu.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }
        return ResponseEntity.ok(existingThuonHieu.get().toResponse());
    }
    @PostMapping("/detailByMa")
    public ResponseEntity<?> detailByMa(@RequestBody Map<String, String> body) {
        String ma = body.get("ma");
        if (ma == null || ma.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã không được để trống.");
        }
        if (!Pattern.matches("^TH[A-Z0-9]{8}$", ma)) {
            return ResponseEntity.badRequest().body("Mã thương hiệu phải có định dạng THXXXXXXXX (X là chữ cái hoặc số)!");
        }
        ThuongHieu thuongHieu = thuongHieuRepository.getByMa(ma);
        if (thuongHieu==null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có mã: " + ma);
        }
        return ResponseEntity.ok(thuongHieu.toResponse());
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody ThuongHieuRequest thuongHieuRequest) {
        if (thuongHieuRepository.getByName(thuongHieuRequest.getTen().trim()) != null) {
            return ResponseEntity.badRequest().body("Tên không được trùng!");
        }

        String maThuongHieu = "TH" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        while (thuongHieuRepository.getByMa(maThuongHieu) != null) {
            maThuongHieu = "TH" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        }

        ThuongHieu thuongHieu = new ThuongHieu();
        BeanUtils.copyProperties(thuongHieuRequest, thuongHieu);
        thuongHieu.setMa(maThuongHieu);
        thuongHieu.setNgayTao(LocalDateTime.now());
        thuongHieu.setNgaySua(null);

        thuongHieuRepository.save(thuongHieu);
        return ResponseEntity.ok("Thêm thương hiệu thành công!");
    }


    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody ThuongHieuRequest thuongHieuRequest) {
        String id = thuongHieuRequest.getId();  // Lấy id từ ThuongHieuRequest

        Optional<ThuongHieu> optionalThuongHieu = thuongHieuRepository.findById(id);
        if (optionalThuongHieu.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + id);
        }

        String tenThuongHieu = thuongHieuRequest.getTen().trim();
        if (thuongHieuRepository.getByNameAndId(tenThuongHieu, id) != null) {
            return ResponseEntity.badRequest().body("Tên không được trùng!");
        }

        ThuongHieu thuongHieuUpdate = optionalThuongHieu.get();
        thuongHieuUpdate.setTen(tenThuongHieu);
        thuongHieuUpdate.setTrangThai(thuongHieuRequest.getTrangThai());
        thuongHieuUpdate.setXuatXu(thuongHieuRequest.getXuatXu());
        thuongHieuUpdate.setMoTa(thuongHieuRequest.getMoTa());
        thuongHieuUpdate.setNgaySua(LocalDateTime.now());

        thuongHieuRepository.save(thuongHieuUpdate);
        return ResponseEntity.ok("Cập nhật thương hiệu thành công!");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        if (thuongHieuRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + id);
        }
        thuongHieuRepository.deleteById(id);
        return ResponseEntity.ok("Xóa thương hiệu thành công!");
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }

}
