package vn.bookstore.the4bookstore.entity;
import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietDonHangId implements Serializable {
    private Integer donHang;
    private Integer sanPham;
}