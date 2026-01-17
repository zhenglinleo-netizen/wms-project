package com.example.wms.entity;

import lombok.Data;
import java.math.BigDecimal;

/**
 * 供应商-辅料关联实体类
 * 维护供应商和辅料之间的多对多关系
 */
@Data
public class SupplierProduct {
    private Long id;
    private Long supplierId;
    private Long productId;
    private BigDecimal price;
    private Integer status;
}