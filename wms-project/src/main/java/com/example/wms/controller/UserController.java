package com.example.wms.controller;

import com.example.wms.annotation.RequireAdmin;
import com.example.wms.common.JwtResponse;
import com.example.wms.common.Result;
import com.example.wms.dto.ChangePasswordDTO;
import com.example.wms.dto.LoginDTO;
import com.example.wms.dto.RegisterDTO;
import com.example.wms.entity.User;
import com.example.wms.service.UserService;
import com.example.wms.util.JwtUtil;
import javax.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @PostMapping("/login")
    public Result<JwtResponse> login(@Valid @RequestBody LoginDTO loginDTO) {
        User user = userService.login(loginDTO.getUsername(), loginDTO.getPassword());
        if (user != null) {
            if (user.getStatus() != 1) {
                return Result.error("账户未审核或已被禁用，请联系管理员");
            }
            // 生成JWT token
            String token = jwtUtil.generateToken(user.getId(), user.getUsername(), user.getRole());
            JwtResponse jwtResponse = new JwtResponse(token, user.getId(), user.getUsername(), 
                    user.getRealName(), user.getCompany(), user.getRole());
            return Result.success("登录成功", jwtResponse);
        }
        return Result.error("用户名或密码错误");
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
    public Result<String> changePassword(@RequestBody ChangePasswordDTO dto, 
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
