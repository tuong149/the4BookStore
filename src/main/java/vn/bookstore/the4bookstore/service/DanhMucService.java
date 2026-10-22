package vn.bookstore.the4bookstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.entity.DanhMucLon;
import vn.bookstore.the4bookstore.repository.DanhMucLonRepository;
import vn.bookstore.the4bookstore.repository.DanhMucRepository;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DanhMucService {

    private final DanhMucRepository danhMucRepository;
    private final DanhMucLonRepository danhMucLonRepository;
    private final SanPhamRepository sanPhamRepository;

    @Cacheable("allActiveCategories")
    public List<DanhMuc> getAllActive() {
        List<DanhMuc> active = danhMucRepository.findByTrangThaiTrueOrderByMaDanhMucAsc();
        return active.isEmpty() ? danhMucRepository.findAll() : active;
    }

    public Page<DanhMuc> getAllActivePaged(int page, int size) {
        List<DanhMuc> all = getAllActive();
        if (all.isEmpty()) {
            return new PageImpl<>(Collections.emptyList(), PageRequest.of(Math.max(0, page), size), 0);
        }
        int total = all.size();
        int safePage = Math.max(0, page);
        int maxPage = (total - 1) / size;
        if (safePage > maxPage) {
            safePage = maxPage;
        }
        int fromIndex = safePage * size;
        int toIndex = Math.min(fromIndex + size, total);
        List<DanhMuc> subList = all.subList(fromIndex, toIndex);
        return new PageImpl<>(subList, PageRequest.of(safePage, size), total);
    }

    public List<DanhMuc> getAll() {
        return danhMucRepository.findAll();
    }

    public Optional<DanhMuc> getById(Integer id) {
        return danhMucRepository.findById(id);
    }

    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview", "allMajorCategories", "allActiveMajorCategories"}, allEntries = true)
    public DanhMuc save(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview", "allMajorCategories", "allActiveMajorCategories"}, allEntries = true)
    public void delete(Integer id) {
        danhMucRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview", "allMajorCategories", "allActiveMajorCategories"}, allEntries = true)
    public Optional<DanhMuc> toggleStatus(Integer id) {
        return danhMucRepository.findById(id).map(dm -> {
            boolean current = Boolean.TRUE.equals(dm.getTrangThai());
            dm.setTrangThai(!current);
            return danhMucRepository.save(dm);
        });
    }

    // =========================================================================
    // QUẢN LÝ DANH MỤC LỚN (NGÀNH HÀNG / MAJOR CATEGORIES)
    // =========================================================================

    public List<DanhMucLon> getAllMajorCategories() {
        return danhMucLonRepository.findAllByOrderByThuTuAscIdAsc();
    }

    @Cacheable("allActiveMajorCategories")
    public List<DanhMucLon> getActiveMajorCategories() {
        return danhMucLonRepository.findByTrangThaiTrueOrderByThuTuAscIdAsc();
    }

    public Optional<DanhMucLon> getMajorCategoryById(Integer id) {
        return danhMucLonRepository.findById(id);
    }

    public Optional<DanhMucLon> getMajorCategoryByMaLoai(String maLoai) {
        return danhMucLonRepository.findByMaLoai(maLoai);
    }

    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview", "allMajorCategories", "allActiveMajorCategories"}, allEntries = true)
    public DanhMucLon saveMajorCategory(DanhMucLon dml, String oldMaLoai) {
        DanhMucLon saved = danhMucLonRepository.save(dml);

        // Nếu mã loại bị thay đổi, tự động cascade cập nhật cho tất cả DanhMuc và SanPham liên quan
        if (oldMaLoai != null && !oldMaLoai.isBlank() && !oldMaLoai.equalsIgnoreCase(saved.getMaLoai())) {
            danhMucRepository.updateLoaiSP(oldMaLoai, saved.getMaLoai());
            sanPhamRepository.updateLoaiSP(oldMaLoai, saved.getMaLoai());
        }

        return saved;
    }

    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview", "allMajorCategories", "allActiveMajorCategories"}, allEntries = true)
    public void deleteMajorCategory(Integer id) {
        danhMucLonRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview", "allMajorCategories", "allActiveMajorCategories"}, allEntries = true)
    public Optional<DanhMucLon> toggleMajorCategoryStatus(Integer id) {
        return danhMucLonRepository.findById(id).map(dml -> {
            boolean current = Boolean.TRUE.equals(dml.getTrangThai());
            dml.setTrangThai(!current);
            return danhMucLonRepository.save(dml);
        });
    }
}
