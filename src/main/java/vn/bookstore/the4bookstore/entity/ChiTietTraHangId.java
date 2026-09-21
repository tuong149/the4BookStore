package vn.bookstore.the4bookstore.entity;
import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietTraHangId implements Serializable {
    private Integer phieuTraHang;
    private Integer sanPham;
}