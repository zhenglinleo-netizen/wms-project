package com.example.wms.service.impl;

import com.example.wms.dto.RegisterDTO;
import com.example.wms.entity.User;
import com.example.wms.mapper.UserMapper;
import com.example.wms.service.UserService;
import com.example.wms.service.NoticeService;
import com.example.wms.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.ModelAndView;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    @Lazy
    private NoticeService noticeService;

    @Override
    public User login(String account, String password, HttpServletRequest request) {
        User user = null;
        // 判断是手机号还是邮箱
        if (account.matches("^1[3-9]\\d{9}$")) {
            // 手机号登录
            user = userMapper.selectByPhone(account);
        } else if (account.contains("@")) {
            // 邮箱登录
            user = userMapper.selectByEmail(account);
        } else {
            // 用户名登录（保持兼容）
            user = userMapper.selectByUsername(account);
        }

        if (user != null) {
            // 如果密码已加密，使用 BCrypt 验证
            if (PasswordUtil.isEncrypted(user.getPassword())) {
                if (PasswordUtil.verify(password, user.getPassword())) {
                    return user;
                }
            } else {
                // 兼容旧明文密码
                if (password.equals(user.getPassword())) {
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    @Transactional
    public void register(RegisterDTO registerDTO) {
        // 空格字符审查
        if (registerDTO.getUsername() != null) {
            registerDTO.setUsername(registerDTO.getUsername().trim());
            if (registerDTO.getUsername().isEmpty()) {
                throw new RuntimeException("用户名不能为空");
            }
        }
        
        if (registerDTO.getRealName() != null) {
            registerDTO.setRealName(registerDTO.getRealName().trim());
            if (registerDTO.getRealName().isEmpty()) {
                throw new RuntimeException("真实姓名不能为空");
            }
        }
        
        if (registerDTO.getCompany() != null) {
            registerDTO.setCompany(registerDTO.getCompany().trim());
            if (registerDTO.getCompany().isEmpty()) {
                throw new RuntimeException("公司不能为空");
            }
        }
        
        if (registerDTO.getRole() != null) {
            registerDTO.setRole(registerDTO.getRole().trim());
            if (registerDTO.getRole().isEmpty()) {
                throw new RuntimeException("角色不能为空");
            }
        }
        
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        
        // 检查手机号是否已存在
        if (registerDTO.getPhone() != null && !registerDTO.getPhone().isEmpty()) {
            if (userMapper.selectByPhone(registerDTO.getPhone()) != null) {
                throw new RuntimeException("手机号已被注册");
            }
        }
        
        // 创建用户对象
        User user = new User();
        user.setUsername(registerDTO.getUsername());
        user.setPassword(PasswordUtil.encrypt(registerDTO.getPassword())); // BCrypt 加密存储
        user.setRealName(registerDTO.getRealName());
        user.setPhone(registerDTO.getPhone());
        user.setEmail(registerDTO.getEmail());
        user.setCompany(registerDTO.getCompany());
        user.setRole(registerDTO.getRole());
        user.setStatus(0); // 待审核状态
        
        userMapper.insert(user);
        
        // 向管理员发送通知
        String title = "新用户注册通知";
        String content = "新用户 " + registerDTO.getUsername() + " (" + registerDTO.getRealName() + ") 已注册，公司：" + registerDTO.getCompany() + "，请及时审核。";
        String type = "system";
        noticeService.sendNoticeToAdmins(title, content, type, user.getId());
    }

    @Override
    @Transactional
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userMapper.selectById(userId);
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        
        // 验证原密码
        boolean passwordValid = false;
        if (PasswordUtil.isEncrypted(user.getPassword())) {
            passwordValid = PasswordUtil.verify(oldPassword, user.getPassword());
        } else {
            // 兼容旧明文密码
            passwordValid = oldPassword.equals(user.getPassword());
        }
        
        if (!passwordValid) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码 - BCrypt 加密存储
        user.setPassword(PasswordUtil.encrypt(newPassword));
        userMapper.update(user);
    }

    @Override
    public List<User> getAllUsers() {
        return userMapper.selectAll();
    }

    @Override
    public User getUserById(Long id) {
        return userMapper.selectById(id);
    }

    @Override
    public int saveUser(User user) {
        if (userMapper.selectByUsername(user.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (user.getStatus() == null) {
            user.setStatus(1);
        }
        // 如果密码未加密，则进行加密存储
        if (user.getPassword() != null && !PasswordUtil.isEncrypted(user.getPassword())) {
            user.setPassword(PasswordUtil.encrypt(user.getPassword()));
        }
        return userMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        return userMapper.update(user);
    }

    @Override
    public int deleteUser(Long id) {
        User target = userMapper.selectById(id);
        if (target == null) {
            throw new RuntimeException("用户不存在");
        }
        if ("admin".equals(target.getRole())) {
            int adminCount = userMapper.countAdmins();
            if (adminCount <= 1) {
                throw new RuntimeException("系统至少保留一个管理员账户，无法删除");
            }
        }
        return userMapper.deleteById(id);
    }

    @Override
    public List<User> getAdmins() {
        return userMapper.selectAdmins();
    }
}
