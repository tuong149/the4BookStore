package vn.bookstore.the4bookstore.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PhieuNhapCTDTO {
    private Long sachId;
    private Integer soLuong;
    private BigDecimal giaNhap;
}
