package vn.bookstore.the4bookstore.entity;
import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietPhieuNhapId implements Serializable {
    private Integer phieuNhap;
    private Integer sanPham;
}