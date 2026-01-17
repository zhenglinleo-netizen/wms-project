package com.example.wms.entity;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 仓库实体类
 */
@Data
public class Warehouse {
    private Long id;
    private String warehouseCode;
    private String warehouseName;
    private String address;
    private BigDecimal capacity;
    private String manager;
    private String phone;
    private Integer status;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}




