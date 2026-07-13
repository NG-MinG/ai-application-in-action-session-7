package org.example.restfulapi.controller;

import org.example.restfulapi.dto.TransactionDTO;
import org.example.restfulapi.parser.TransactionParser;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller cung cấp REST API kiểm tra chức năng của TransactionParser.
 */
@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    private final TransactionParser transactionParser;

    public TransactionController(TransactionParser transactionParser) {
        this.transactionParser = transactionParser;
    }

    /**
     * API chuẩn hóa dữ liệu raw JSON thành danh sách TransactionDTO.
     * Đầu vào: Chuỗi raw JSON đại diện cho mảng các giao dịch (post qua body dạng text/plain hoặc application/json).
     * Đầu ra: Danh sách các TransactionDTO hợp lệ.
     */
    @PostMapping("/parse")
    public ResponseEntity<List<TransactionDTO>> parseTransactions(@RequestBody String rawJson) {
        List<TransactionDTO> parsedTransactions = transactionParser.parseTransactions(rawJson);
        return ResponseEntity.ok(parsedTransactions);
    }
}
