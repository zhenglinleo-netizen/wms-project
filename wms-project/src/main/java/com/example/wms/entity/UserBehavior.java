package com.example.wms.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserBehavior {
    private Long id;
    private Long userId;
    private Long materialId;
    private String behaviorType;
    private BigDecimal weight;
    private String ipAddress;
    private String userAgent;
    private LocalDateTime createTime;
}
