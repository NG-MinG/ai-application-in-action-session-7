package org.example.restfulapi.dto;

import org.example.restfulapi.model.TransactionStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Record đại diện cho dữ liệu chuyển giao của Giao dịch sau khi đã được chuẩn hóa.
 */
public record TransactionDTO(
    String id,
    BigDecimal amount,
    TransactionStatus status,
    LocalDateTime localDateTime
) {}
