    package com.example.demo.controller;

    import com.example.demo.entities.AnhCTSP;
    import com.example.demo.entities.ChiTietSanPham;
    import com.example.demo.entities.GiamGia;
    import com.example.demo.entities.SanPham;
    import com.example.demo.repositories.AnhCTSPRepository;
    import com.example.demo.repositories.ChiTietSanPhamRepository;
    import com.example.demo.repositories.GiamGiaRepository;
    import com.example.demo.repositories.SanPhamRepository;
    import com.example.demo.request.ChiTietSanPhamRequest;
    import com.example.demo.respone.ChiTietSanPhamResponse;
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
    import java.time.temporal.ChronoUnit;
    import java.util.HashMap;
    import java.util.List;
    import java.util.Map;
    import java.util.UUID;
    import java.util.regex.Pattern;
    import java.util.stream.Collectors;

    @CrossOrigin(origins = "*")
    @RestController
    @RequestMapping("chi-tiet-san-pham")
    public class ChiTietSanPhamController {
        @Autowired
        SanPhamRepository sanPhamRepository;
    //    @Autowired
    //    GiamGiaRepository giamGiaRepository;
        @Autowired
        ChiTietSanPhamRepository chiTietSanPhamRepository;
        @Autowired
        AnhCTSPRepository anhCTSPRepository;

        @GetMapping()
        public ResponseEntity<?> getAll(@RequestParam(name = "idSP", required = false) String idSP) {
            List<ChiTietSanPham> chiTietSanPhams=chiTietSanPhamRepository.getAllByIdSP(idSP, Sort.by(Sort.Order.desc("ngayTao")));
            if (chiTietSanPhams.isEmpty()) {
                return ResponseEntity.noContent().build(); // Trả về 204 No Content nếu không có sản phẩm nào
            }

            List<ChiTietSanPhamResponse> responseList = chiTietSanPhams.stream()
                    .map(ChiTietSanPham::toChiTietSanPhamResponse)
                    .collect(Collectors.toList());
            return ResponseEntity.ok(responseList);
        }


//
//        @GetMapping()
//        public ResponseEntity<?> getAll() {
//            List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findAll(Sort.by(Sort.Order.desc("ngayTao")));
//            List<ChiTietSanPhamResponse> responseList = chiTietSanPhams.stream()
//                    .map(ChiTietSanPham::toChiTietSanPhamResponse)
//                    .collect(Collectors.toList());
//
//            // Kiểm tra các sản phẩm sắp hết hạn
//            LocalDateTime now = LocalDateTime.now();
//            LocalDateTime sixMonthsFromNow = now.plus(6, ChronoUnit.MONTHS);
//
//            List<ChiTietSanPham> expiringSoonList = chiTietSanPhams.stream()
//                    .filter(ctsp -> ctsp.getHsd() != null && ctsp.getHsd().isBefore(sixMonthsFromNow))
//                    .collect(Collectors.toList());
//
//            if (!expiringSoonList.isEmpty()) {
//                return ResponseEntity.ok(new HashMap<String, Object>() {{
//                    put("products", responseList);
//                    put("message", "Có sản phẩm sắp hết hạn trong vòng 6 tháng. Xem chi tiết tại: /chi-tiet-san-pham/expiring-soon");
//                }});
//            }
//
//            return ResponseEntity.ok(responseList);
//        }

        @GetMapping("/page")
        public ResponseEntity<?> page(@RequestParam(name = "page", defaultValue = "0") Integer page) {
            PageRequest pageRequest = PageRequest.of(page, 5, Sort.by(Sort.Order.desc("ngayTao")));
            Page<ChiTietSanPham> chiTietSanPhamPage = chiTietSanPhamRepository.findAll(pageRequest);
            List<ChiTietSanPhamResponse> responseList = chiTietSanPhamPage.stream()
                    .map(ChiTietSanPham::toChiTietSanPhamResponse)
                    .collect(Collectors.toList());

            // Kiểm tra các sản phẩm sắp hết hạn
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sixMonthsFromNow = now.plus(6, ChronoUnit.MONTHS);

            List<ChiTietSanPham> expiringSoonList = chiTietSanPhamPage.getContent().stream()
                    .filter(ctsp -> ctsp.getHsd() != null && ctsp.getHsd().isBefore(sixMonthsFromNow))
                    .collect(Collectors.toList());

            if (!expiringSoonList.isEmpty()) {
                return ResponseEntity.ok(new HashMap<String, Object>() {{
                    put("products", responseList);
                    put("message", "Có sản phẩm sắp hết hạn trong vòng 6 tháng. Xem chi tiết tại: /chi-tiet-san-pham/expiring-soon");
                }});
            }

            return ResponseEntity.ok(responseList);
        }
        @GetMapping("/expiring-soon")
        public ResponseEntity<?> getExpiringSoon() {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime sixMonthsFromNow = now.plus(6, ChronoUnit.MONTHS);

            // Tìm tất cả các chi tiết sản phẩm và lọc những sản phẩm sắp hết hạn
            List<ChiTietSanPham> chiTietSanPhams = chiTietSanPhamRepository.findAll();
            List<Map<String, Object>> expiringSoonList = chiTietSanPhams.stream()
                    .filter(ctsp -> ctsp.getHsd() != null && ctsp.getHsd().isBefore(sixMonthsFromNow))
                    .map(ctsp -> {
                        Map<String, Object> productDetails = new HashMap<>();
                        ChiTietSanPhamResponse response = ctsp.toChiTietSanPhamResponse();
                        productDetails.put("product", response);
                        productDetails.put("detailsLink", "/chi-tiet-san-pham/detail"); // Chỉ sử dụng endpoint, không có tham số id trong URL
                        productDetails.put("id", ctsp.getId()); // Truyền id của sản phẩm trong body
                        return productDetails;
                    })
                    .collect(Collectors.toList());

            if (expiringSoonList.isEmpty()) {
                return ResponseEntity.ok("Không có sản phẩm nào sắp hết hạn trong vòng 6 tháng.");
            }

            return ResponseEntity.ok(expiringSoonList);
        }

        @GetMapping("/detail")
        public ResponseEntity<?> detail(@RequestParam String id) {
            if (id == null || id.isEmpty()) {
                return ResponseEntity.badRequest().body("ID không được để trống.");
            }
            if (chiTietSanPhamRepository.findById(id).isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy CTSP có id: " + id);
            }
            return ResponseEntity.ok(chiTietSanPhamRepository.findById(id)
                    .stream().map(ChiTietSanPham::toChiTietSanPhamResponse).findFirst().orElse(null));
        }

        @GetMapping("/detailByMa")
        public ResponseEntity<?> detailByMa(@RequestBody Map<String, String> request) {
            String ma = request.get("ma");
            if (ma == null || ma.trim().isEmpty()) {
                return ResponseEntity.badRequest().body("Mã không được để trống.");
            }
            if (!Pattern.matches("^CTSP[A-Z0-9]{6}$", ma.trim())) {
                return ResponseEntity.badRequest().body("Mã phải có định dạng CTSPXXXXXX (X là chữ cái hoặc số)!");
            }
            ChiTietSanPham chiTietSanPham = chiTietSanPhamRepository.getByMa(ma);
            if (chiTietSanPham == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy CTSP có mã: " + ma);
            }

            ChiTietSanPhamResponse response = chiTietSanPham.toChiTietSanPhamResponse();
            return ResponseEntity.ok(response);
        }
        @PostMapping("/add")
        public ResponseEntity<?> add(@Valid @ModelAttribute ChiTietSanPhamRequest chiTietSanPhamRequest) {
            // Chuẩn hóa số ngày sử dụng
            chiTietSanPhamRequest.setSoNgaySuDung(chiTietSanPhamRequest.getSoNgaySuDung().trim());

            String generatedMa;
            do {
                String randomString = UUID.randomUUID().toString().replace("-", "").substring(0, 6).toUpperCase();
                generatedMa = "CTSP" + randomString;
            } while (chiTietSanPhamRepository.getByMa(generatedMa) != null);

            // Kiểm tra sản phẩm đã tồn tại
            ChiTietSanPham existingChiTietSanPham = chiTietSanPhamRepository.trungCTSP(
                    chiTietSanPhamRequest.getIdSP(),
                    chiTietSanPhamRequest.getSoNgaySuDung(),
                    chiTietSanPhamRequest.getNgaySanXuat(),
                    chiTietSanPhamRequest.getHsd(),
                    chiTietSanPhamRequest.getGia());

            // Cập nhật số lượng nếu sản phẩm đã tồn tại
            if (existingChiTietSanPham != null) {
                existingChiTietSanPham.setSoLuong(existingChiTietSanPham.getSoLuong() + chiTietSanPhamRequest.getSoLuong());
                chiTietSanPhamRepository.save(existingChiTietSanPham);
                return ResponseEntity.ok("Sản phẩm đã tồn tại, số lượng đã được cập nhật!");
            }

            // Tạo mới chi tiết sản phẩm
            ChiTietSanPham chiTietSanPham = new ChiTietSanPham();
            BeanUtils.copyProperties(chiTietSanPhamRequest, chiTietSanPham);
            chiTietSanPham.setMa(generatedMa);
            chiTietSanPham.setNgayTao(LocalDateTime.now());
            chiTietSanPham.setNgaySua(null);

            // Kiểm tra và thiết lập sản phẩm
            if (chiTietSanPhamRequest.getIdSP() != null) {
                SanPham sanPham = sanPhamRepository.findById(chiTietSanPhamRequest.getIdSP()).orElse(null);
                if (sanPham == null) {
                    return ResponseEntity.badRequest().body("Không tìm thấy sản phẩm với id: " + chiTietSanPhamRequest.getIdSP());
                }
                chiTietSanPham.setSanPham(sanPham);
            }

            // Lưu chi tiết sản phẩm vào repository
            chiTietSanPhamRepository.save(chiTietSanPham);
            if (chiTietSanPhamRequest.getLinkAnhList() != null && !chiTietSanPhamRequest.getLinkAnhList().isEmpty()) {
                for (String link : chiTietSanPhamRequest.getLinkAnhList()) {
                    AnhCTSP anhCTSP = new AnhCTSP();
                    anhCTSP.setLink(link);
                    anhCTSP.setTrangThai(1);
                    anhCTSP.setNgayTao(LocalDateTime.now());
                    anhCTSP.setTen("Ảnh của"+chiTietSanPham.getMa());
                    anhCTSP.setChiTietSanPham(chiTietSanPham); // Thiết lập liên kết với chi tiết sản phẩm
                    anhCTSPRepository.save(anhCTSP); // Lưu ảnh vào repository
                }
            }

            return ResponseEntity.ok("Thêm mới chi tiết sản phẩm và hình ảnh thành công!");
        }



        @PutMapping("/update")
        public ResponseEntity<?> update(@Valid @ModelAttribute ChiTietSanPhamRequest chiTietSanPhamRequest) {
            String id = chiTietSanPhamRequest.getId();
            if (id == null || id.isEmpty()) {
                return ResponseEntity.badRequest().body("ID không được để trống.");
            }
            ChiTietSanPham existingChiTietSanPham = chiTietSanPhamRepository.findById(id).orElse(null);
            if (existingChiTietSanPham == null) {
                return ResponseEntity.badRequest().body("Không tìm thấy chi tiết sản phẩm có id: " + id);
            }

            // Xóa tất cả ảnh cũ nếu có yêu cầu thay đổi
            if (chiTietSanPhamRequest.getLinkAnhList() != null && !chiTietSanPhamRequest.getLinkAnhList().isEmpty()) {
                existingChiTietSanPham.getAnhCTSP().clear(); // Xóa ảnh cũ
                for (String link : chiTietSanPhamRequest.getLinkAnhList()) {
                    AnhCTSP anhCTSP = new AnhCTSP();
                    anhCTSP.setLink(link);
                    anhCTSP.setTrangThai(1);
                    anhCTSP.setNgayTao(LocalDateTime.now());
                    anhCTSP.setTen("Ảnh của " + existingChiTietSanPham.getMa());
                    anhCTSP.setChiTietSanPham(existingChiTietSanPham);
                    anhCTSPRepository.save(anhCTSP);
                    existingChiTietSanPham.getAnhCTSP().add(anhCTSP);
                }
            }
            if (chiTietSanPhamRequest.getLinkAnhList() == null ) {
                existingChiTietSanPham.getAnhCTSP().clear(); // Xóa ảnh cũ
            }

            BeanUtils.copyProperties(chiTietSanPhamRequest, existingChiTietSanPham, "id", "ma", "ngayTao");
            existingChiTietSanPham.setNgaySua(LocalDateTime.now());
            chiTietSanPhamRepository.save(existingChiTietSanPham);

            return ResponseEntity.ok("Cập nhật chi tiết sản phẩm thành công!");
        }

        @DeleteMapping("/delete")
        public ResponseEntity<?> delete(@RequestBody Map<String, String> request) {
            String id = request.get("id");
            if (id == null || id.isEmpty()) {
                return ResponseEntity.badRequest().body("ID không được để trống.");
            }
            if (id == null || chiTietSanPhamRepository.findById(id).isEmpty()) {
                return ResponseEntity.badRequest().body("Không tìm thấy CTSP có id: " + id);
            }
            anhCTSPRepository.deleteByIdCTSP(id);
            chiTietSanPhamRepository.deleteById(id);
            return ResponseEntity.ok("Xóa thành công");
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
