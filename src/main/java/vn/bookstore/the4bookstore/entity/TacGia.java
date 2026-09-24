package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

import java.util.List;
import java.util.ArrayList;

@Entity
@Table(name = "TAC_GIA")
@Data @NoArgsConstructor @AllArgsConstructor
public class TacGia {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer maTacGia;
    @Column(nullable = false, length = 150)
    private String tenTacGia;
    @Column(length = 500)
    private String moTa;

    @OneToMany(mappedBy = "tacGia", fetch = FetchType.LAZY)
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private List<SanPhamTacGia> sanPhamTacGias = new ArrayList<>();
}