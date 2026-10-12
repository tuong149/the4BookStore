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
public class VendorImportRequest {
    private String nhaCungCapTen;
    private String ghiChu;
    private List<Item> items;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class Item {
        private Integer maSP;
        private Integer soLuong;
        private Integer donGiaNhap;
    }
}
