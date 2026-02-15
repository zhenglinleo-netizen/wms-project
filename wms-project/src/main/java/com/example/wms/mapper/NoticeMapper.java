package com.example.wms.mapper;

import com.example.wms.entity.Notice;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface NoticeMapper {
    
    int insert(Notice notice);
    
    int updateById(Notice notice);
    
    Notice selectById(Long id);
    
    List<Notice> selectByUserId(@Param("userId") Long userId, @Param("limit") Integer limit);
    
    int countUnreadByUserId(Long userId);
    
    int markAsRead(@Param("userId") Long userId, @Param("noticeId") Long noticeId);
    
    int markAllAsRead(Long userId);
    
    int deleteById(Long id);
}