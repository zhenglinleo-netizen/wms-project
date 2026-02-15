package com.example.wms.entity;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class Notice {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private String type;
    private Integer isRead;
    private Long relatedId;
    private String relatedType;
    private Integer priority;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}