package vn.bookstore.the4bookstore.entity;
import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietGioHangId implements Serializable {
    private Integer gioHang;
    private Integer sanPham;
}