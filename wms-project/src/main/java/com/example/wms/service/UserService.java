package com.example.wms.service;

import com.example.wms.dto.RegisterDTO;
import com.example.wms.entity.User;
import java.util.List;

public interface UserService {
    User login(String username, String password);
    void register(RegisterDTO registerDTO);
    void changePassword(Long userId, String oldPassword, String newPassword);
    List<User> getAllUsers();
    User getUserById(Long id);
    int saveUser(User user);
    int updateUser(User user);
    int deleteUser(Long id);
}

