package com.example.demo.controller;

import com.example.demo.entities.SanPham;
import com.example.demo.repositories.DanhMucRepository;
import com.example.demo.repositories.GiamGiaRepository;
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
    DanhMucRepository danhMucRepository;
    @Autowired
    ThuongHieuRepository thuongHieuRepository;
    @Autowired
    GiamGiaRepository giamGiaRepository;
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
    @GetMapping("/detail")
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
        SanPham sanPham = new SanPham();
        if (sanPhamRepository.getByName(sanPhamRequest.getTenSP().trim())!=null){
            return ResponseEntity.badRequest().body("Tên sản phẩm không được trùng!");
        }
        String maSanPham = "SP" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        while (sanPhamRepository.getByMa(maSanPham) != null) {
            maSanPham = "SP" + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
        }
        if (danhMucRepository.findById(sanPhamRequest.getIdDanhMuc().trim()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: " + sanPhamRequest.getIdDanhMuc().trim());
        }

        if (thuongHieuRepository.findById(sanPhamRequest.getIdThuongHieu().trim()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + sanPhamRequest.getIdThuongHieu().trim());
        }
        if (sanPhamRequest.getIdGiamGia()==null){
            sanPham.setGiamGia(null);
        }
        else if (sanPhamRequest.getIdGiamGia()!=null&giamGiaRepository.findById(sanPhamRequest.getIdGiamGia()).isEmpty()){
            return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + sanPhamRequest.getIdGiamGia());
        }

        BeanUtils.copyProperties(sanPhamRequest, sanPham);

        sanPham.setMaSP(maSanPham);
        sanPham.setNgayTao(LocalDateTime.now());
        sanPham.setNgaySua(null);
        sanPham.setDanhMuc(danhMucRepository.getById(sanPhamRequest.getIdDanhMuc().trim()));
        sanPham.setThuongHieu(thuongHieuRepository.getById(sanPhamRequest.getIdThuongHieu().trim()));
        if (sanPhamRequest.getIdGiamGia()!=null){
            sanPham.setGiamGia(giamGiaRepository.getById(sanPhamRequest.getIdGiamGia().trim()));
        }
        sanPhamRepository.save(sanPham);
        return ResponseEntity.ok("Thêm sản phẩm thành công!");
    }

    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody SanPhamRequest sanPhamRequest) {
        String id = sanPhamRequest.getId();
        if (id == null || id.trim().isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        Optional<SanPham> optionalSanPham = sanPhamRepository.findById(id.trim());

        if (optionalSanPham.isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm có id: " + id);
        }
        if (sanPhamRepository.getByNameAndId(sanPhamRequest.getTenSP().trim(),id)!=null){
            return ResponseEntity.badRequest().body("Tên sản phẩm không được trùng!");
        }
        if (sanPhamRequest.getTuoiMin() > sanPhamRequest.getTuoiMax()) {
                return ResponseEntity.badRequest().body("Tuổi tối thiểu không được lớn hơn tuổi tối đa!");
        }

        if (danhMucRepository.findById(sanPhamRequest.getIdDanhMuc().trim()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy danh mục có id: " + sanPhamRequest.getIdDanhMuc().trim());
        }

        if (thuongHieuRepository.findById(sanPhamRequest.getIdThuongHieu().trim()).isEmpty()) {
            return ResponseEntity.badRequest().body("Không tìm thấy thương hiệu có id: " + sanPhamRequest.getIdThuongHieu().trim());
        }
        if (sanPhamRequest.getIdGiamGia()==null){
            optionalSanPham.get().setGiamGia(null);
        }
        else if (giamGiaRepository.findById(sanPhamRequest.getIdGiamGia().trim()).isEmpty()){
            return ResponseEntity.badRequest().body("Không tìm thấy giảm giá có id: " + sanPhamRequest.getIdGiamGia().trim());
        }
        SanPham existingSanPham = optionalSanPham.get();
        BeanUtils.copyProperties(sanPhamRequest, existingSanPham, "id", "ngayTao","maSP");
        existingSanPham.setNgaySua(LocalDateTime.now());
        existingSanPham.setDanhMuc(danhMucRepository.getById(sanPhamRequest.getIdDanhMuc().trim()));
        existingSanPham.setThuongHieu(thuongHieuRepository.getById(sanPhamRequest.getIdThuongHieu().trim()));
        if (sanPhamRequest.getIdGiamGia()!=null){
            existingSanPham.setGiamGia(giamGiaRepository.getById(sanPhamRequest.getIdGiamGia().trim()));
        }
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
