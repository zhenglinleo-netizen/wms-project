package com.example.wms.service.impl;

import com.example.wms.dto.RegisterDTO;
import com.example.wms.entity.User;
import com.example.wms.mapper.UserMapper;
import com.example.wms.service.UserService;
import com.example.wms.util.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
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
        // 检查用户名是否已存在
        if (userMapper.selectByUsername(registerDTO.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
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
