package vn.bookstore.the4bookstore.entity;
import jakarta.persistence.*;
import lombok.*;

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
}