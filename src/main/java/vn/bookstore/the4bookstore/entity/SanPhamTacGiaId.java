package vn.bookstore.the4bookstore.entity;
import lombok.*;
import java.io.Serializable;

@Data @NoArgsConstructor @AllArgsConstructor
public class SanPhamTacGiaId implements Serializable {
    private Integer sanPham;
    private Integer tacGia;
}