package vn.bookstore.the4bookstore.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.bookstore.the4bookstore.entity.DanhMuc;
import vn.bookstore.the4bookstore.repository.DanhMucRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DanhMucService {

    private final DanhMucRepository danhMucRepository;

    public List<DanhMuc> getAllActive() {
        List<DanhMuc> active = danhMucRepository.findAll().stream()
                .filter(dm -> Boolean.TRUE.equals(dm.getTrangThai()))
                .toList();
        // Fallback: nếu chưa set trangThai thì lấy tất cả
        return active.isEmpty() ? danhMucRepository.findAll() : active;
    }

    public List<DanhMuc> getAll() {
        return danhMucRepository.findAll();
    }

    public Optional<DanhMuc> getById(Integer id) {
        return danhMucRepository.findById(id);
    }

    public DanhMuc save(DanhMuc danhMuc) {
        return danhMucRepository.save(danhMuc);
    }

    public void delete(Integer id) {
        danhMucRepository.deleteById(id);
    }
}
