package com.example.wms.service;

import com.example.wms.dto.RegisterDTO;
import com.example.wms.entity.User;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface UserService {
    User login(String account, String password, HttpServletRequest request);
    void register(RegisterDTO registerDTO);
    void changePassword(Long userId, String oldPassword, String newPassword);
    List<User> getAllUsers();
    User getUserById(Long id);
    int saveUser(User user);
    int updateUser(User user);
    int deleteUser(Long id);
    List<User> getAdmins();
}

