package vn.bookstore.the4bookstore.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import vn.bookstore.the4bookstore.entity.DonHang;
import vn.bookstore.the4bookstore.repository.DonHangRepository;

@Service
public class OrderService {
    @Autowired
    private DonHangRepository donHangRepository;

    public void updateStatus(Long id, String status) {
        DonHang dh = donHangRepository.findById(id.intValue())
            .orElseThrow(() -> new RuntimeException("Không tìm thấy đơn hàng: " + id));
        dh.setTrangThai(status);
        donHangRepository.save(dh);
    }
}
