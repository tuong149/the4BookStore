package vn.bookstore.the4bookstore.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "chi_tiet_kiem_ke")
@IdClass(ChiTietKiemKeId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ChiTietKiemKe {
    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ma_phieu_kiem_ke", nullable = false)
    private PhieuKiemKe phieuKiemKe;

    @Id
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "masp", nullable = false)
    private SanPham sanPham;

    @Column(name = "so_luong_he_thong", nullable = false)
    private Integer soLuongHeThong;

    @Column(name = "so_luong_thuc_te", nullable = false)
    private Integer soLuongThucTe;

    @Column(name = "chenh_lech", nullable = false)
    private Integer chenhLech = 0;

    @Column(name = "ly_do", length = 500)
    private String lyDo;
}
