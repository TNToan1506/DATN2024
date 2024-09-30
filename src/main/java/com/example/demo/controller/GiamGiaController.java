package com.example.demo.controller;

import com.example.demo.entities.DanhMuc;
import com.example.demo.entities.GiamGia;
import com.example.demo.repositories.GiamGiaRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

@RestController
@RequestMapping("giam-gia")
public class GiamGiaController {

    @Autowired
    GiamGiaRepository giamGiaRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayTao");
        List<GiamGia> giamGiaList = giamGiaRepository.findAll(sort);
        return ResponseEntity.ok(giamGiaList);
    }
    @GetMapping("/phanTrang")
    public ResponseEntity<?> phanTrang(@RequestParam(name = "page",defaultValue = "0")Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return ResponseEntity.ok(giamGiaRepository.findAll(pageRequest));
    }
    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        GiamGia giamGia = giamGiaRepository.findById(id).orElse(null);
        if (giamGia != null) {
            return ResponseEntity.ok(giamGia);
        }
        return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + id);
    }
    @PostMapping("/detailByMa")
    public ResponseEntity<?> detailByMa(@RequestBody Map<String, String> request) {
        String ma = request.get("ma");
        if (ma == null || ma.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã không được để trống.");
        }
        if (!Pattern.matches("^GG[A-Z0-9]{8}$", ma.trim())) {
            return ResponseEntity.badRequest().body("Mã phải có định dạng GGXXXXXXXX (X là chữ cái hoặc số)!");
        }
        GiamGia giamGia = giamGiaRepository.getByMa(ma);
        if (giamGia != null) {
            return ResponseEntity.ok(giamGia);
        }
        return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có mã: " +ma);
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody GiamGia giamGia) {
        giamGia.setNgayTao(LocalDateTime.now());
        giamGia.setNgaySua(null);

        if (giamGia.getMa() == null || giamGia.getMa().trim().isEmpty()) {
            String prefix = "GG";
            String uniqueID;
            do {
                uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            } while (giamGiaRepository.getByMa(prefix + uniqueID) != null);
            giamGia.setMa(prefix + uniqueID);
        } else {
            if (!Pattern.matches("^GG[A-Z0-9]{8}$", giamGia.getMa().trim())) {
                return ResponseEntity.badRequest().body("Mã phải có định dạng GGXXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (giamGiaRepository.getByMa(giamGia.getMa().trim()) != null) {
                return ResponseEntity.badRequest().body("Mã giảm giá không được trùng!");
            }
        }
        if (giamGia.getNgayBatDau().isAfter(giamGia.getNgayKetThuc())) {
            return ResponseEntity.badRequest().body("Ngày bắt đầu phải trước ngày kết thúc!");
        }
        if (!isValidDateFormat(giamGia.getNgayBatDau()) || !isValidDateFormat(giamGia.getNgayKetThuc())) {
            return ResponseEntity.badRequest().body("Ngày phải có định dạng yyyy-MM-dd HH:mm:ss!");
        }
        GiamGia existingGiamGia = giamGiaRepository.getByNameAndTimeOverlap(
                giamGia.getTen(),
                giamGia.getNgayBatDau(),
                giamGia.getNgayKetThuc(),
                null);
        if (existingGiamGia != null) {
            return ResponseEntity.badRequest().body("Đã tồn tại giảm giá với tên hoặc có thời gian trùng lặp!");
        }
        String giaGiam = giamGia.getGiaGiam();
        boolean isPercentage = giaGiam.endsWith("%");
        if (isPercentage) {
            giaGiam = giaGiam.substring(0, giaGiam.length() - 1);
        }

        try {
            double giaGiamValue = Double.parseDouble(giaGiam);
            if (giaGiamValue < 0) {
                return ResponseEntity.badRequest().body("Giá giảm phải là số dương!");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Giá giảm không hợp lệ!");
        }

        giamGiaRepository.save(giamGia);
        return ResponseEntity.ok("Add done!");
    }
    private boolean isValidDateFormat(LocalDateTime date) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime.parse(date.format(formatter), formatter);
            return true;
        } catch (DateTimeParseException e) {
            return false;
        }
    }
    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody GiamGia giamGia, @RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        if (giamGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + id);
        }
        if (giamGia.getMa().trim() == null || giamGia.getMa().trim().isEmpty()) {
            String prefix = "GG";
            String uniqueID;
            do {
                uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            } while (giamGiaRepository.getByMa(prefix + uniqueID) != null);
            giamGia.setMa(prefix + uniqueID);
        } else {
            if (!Pattern.matches("^GG[A-Z0-9]{8}$", giamGia.getMa().trim())) {
                return ResponseEntity.badRequest().body("Mã phải có định dạng GGXXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (giamGiaRepository.getByMaAndId(giamGia.getMa(), id)!=null) {
                return ResponseEntity.badRequest().body("Mã giảm giá không được trùng!");
            }
        }

        if (!isValidDateFormat(giamGia.getNgayBatDau()) || !isValidDateFormat(giamGia.getNgayKetThuc())) {
            return ResponseEntity.badRequest().body("Ngày phải có định dạng yyyy-MM-dd HH:mm:ss!");
        }
        if (giamGia.getNgayBatDau().isAfter(giamGia.getNgayKetThuc())) {
            return ResponseEntity.badRequest().body("Ngày bắt đầu phải trước ngày kết thúc!");
        }
        GiamGia existingGiamGia = giamGiaRepository.getByNameAndTimeOverlap(
                giamGia.getTen(),
                giamGia.getNgayBatDau(),
                giamGia.getNgayKetThuc(),
                id);
        if (existingGiamGia != null) {
            return ResponseEntity.badRequest().body("Đã tồn tại giảm giá với tên hoặc có thời gian trùng lặp!");
        }
        String giaGiam = giamGia.getGiaGiam();
        boolean isPercentage = giaGiam.endsWith("%");
        if (isPercentage) {
            giaGiam = giaGiam.substring(0, giaGiam.length() - 1); // Loại bỏ ký tự %
        }

        try {
            double giaGiamValue = Double.parseDouble(giaGiam);
            if (giaGiamValue < 0) {
                return ResponseEntity.badRequest().body("Giá giảm phải là số dương!");
            }
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Giá giảm không hợp lệ!");
        }

        GiamGia giamGiaUpdate = giamGiaRepository.getReferenceById(id);

        giamGiaUpdate.setMa(giamGia.getMa());
        giamGiaUpdate.setTen(giamGia.getTen());
        giamGiaUpdate.setNgaySua(LocalDateTime.now());
        giamGiaUpdate.setNgayBatDau(giamGia.getNgayBatDau());
        giamGiaUpdate.setNgayKetThuc(giamGia.getNgayKetThuc());
        giamGiaUpdate.setGiaGiam(giamGia.getGiaGiam());
        giamGiaUpdate.setTrangThai(giamGia.getTrangThai());

        giamGiaRepository.save(giamGiaUpdate);
        return ResponseEntity.ok("Update done!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        if (giamGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + id);
        }
        giamGiaRepository.deleteById(id);
        return ResponseEntity.ok("Xóa giảm giá thành công!");
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
