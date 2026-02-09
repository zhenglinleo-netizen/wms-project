package com.example.wms.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 商品实体类
 */
@Data
public class Product {
    private Long id;
    private Long categoryId;
    private String productCode;
    private String productName;
    private String category;
    private String type;
    private String style;
    private String specification;
    private String unit;
    private BigDecimal price;
    private String description;
    private Integer expectedDeliveryDays;
    private Integer status;
    private String fileHash;
    private String imageUrl;
    private String images;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}




