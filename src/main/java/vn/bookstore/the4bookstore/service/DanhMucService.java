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
import vn.bookstore.the4bookstore.repository.DanhMucRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DanhMucService {

    private final DanhMucRepository danhMucRepository;

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
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview"}, allEntries = true)
    public DanhMuc save(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview"}, allEntries = true)
    public void delete(Integer id) {
        danhMucRepository.deleteById(id);
    }

    @Transactional
    @CacheEvict(value = {"allActiveCategories", "categories", "categoriesByLoaiSP", "home_preview"}, allEntries = true)
    public Optional<DanhMuc> toggleStatus(Integer id) {
        return danhMucRepository.findById(id).map(dm -> {
            boolean current = Boolean.TRUE.equals(dm.getTrangThai());
            dm.setTrangThai(!current);
            return danhMucRepository.save(dm);
        });
    }
}
