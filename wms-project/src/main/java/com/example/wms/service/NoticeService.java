package com.example.wms.service;

import com.example.wms.entity.Notice;
import java.util.List;

public interface NoticeService {
    
    int createNotice(Notice notice);
    
    int updateNotice(Notice notice);
    
    Notice getNoticeById(Long id);
    
    List<Notice> getNoticesByUserId(Long userId, Integer limit);
    
    int getUnreadCount(Long userId);
    
    int markAsRead(Long userId, Long noticeId);
    
    int markAllAsRead(Long userId);
    
    int deleteNotice(Long id);
    
    void sendNoticeToQueue(Notice notice);
}