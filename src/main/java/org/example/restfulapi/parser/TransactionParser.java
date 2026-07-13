package org.example.restfulapi.parser;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.example.restfulapi.dto.TransactionDTO;
import org.example.restfulapi.model.TransactionStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

/**
 * Lớp phân tích và chuẩn hóa dữ liệu raw JSON thành danh sách TransactionDTO.
 */
@Component
public class TransactionParser {
    private static final Logger log = LoggerFactory.getLogger(TransactionParser.class);
    private final ObjectMapper objectMapper;

    public TransactionParser() {
        this.objectMapper = new ObjectMapper();
        // Đăng ký module hỗ trợ Java 8 Date/Time API cho Jackson (LocalDateTime, vv.)
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Chuyển đổi và chuẩn hóa chuỗi raw JSON sang danh sách TransactionDTO.
     * Nếu bản ghi bất kỳ không hợp lệ, nó sẽ bị bỏ qua và ghi log lỗi mà không gây sập toàn bộ tiến trình.
     *
     * @param rawJson Chuỗi raw JSON đại diện cho mảng các giao dịch.
     * @return Danh sách TransactionDTO hợp lệ.
     */
    public List<TransactionDTO> parseTransactions(String rawJson) {
        List<TransactionDTO> result = new ArrayList<>();
        if (rawJson == null || rawJson.isBlank()) {
            log.warn("Raw JSON input is null or empty");
            return result;
        }

        try {
            JsonNode rootNode = objectMapper.readTree(rawJson);
            if (!rootNode.isArray()) {
                log.error("Input JSON is not a JSON Array. Root content: {}", rawJson);
                return result;
            }

            for (JsonNode node : rootNode) {
                try {
                    TransactionDTO dto = parseAndValidate(node);
                    result.add(dto);
                } catch (Exception e) {
                    log.error("Skip invalid transaction. JSON node: {}, Error: {}", node.toString(), e.getMessage());
                }
            }
        } catch (Exception e) {
            log.error("Failed to parse raw JSON: {}", e.getMessage(), e);
        }

        return result;
    }

    /**
     * Phân tích và kiểm tra tính hợp lệ của từng node giao dịch.
     *
     * @param node JSON Node của một giao dịch.
     * @return Đối tượng TransactionDTO hợp lệ.
     * @throws IllegalArgumentException Nếu bất kỳ ràng buộc dữ liệu nào không được thỏa mãn.
     */
    private TransactionDTO parseAndValidate(JsonNode node) {
        // 1. Kiểm tra ID: Không được null hoặc rỗng
        JsonNode idNode = node.get("id");
        if (idNode == null || idNode.isNull()) {
            throw new IllegalArgumentException("Transaction ID is missing or null");
        }
        String id = idNode.asText().trim();
        if (id.isEmpty()) {
            throw new IllegalArgumentException("Transaction ID cannot be empty");
        }

        // 2. Kiểm tra Amount: Phải là số và > 0
        JsonNode amountNode = node.get("amount");
        if (amountNode == null || amountNode.isNull()) {
            throw new IllegalArgumentException("Amount is missing or null");
        }
        BigDecimal amount;
        try {
            amount = new BigDecimal(amountNode.asText());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Amount is not a valid decimal number: " + amountNode.asText());
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Amount must be greater than 0, got: " + amount);
        }

        // 3. Kiểm tra Status: Chỉ chấp nhận SUCCESS hoặc FAILED
        JsonNode statusNode = node.get("status");
        if (statusNode == null || statusNode.isNull()) {
            throw new IllegalArgumentException("Status is missing or null");
        }
        String statusStr = statusNode.asText().trim().toUpperCase();
        TransactionStatus status;
        try {
            status = TransactionStatus.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid status: '" + statusStr + "'. Allowed values are SUCCESS, FAILED");
        }

        // 4. Kiểm tra transactionDate: Định dạng LocalDateTime
        JsonNode dateNode = node.get("transactionDate");
        if (dateNode == null || dateNode.isNull()) {
            throw new IllegalArgumentException("Transaction date is missing or null");
        }
        String dateStr = dateNode.asText().trim();
        LocalDateTime transactionDate;
        try {
            transactionDate = LocalDateTime.parse(dateStr);
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Invalid transaction date format: '" + dateStr + "'. Expected ISO-8601 (e.g. yyyy-MM-ddTHH:mm:ss)");
        }

        return new TransactionDTO(id, amount, status, transactionDate);
    }
}
