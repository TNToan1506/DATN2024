package com.example.demo.controller;

import com.example.demo.entities.AnhCTSP;
import com.example.demo.entities.ChiTietSanPham;
import com.example.demo.repositories.AnhCTSPRepository;
import com.example.demo.repositories.ChiTietSanPhamRepository;
import com.example.demo.request.AnhCTSPRequest;
import com.example.demo.respone.AnhCTSPResponse;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("anh-sp")
public class AnhCTSPController {

    @Autowired
    private AnhCTSPRepository anhCTSPRepository;

    @Autowired
    private ChiTietSanPhamRepository chiTietSanPhamRepository;

    @GetMapping()
    public ResponseEntity<?> getAll() {
        List<AnhCTSP> anhCTSPList = anhCTSPRepository.findAll(Sort.by(Sort.Direction.DESC, "ngayTao"));
        List<AnhCTSPResponse> responseList = anhCTSPList.stream()
                .map(AnhCTSP::toAnhSPResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @GetMapping("/phanTrang")
    public ResponseEntity<?> phanTrang(@RequestParam(name = "page",defaultValue = "0") Integer page) {
        PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Direction.DESC, "ngayTao"));
        Page<AnhCTSP> anhCTSPPage = anhCTSPRepository.findAll(pageRequest);
        List<AnhCTSPResponse> responseList = anhCTSPPage.stream()
                .map(AnhCTSP::toAnhSPResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responseList);
    }

    @PostMapping("/detail")
    public ResponseEntity<?> detail(@RequestBody Map<String, String> request) {
        String id = request.get("id");
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        AnhCTSP anhCTSP = anhCTSPRepository.findById(id).orElse(null);
        if (anhCTSP != null) {
            return ResponseEntity.ok(anhCTSP.toAnhSPResponse());
        }
        return ResponseEntity.badRequest().body("Không tìm thấy ảnh có id: " + id);
    }
    @PostMapping("/add")
    public ResponseEntity<?> add(@Valid @RequestBody AnhCTSPRequest anhSPRequest) {
        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(anhSPRequest.getIdCTSP()).orElse(null);
        if (chiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy ChiTietSanPham có id: " + anhSPRequest.getIdCTSP());
        }

        AnhCTSP duplicate = anhCTSPRepository.checkTrungAdd(anhSPRequest.getLink().trim(), anhSPRequest.getIdCTSP());
        if (duplicate != null) {
            return ResponseEntity.badRequest().body("Ảnh đã tồn tại với liên kết này trong chi tiết sản phẩm.");
        }

        AnhCTSP anhSP = new AnhCTSP();
        BeanUtils.copyProperties(anhSPRequest, anhSP);
        anhSP.setNgayTao(LocalDateTime.now());
        anhSP.setNgaySua(null);
        anhSP.setLink(anhSP.getLink().trim());
        anhSP.setChiTietSanPham(chiTietSanPham);

        anhCTSPRepository.save(anhSP);
        return ResponseEntity.ok("Thêm ảnh thành công!");
    }


    @PutMapping("/update")
    public ResponseEntity<?> update(@Valid @RequestBody AnhCTSPRequest anhSPRequest) {
        String id = anhSPRequest.getId();
        AnhCTSP existingAnhSP = anhCTSPRepository.findById(id).orElse(null);
        if (existingAnhSP == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy ảnh có id: " + id);
        }

        ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.findById(anhSPRequest.getIdCTSP()).orElse(null);
        if (chiTietSanPham == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy ChiTietSanPham có id: " + anhSPRequest.getIdCTSP());
        }
        AnhCTSP duplicate = anhCTSPRepository.checkTrungUpdate(anhSPRequest.getLink().trim(), anhSPRequest.getIdCTSP(), id);
        if (duplicate != null) {
            return ResponseEntity.badRequest().body("Ảnh đã tồn tại với liên kết này trong chi tiết sản phẩm.");
        }
        BeanUtils.copyProperties(anhSPRequest, existingAnhSP, "id", "ngayTao");
        existingAnhSP.setChiTietSanPham(chiTietSanPham);
        existingAnhSP.setNgaySua(LocalDateTime.now());

        anhCTSPRepository.save(existingAnhSP);
        return ResponseEntity.ok("Cập nhật ảnh thành công!");
    }

    @DeleteMapping("/delete")
    public ResponseEntity<?> delete(@RequestBody Map<String, String> body) {
        String id = body.get("id");
        if (id == null || id.isEmpty()) {
            return ResponseEntity.badRequest().body("ID không được để trống.");
        }
        if (anhCTSPRepository.getById(id) == null) {
            return ResponseEntity.badRequest().body("Không tìm thấy ảnh có id: " + id);
        }
        anhCTSPRepository.deleteById(id);
        return ResponseEntity.ok("Xóa ảnh thành công!");
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
