package com.example.wms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class MaterialCategory {
    private Long id;
    private String categoryName;
    private Long parentId;
    private Integer sortOrder;
    private String description;
    private LocalDateTime createTime;
}

