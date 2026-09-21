package vn.bookstore.the4bookstore.entity;
import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class ChiTietKiemKeId implements Serializable {
    private Integer phieuKiemKe;
    private Integer sanPham;
}