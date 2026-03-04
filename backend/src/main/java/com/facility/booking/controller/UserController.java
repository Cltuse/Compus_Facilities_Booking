package com.facility.booking.controller;

import com.facility.booking.common.Result;
import com.facility.booking.entity.User;
import com.facility.booking.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 用户管理控制器
 * 提供用户注册、登录、信息管理等功能
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    /**
     * 用户登录
     * @param user 包含用户名和密码的用户信息
     * @return 登录结果和用户基本信息
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody User user) {
        Optional<User> userOpt = userRepository.findByUsernameAndPassword(
                user.getUsername(), user.getPassword());

        if (userOpt.isPresent()) {
            User foundUser = userOpt.get();
            Map<String, Object> data = new HashMap<>();
            data.put("id", foundUser.getId());
            data.put("username", foundUser.getUsername());
            data.put("realName", foundUser.getRealName());
            data.put("role", foundUser.getRole());
            data.put("phone", foundUser.getPhone());
            data.put("email", foundUser.getEmail());
            return Result.success("登录成功", data);
        } else {
            return Result.error("用户名或密码错误");
        }
    }

    /**
     * 用户注册
     * @param user 用户注册信息
     * @return 注册结果和用户基本信息
     */
    @PostMapping("/register")
    public Result<Map<String, Object>> register(@RequestBody User user) {
        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return Result.error("用户名已存在");
        }

        // 检查邮箱是否已存在
        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            Optional<User> existingEmailUser = userRepository.findByEmail(user.getEmail());
            if (existingEmailUser.isPresent()) {
                return Result.error("邮箱已被使用");
            }
        }

        // 设置默认值
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        if (user.getStatus() == null || user.getStatus().isEmpty()) {
            user.setStatus("ACTIVE");
        }

        User savedUser = userRepository.save(user);

        // 返回用户信息（用于自动登录）
        Map<String, Object> data = new HashMap<>();
        data.put("id", savedUser.getId());
        data.put("username", savedUser.getUsername());
        data.put("realName", savedUser.getRealName());
        data.put("role", savedUser.getRole());
        data.put("phone", savedUser.getPhone());
        data.put("email", savedUser.getEmail());

        return Result.success("注册成功", data);
    }

    /**
     * 获取所有用户列表
     * @return 用户列表，密码已隐藏
     */
    @GetMapping("/list")
    public Result<List<User>> list() {
        List<User> users = userRepository.findAll();
        users.forEach(user -> user.setPassword("******")); // 隐藏密码
        return Result.success(users);
    }

    /**
     * 根据ID获取用户详情
     * @param id 用户ID
     * @return 用户详情，密码已隐藏
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            foundUser.setPassword("******");
            return Result.success(foundUser);
        }
        return Result.error("用户不存在");
    }

    /**
     * 创建新用户
     * @param user 用户信息
     * @return 创建的用户信息，密码已隐藏
     */
    @PostMapping
    public Result<User> create(@RequestBody User user) {
        // 检查用户名是否已存在
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return Result.error("用户名已存在");
        }
        User savedUser = userRepository.save(user);
        savedUser.setPassword("******");
        return Result.success("创建成功", savedUser);
    }

    /**
     * 更新用户信息
     * @param id 用户ID
     * @param user 更新的用户信息
     * @return 更新后的用户信息，密码已隐藏
     */
    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }

        User existingUser = userOpt.get();
        existingUser.setRealName(user.getRealName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setStatus(user.getStatus());

        // 只有密码不为null且不为空字符串时才更新密码
        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }

        User savedUser = userRepository.save(existingUser);
        savedUser.setPassword("******");
        return Result.success("更新成功", savedUser);
    }

    /**
     * 修改用户密码
     * @param id 用户ID
     * @param passwordData 包含当前密码和新密码的数据
     * @return 密码修改结果
     */
    @PostMapping("/{id}/change-password")
    public Result<String> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        Optional<User> userOpt = userRepository.findById(id);
        if (!userOpt.isPresent()) {
            return Result.error("用户不存在");
        }

        String currentPassword = passwordData.get("currentPassword");
        String newPassword = passwordData.get("newPassword");

        if (currentPassword == null || currentPassword.trim().isEmpty()) {
            return Result.error("当前密码不能为空");
        }

        if (newPassword == null || newPassword.trim().isEmpty()) {
            return Result.error("新密码不能为空");
        }

        if (newPassword.length() < 6 || newPassword.length() > 20) {
            return Result.error("新密码长度必须在6-20个字符之间");
        }

        User existingUser = userOpt.get();

        // 验证当前密码
        if (!currentPassword.equals(existingUser.getPassword())) {
            return Result.error("当前密码错误");
        }

        // 更新密码
        existingUser.setPassword(newPassword);
        userRepository.save(existingUser);

        return Result.success("密码修改成功");
    }

    /**
     * 删除用户
     * @param id 用户ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return Result.error("用户不存在");
        }
        userRepository.deleteById(id);
        return Result.success("删除成功", null);
    }

    /**
     * 搜索用户（按姓名或学号）
     * @param keyword 搜索关键词
     * @return 匹配的用户列表
     */
    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(new ArrayList<>());
        }
        
        String searchTerm = "%" + keyword.trim() + "%";
        List<User> users = userRepository.findByRealNameLikeOrUsernameLike(searchTerm);
        
        // 隐藏密码信息
        users.forEach(user -> user.setPassword("******"));
        
        return Result.success(users);
    }
}