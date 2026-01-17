package com.example.wms.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 库存实体类
 */
@Data
public class Inventory {
    private Long id;
    private Long warehouseId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal avgPrice;
    private LocalDateTime updateTime;
    
    // 关联信息
    private String warehouseName;
    private String productName;
    private String productCode;
    private String imageUrl;
}




