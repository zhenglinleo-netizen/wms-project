package com.example.wms.entity;

import lombok.Data;
import java.time.LocalDateTime;

/**
 * 材质类型实体类
 */
@Data
public class MaterialType {
    private Long id;
    private String typeName;
    private String description;
    private LocalDateTime createTime;
}