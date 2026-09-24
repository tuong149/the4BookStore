package vn.bookstore.the4bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CreatePhieuKeRequest {
    private Long khoId;
    private Long nhanVienId;
    private List<PhieuKeCTDTO> chiTiets;
    private String ghiChu;
}
