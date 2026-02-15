package com.example.wms.controller;

import com.example.wms.common.Result;
import com.example.wms.entity.Notice;
import com.example.wms.service.NoticeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/notice")
public class NoticeController {
    
    @Autowired
    private NoticeService noticeService;
    
    @PostMapping("/send")
    public Result<String> sendNotice(@RequestBody Notice notice) {
        try {
            notice.setIsRead(0);
            noticeService.createNotice(notice);
            noticeService.sendNoticeToQueue(notice);
            return Result.success("通知发送成功", null);
        } catch (Exception e) {
            return Result.error("通知发送失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/list")
    public Result<List<Notice>> getNoticeList(@RequestParam Long userId, @RequestParam(defaultValue = "20") Integer limit) {
        try {
            List<Notice> notices = noticeService.getNoticesByUserId(userId, limit);
            return Result.success(notices);
        } catch (Exception e) {
            return Result.error("获取通知列表失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/unread/count")
    public Result<Integer> getUnreadCount(@RequestParam Long userId) {
        try {
            int count = noticeService.getUnreadCount(userId);
            return Result.success(count);
        } catch (Exception e) {
            return Result.error("获取未读消息数失败: " + e.getMessage());
        }
    }
    
    @GetMapping("/latest")
    public Result<Map<String, Object>> getLatestNotice(@RequestParam Long userId) {
        try {
            List<Notice> notices = noticeService.getNoticesByUserId(userId, 5);
            int unreadCount = noticeService.getUnreadCount(userId);
            
            Map<String, Object> result = new HashMap<>();
            result.put("notices", notices);
            result.put("unreadCount", unreadCount);
            
            return Result.success(result);
        } catch (Exception e) {
            return Result.error("获取最新通知失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/read")
    public Result<String> markAsRead(@RequestParam Long userId, @RequestParam Long noticeId) {
        try {
            int result = noticeService.markAsRead(userId, noticeId);
            if (result > 0) {
                return Result.success("标记已读成功", null);
            } else {
                return Result.error("标记已读失败");
            }
        } catch (Exception e) {
            return Result.error("标记已读失败: " + e.getMessage());
        }
    }
    
    @PostMapping("/read/all")
    public Result<String> markAllAsRead(@RequestParam Long userId) {
        try {
            int result = noticeService.markAllAsRead(userId);
            return Result.success("全部标记已读成功", null);
        } catch (Exception e) {
            return Result.error("全部标记已读失败: " + e.getMessage());
        }
    }
    
    @DeleteMapping("/{id}")
    public Result<String> deleteNotice(@PathVariable Long id) {
        try {
            int result = noticeService.deleteNotice(id);
            if (result > 0) {
                return Result.success("删除成功", null);
            } else {
                return Result.error("删除失败");
            }
        } catch (Exception e) {
            return Result.error("删除失败: " + e.getMessage());
        }
    }
}