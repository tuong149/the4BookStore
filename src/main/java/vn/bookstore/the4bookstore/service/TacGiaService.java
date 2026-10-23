package vn.bookstore.the4bookstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.bookstore.the4bookstore.entity.SanPham;
import vn.bookstore.the4bookstore.entity.TacGia;
import vn.bookstore.the4bookstore.repository.SanPhamRepository;
import vn.bookstore.the4bookstore.repository.SanPhamTacGiaRepository;
import vn.bookstore.the4bookstore.repository.TacGiaRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class TacGiaService {

    private final TacGiaRepository tacGiaRepository;
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamTacGiaRepository sanPhamTacGiaRepository;

    @org.springframework.cache.annotation.Cacheable("authors")
    public List<TacGia> getAll() {
        return tacGiaRepository.findAll();
    }

    public Optional<TacGia> getById(Integer id) {
        return tacGiaRepository.findById(id);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "authors", allEntries = true)
    public TacGia save(TacGia tacGia) {
        return tacGiaRepository.save(tacGia);
    }

    @Transactional
    @org.springframework.cache.annotation.CacheEvict(value = "authors", allEntries = true)
    public void delete(Integer id) {
        long bookCount = sanPhamTacGiaRepository.countByTacGia_MaTacGia(id);
        if (bookCount > 0) {
            throw new IllegalStateException("Không thể xóa tác giả này vì đang có " + bookCount + " đầu sách liên kết!");
        }
        tacGiaRepository.deleteById(id);
    }

    public List<SanPham> getBooksByAuthor(Integer authorId) {
        return sanPhamRepository.findBooksByTacGiaId(authorId);
    }

    public long countBooksByAuthor(Integer authorId) {
        return sanPhamTacGiaRepository.countByTacGia_MaTacGia(authorId);
    }

    public List<TacGia> search(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return tacGiaRepository.findAll();
        }
        return tacGiaRepository.findByTenTacGiaContainingIgnoreCase(keyword.trim());
    }

    public List<TacGia> getAuthorsByDanhMuc(Integer maDanhMuc) {
        if (maDanhMuc == null || maDanhMuc <= 0) {
            return getAuthorsByLoaiSP("Sach");
        }
        return tacGiaRepository.findByDanhMucId(maDanhMuc);
    }

    public List<TacGia> getAuthorsByLoaiSP(String loaiSP) {
        if (loaiSP == null || loaiSP.isBlank()) {
            return tacGiaRepository.findAll();
        }
        return tacGiaRepository.findByLoaiSP(loaiSP);
    }

    public java.util.Map<Integer, String> getAuthorCategoryMap() {
        List<Object[]> rows = tacGiaRepository.findAuthorCategoryMappingsForSach();
        java.util.Map<Integer, java.util.List<String>> map = new java.util.HashMap<>();
        for (Object[] row : rows) {
            Integer authorId = (Integer) row[0];
            Integer catId = (Integer) row[1];
            map.computeIfAbsent(authorId, k -> new java.util.ArrayList<>()).add(String.valueOf(catId));
        }
        java.util.Map<Integer, String> result = new java.util.HashMap<>();
        for (java.util.Map.Entry<Integer, java.util.List<String>> entry : map.entrySet()) {
            result.put(entry.getKey(), String.join(",", entry.getValue()));
        }
        return result;
    }

    public boolean isAuthorInCategory(Integer maTacGia, Integer maDanhMuc) {
        if (maTacGia == null || maDanhMuc == null) return false;
        return tacGiaRepository.findByDanhMucId(maDanhMuc).stream()
                .anyMatch(tg -> tg.getMaTacGia().equals(maTacGia));
    }
}
