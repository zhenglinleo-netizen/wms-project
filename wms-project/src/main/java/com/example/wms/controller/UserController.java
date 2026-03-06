package com.example.wms.controller;

import com.example.wms.annotation.RequireAdmin;
import com.example.wms.common.JwtResponse;
import com.example.wms.common.Result;
import com.example.wms.dto.ChangePasswordDTO;
import com.example.wms.dto.LoginDTO;
import com.example.wms.dto.RegisterDTO;
import com.example.wms.entity.Notice;
import com.example.wms.entity.User;
import com.example.wms.service.NoticeService;
import com.example.wms.service.UserService;
import com.example.wms.util.JwtUtil;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private NoticeService noticeService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<JwtResponse> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletRequest request) {
        System.out.println("接收到登录请求: " + loginDTO);
        
        String account = loginDTO.getAccount();
        String password = loginDTO.getPassword();
        
        System.out.println("账号: " + account);
        System.out.println("密码: " + password);
        
        System.out.println("参数验证通过，开始登录: " + account);
        User user = userService.login(account, password, request);
        if (user != null) {
            System.out.println("登录成功: " + user.getUsername());
            if (user.getStatus() != 1) {
                System.out.println("账户未审核或已被禁用: " + user.getStatus());
                return Result.error("账户未审核或已被禁用，请联系管理员");
            }
            
            // 检查是否首次登录
            if (user.getIsFirstLogin() != null && user.getIsFirstLogin() == 1) {
                System.out.println("首次登录，发送欢迎消息");
                // 构建欢迎消息
                String title = "欢迎使用WMS系统";
                String content = "亲爱的" + user.getRealName() + "，欢迎您首次登录WMS系统！\n\n" +
                        "系统使用指南：\n" +
                        "1. 左侧菜单栏包含系统所有功能模块\n" +
                        "2. 辅料管理模块用于管理辅料信息\n" +
                        "3. 项目方案模块用于创建和管理项目\n" +
                        "4. 消息中心用于查看系统通知\n\n" +
                        "如有任何问题，请联系系统管理员。";
                String type = "system";
                
                // 发送欢迎消息
                Notice notice = new Notice();
                notice.setTitle(title);
                notice.setContent(content);
                notice.setType(type);
                notice.setUserId(user.getId());
                notice.setRelatedId(user.getId());
                notice.setIsRead(0);
                notice.setCreateTime(LocalDateTime.now());
                
                noticeService.createNotice(notice);
                
                // 更新首次登录状态
                user.setIsFirstLogin(0);
                userService.updateUser(user);
                System.out.println("已更新首次登录状态为非首次登录");
            }
            
            // 生成JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            JwtResponse jwtResponse = new JwtResponse(token, user.getId(), user.getUsername(), 
                    user.getRealName(), user.getCompany(), user.getRole());
            return Result.success("登录成功", jwtResponse);
        }
        System.out.println("登录失败: 账号或密码错误");
        return Result.error("账号或密码错误");
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public Result<String> register(@Valid @RequestBody RegisterDTO registerDTO) {
        try {
            userService.register(registerDTO);
            return Result.success("注册成功，等待管理员审核", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 修改密码
     */
    @PostMapping("/change-password")
    public Result<String> changePassword(@Valid @RequestBody ChangePasswordDTO dto, 
                                         @RequestAttribute Long userId) {
        try {
            userService.changePassword(userId, dto.getOldPassword(), dto.getNewPassword());
            return Result.success("密码修改成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }

    /**
     * 获取当前用户信息
     */
    @GetMapping("/current")
    public Result<User> getCurrentUser(@RequestAttribute Long userId) {
        User user = userService.getUserById(userId);
        // 清除密码信息
        user.setPassword(null);
        return Result.success(user);
    }

    @GetMapping("/list")
    @RequireAdmin
    public Result<List<User>> getAllUsers() {
        List<User> users = userService.getAllUsers();
        return Result.success(users);
    }

    @GetMapping("/{id}")
    @RequireAdmin
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    @PostMapping
    @RequireAdmin
    public Result<String> saveUser(@RequestBody User user) {
        userService.saveUser(user);
        return Result.success("添加成功", null);
    }

    @PutMapping
    @RequireAdmin
    public Result<String> updateUser(@RequestBody User user) {
        userService.updateUser(user);
        return Result.success("更新成功", null);
    }

    @DeleteMapping("/{id}")
    @RequireAdmin
    public Result<String> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return Result.success("删除成功", null);
        } catch (Exception e) {
            return Result.error(e.getMessage());
        }
    }
}
