package com.example.wms.service.impl;

import com.example.wms.dto.RegisterDTO;
import com.example.wms.entity.User;
import com.example.wms.mapper.UserMapper;
import com.example.wms.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserMapper userMapper;

    @Override
    public User login(String username, String password) {
        User user = userMapper.selectByUsername(username);
        if (user != null && password.equals(user.getPassword())) {
            return user;
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
        user.setPassword(registerDTO.getPassword()); // 实际应该加密存储
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
        if (!oldPassword.equals(user.getPassword())) {
            throw new RuntimeException("原密码错误");
        }
        
        // 更新密码
        user.setPassword(newPassword); // 实际应该加密存储
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
}
