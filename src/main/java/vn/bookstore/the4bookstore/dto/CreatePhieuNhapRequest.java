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
public class CreatePhieuNhapRequest {
    private Long khoId;
    private Long nhaCungCapId;
    private Long nhanVienId;
    private List<PhieuNhapCTDTO> chiTiets;
}
