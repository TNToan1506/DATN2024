package com.example.demo.controller;

import com.example.demo.entities.LoaiSanPham;
import com.example.demo.entities.SanPham;
import com.example.demo.entities.ThuongHieu;
import com.example.demo.repositories.LoaiSanPhamRepository;
import com.example.demo.repositories.SanPhamRepository;
import com.example.demo.repositories.ThuongHieuRepository;
import com.example.demo.request.SanPhamRequest;
import jakarta.validation.Valid;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;

@RestController
@RequestMapping("san-pham")
public class SanPhamController {

    @Autowired
    SanPhamRepository sanPhamRepository;
    @Autowired
    LoaiSanPhamRepository loaiSanPhamRepository;
    @Autowired
    ThuongHieuRepository thuongHieuRepository;
    @GetMapping()
    public ResponseEntity<?> getAll() {
        Sort sort = Sort.by(Sort.Direction.DESC, "ngayTao");
        List<SanPham> sanPhamList = sanPhamRepository.findAll(sort);
        return  ResponseEntity.ok(sanPhamList.stream().map(SanPham::toResponse));
    }
    @GetMapping("/phanTrang")
    public ResponseEntity<?>phanTrang(@RequestParam(name = "page",defaultValue = "0")Integer page){
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        return ResponseEntity.ok(sanPhamRepository.findAll(pageRequest));
    }
    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        Optional<SanPham> existingSanPham = sanPhamRepository.findById(id);
        if (existingSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }
        return ResponseEntity.ok(existingSanPham.get().toResponse());
    }
    @PostMapping("/detailByMa")
    public ResponseEntity<?> detailByMa(@RequestBody Map<String, String> body) {
        String ma = body.get("ma");
        if (ma == null || ma.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Mã không được để trống!");
        }
        if (!Pattern.matches("^SP[A-Z0-9]{8}$", ma)) {
            return ResponseEntity.badRequest().body("Mã sản phẩm phải có định dạng SPXXXXXXXX (X là chữ cái hoặc số)!");
        }
        SanPham sanPham= sanPhamRepository.getByMa(ma);
        if (sanPham==null) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có mã: " + ma);
        }
        return ResponseEntity.ok(sanPham.toResponse());
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody SanPhamRequest sanPhamRequest) {
        if (sanPhamRepository.getByName(sanPhamRequest.getTenSP().trim())!=null){
            return ResponseEntity.badRequest().body("Tên sản phẩm không được trùng!");
        }
        String maSanPham = sanPhamRequest.getMaSP();
        if (maSanPham == null || maSanPham.trim().isEmpty()) {
            String prefix = "SP";
            String uniqueID;
            do {
                uniqueID = UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
            } while (sanPhamRepository.getByMa(prefix + uniqueID) != null);
            maSanPham = prefix + uniqueID;
        } else {
            maSanPham = maSanPham.trim();
            if (!Pattern.matches("^SP[A-Z0-9]{8}$", maSanPham)) {
                return ResponseEntity.badRequest().body("Mã sản phẩm phải có định dạng SPXXXXXXXX (X là chữ cái hoặc số)!");
            }
            if (sanPhamRepository.getByMa(maSanPham) != null) {
                return ResponseEntity.badRequest().body("Mã sản phẩm không được trùng!");
            }
        }

        if (loaiSanPhamRepository.findById(sanPhamRequest.getIdSanPham()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy loại sản phẩm có id: " + sanPhamRequest.getIdSanPham());
        }

        if (thuongHieuRepository.findById(sanPhamRequest.getIdThuongHieu()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + sanPhamRequest.getIdThuongHieu());
        }

        SanPham sanPham = new SanPham();
        BeanUtils.copyProperties(sanPhamRequest, sanPham);
        sanPham.setMaSP(maSanPham);
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgaySua(null);
        sanPham.setLoaiSanPham(loaiSanPhamRepository.getById(sanPhamRequest.getIdSanPham()));
        sanPham.setThuongHieu(thuongHieuRepository.getById(sanPhamRequest.getIdThuongHieu()));

        sanPhamRepository.save(sanPham);
        return ResponseEntity.ok("Thêm sản phẩm thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody SanPhamRequest sanPhamRequest,@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        Optional<SanPham> optionalSanPham = sanPhamRepository.findById(id);

        if (optionalSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }
        if (sanPhamRepository.getByNameAndId(sanPhamRequest.getTenSP().trim(),id)!=null){
            return ResponseEntity.badRequest().body("Tên sản phẩm không được trùng!");
        }
//        String maSanPham = sanPhamRequest.getMaSP();
//        if (maSanPham != null && !maSanPham.trim().isEmpty()) {
//            maSanPham = maSanPham.trim();
//            if (!Pattern.matches("^SP[A-Z0-9]{8}$", maSanPham)) {
//                return ResponseEntity.badRequest().body("Mã sản phẩm phải có định dạng SPXXXXXXXX (X là chữ cái hoặc số)!");
//            }
//            if (sanPhamRepository.getByMaAndId(maSanPham,id) != null) {
//                return ResponseEntity.badRequest().body("Mã sản phẩm không được trùng!");
//            }
//        }
        if (sanPhamRequest.getTuoiMin() > sanPhamRequest.getTuoiMax()) {
                return ResponseEntity.badRequest().body("Tuổi tối thiểu không được lớn hơn tuổi tối đa!");
        }

        if (loaiSanPhamRepository.findById(sanPhamRequest.getIdSanPham()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy loại sản phẩm có id: " + sanPhamRequest.getIdSanPham());
        }

        if (thuongHieuRepository.findById(sanPhamRequest.getIdThuongHieu()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + sanPhamRequest.getIdThuongHieu());
        }

        SanPham existingSanPham = optionalSanPham.get();
        BeanUtils.copyProperties(sanPhamRequest, existingSanPham, "id", "ngayTao","maSP");
        existingSanPham.setNgaySua(LocalDateTime.now());
        existingSanPham.setLoaiSanPham(loaiSanPhamRepository.getById(sanPhamRequest.getIdSanPham()));
        existingSanPham.setThuongHieu(thuongHieuRepository.getById(sanPhamRequest.getIdThuongHieu()));

        sanPhamRepository.save(existingSanPham);
        return ResponseEntity.ok("Cập nhật sản phẩm thành công!");
    }


    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        if (sanPhamRepository.findById(id).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }
        sanPhamRepository.deleteById(id);
        return ResponseEntity.ok("Xóa sản phẩm thành công!");
    }

    // Handle validation errors
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error -> {
            errors.put(error.getField(), error.getDefaultMessage());
        });
        return ResponseEntity.badRequest().body(errors);
    }
}
