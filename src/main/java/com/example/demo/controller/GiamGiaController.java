package com.example.demo.controller;

import com.example.demo.entities.DanhMuc;
import com.example.demo.entities.GiamGia;
import com.example.demo.entities.SanPham;
import com.example.demo.repositories.GiamGiaRepository;
import com.example.demo.repositories.SanPhamRepository;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("giam-gia")
public class GiamGiaController {

    @Autowired
    GiamGiaRepository giamGiaRepository;
    @Autowired
    SanPhamRepository sanPhamRepository;
    @GetMapping()
    public ResponseEntity<?> getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayTao");
        List<GiamGia> giamGiaList = giamGiaRepository.findAll(sort);
        return ResponseEntity.ok(giamGiaList);
    }
    @GetMapping("/phanTrang")
    public ResponseEntity<?> phanTrang(@RequestParam(name = "page",defaultValue = "0")Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<GiamGia> giamGiaPage=giamGiaRepository.findAll(pageRequest);
        Map<String, Object> response = new HashMap<>();
        response.put("giamGias", giamGiaPage.getContent());
        response.put("totalPages", giamGiaPage.getTotalPages());
        response.put("totalElements", giamGiaPage.getTotalElements());
        return ResponseEntity.ok(response);
    }
    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam Map<String, String> request) {
        String id = request.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        GiamGia giamGia = giamGiaRepository.findById(id).orElse(null);
        if (giamGia != null) {
            Map<String, Object> response = new HashMap<>();
            response.put("listSanPham", giamGia.getListSanPham());  // Thêm danh sách sản phẩm
            response.put("giamGiaDetail", giamGia);  // Thêm chi tiết giảm giá
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + id);
    }
    @GetMapping("/detailByMa")
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
    public ResponseEntity<?> add(@Valid @ModelAttribute GiamGia giamGia,@RequestParam("selectedProducts") List<String> selectedProducts) {
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
        List<SanPham> sanPhams = new ArrayList<>();
        for (String productId : selectedProducts) {
            SanPham sanPham = sanPhamRepository.findById(productId).orElse(null);
            if (sanPham == null) {
                System.out.println("ID:"+productId);
                return ResponseEntity.badRequest().body("Sản phẩm với ID: " + productId + " không tồn tại!");
            }
            if (sanPham!=null){
                sanPham.setGiamGia(giamGia);
            }

            sanPhams.add(sanPham);
        }
        sanPhamRepository.saveAll(sanPhams); //
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
    public ResponseEntity<?> update(@Valid @ModelAttribute GiamGia giamGia, @RequestParam("selectedProducts") List<String> selectedProducts) {
        String id = giamGia.getId();
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        if (giamGiaRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + id);
        }

        // Kiểm tra mã giảm giá
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
            if (giamGiaRepository.getByMaAndId(giamGia.getMa().trim(), id) != null) {
                return ResponseEntity.badRequest().body("Mã giảm giá không được trùng!");
            }
        }

        // Kiểm tra định dạng ngày
        if (!isValidDateFormat(giamGia.getNgayBatDau()) || !isValidDateFormat(giamGia.getNgayKetThuc())) {
            return ResponseEntity.badRequest().body("Ngày phải có định dạng yyyy-MM-dd HH:mm:ss!");
        }
        if (giamGia.getNgayBatDau().isAfter(giamGia.getNgayKetThuc())) {
            return ResponseEntity.badRequest().body("Ngày bắt đầu phải trước ngày kết thúc!");
        }

        // Kiểm tra trùng lặp
        GiamGia existingGiamGia = giamGiaRepository.getByNameAndTimeOverlap(
                giamGia.getTen(),
                giamGia.getNgayBatDau(),
                giamGia.getNgayKetThuc(),
                id);
        if (existingGiamGia != null) {
            return ResponseEntity.badRequest().body("Đã tồn tại giảm giá với tên hoặc có thời gian trùng lặp!");
        }

        // Kiểm tra giá giảm
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

        // Cập nhật thông tin giảm giá
        GiamGia giamGiaUpdate = giamGiaRepository.getReferenceById(id);
        giamGiaUpdate.setMa(giamGia.getMa());
        giamGiaUpdate.setTen(giamGia.getTen());
        giamGiaUpdate.setNgaySua(LocalDateTime.now());
        giamGiaUpdate.setNgayBatDau(giamGia.getNgayBatDau());
        giamGiaUpdate.setNgayKetThuc(giamGia.getNgayKetThuc());
        giamGiaUpdate.setGiaGiam(giamGia.getGiaGiam());
        giamGiaUpdate.setTrangThai(giamGia.getTrangThai());

        // Cập nhật sản phẩm liên kết
        List<SanPham> sanPhams = new ArrayList<>();
        for (String productId : selectedProducts) {
            SanPham sanPham = sanPhamRepository.findById(productId).orElse(null);
            if (sanPham == null) {
                return ResponseEntity.badRequest().body("Sản phẩm với ID: " + productId + " không tồn tại!");
            }
            sanPham.setGiamGia(giamGiaUpdate);
            sanPhams.add(sanPham);
        }
        sanPhamRepository.saveAll(sanPhams);

        // Lưu cập nhật cho GiamGia
        giamGiaRepository.save(giamGiaUpdate);
        return ResponseEntity.ok("Cập nhật thành công!");
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
