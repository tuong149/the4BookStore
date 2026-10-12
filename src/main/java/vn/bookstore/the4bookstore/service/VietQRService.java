package vn.bookstore.the4bookstore.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class VietQRService {

    @Value("${vietqr.bank-id:MB}")
    private String bankId;

    @Value("${vietqr.account-no:0987654321}")
    private String accountNo;

    @Value("${vietqr.account-name:THE4BOOKSTORE}")
    private String accountName;

    @Value("${vietqr.template:compact2}")
    private String template;

    public String generateQrUrl(int orderId, int amount) {
        String addInfo = URLEncoder.encode("DH" + orderId, StandardCharsets.UTF_8);
        String accName = URLEncoder.encode(accountName, StandardCharsets.UTF_8);
        return String.format("https://img.vietqr.io/image/%s-%s-%s.png?amount=%d&addInfo=%s&accountName=%s",
                bankId, accountNo, template, amount, addInfo, accName);
    }

    public String getBankId() {
        return bankId;
    }

    public String getAccountNo() {
        return accountNo;
    }

    public String getAccountName() {
        return accountName;
    }
}
