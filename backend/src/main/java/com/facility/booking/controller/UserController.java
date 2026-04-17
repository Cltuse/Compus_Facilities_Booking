package com.facility.booking.controller;

import com.facility.booking.common.Result;
import com.facility.booking.dto.auth.AuthResponse;
import com.facility.booking.entity.User;
import com.facility.booking.repository.UserRepository;
import com.facility.booking.security.CustomUserPrincipal;
import com.facility.booking.security.JwtTokenProvider;
import com.facility.booking.service.ViolationRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ViolationRecordService violationRecordService;

    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    @PostMapping("/login")
    public Result<AuthResponse> login(@RequestBody User user) {
        Optional<User> userOpt = userRepository.findByUsernameAndPassword(user.getUsername(), user.getPassword());
        if (userOpt.isEmpty()) {
            return Result.error("用户名或密码错误");
        }
        return Result.success("登录成功", buildAuthResponse(userOpt.get()));
    }

    @PostMapping("/register")
    public Result<AuthResponse> register(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return Result.error("用户名已存在");
        }

        if (user.getEmail() != null && !user.getEmail().isEmpty()) {
            Optional<User> existingEmailUser = userRepository.findByEmail(user.getEmail());
            if (existingEmailUser.isPresent()) {
                return Result.error("邮箱已被使用");
            }
        }

        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("USER");
        }
        if (user.getStatus() == null || user.getStatus().isEmpty()) {
            user.setStatus("ACTIVE");
        }

        User savedUser = userRepository.save(user);
        return Result.success("注册成功", buildAuthResponse(savedUser));
    }

    @GetMapping("/list")
    public Result<Page<User>> list(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            PageRequest pageRequest = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "id"));
            Page<User> users = userRepository.findAll(pageRequest);
            users.forEach(user -> {
                violationRecordService.recalculateUserCreditScoreAndViolationCount(user.getId());
                user.setCreditScore(violationRecordService.getUserCurrentCreditScore(user.getId()));
                user.setViolationCount(violationRecordService.getUserViolationCount(user.getId()));
                user.setPassword("******");
            });
            return Result.success(users);
        } catch (Exception e) {
            return Result.error("获取用户列表失败: " + e.getMessage());
        }
    }

    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            User foundUser = user.get();
            violationRecordService.recalculateUserCreditScoreAndViolationCount(foundUser.getId());
            foundUser.setCreditScore(violationRecordService.getUserCurrentCreditScore(foundUser.getId()));
            foundUser.setViolationCount(violationRecordService.getUserViolationCount(foundUser.getId()));
            foundUser.setPassword("******");
            return Result.success(foundUser);
        }
        return Result.error("用户不存在");
    }

    @PostMapping
    public Result<User> create(@RequestBody User user) {
        Optional<User> existingUser = userRepository.findByUsername(user.getUsername());
        if (existingUser.isPresent()) {
            return Result.error("用户名已存在");
        }
        User savedUser = userRepository.save(user);
        savedUser.setPassword("******");
        return Result.success("创建成功", savedUser);
    }

    @PutMapping("/{id}")
    public Result<User> update(@PathVariable Long id, @RequestBody User user) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return Result.error("用户不存在");
        }

        User existingUser = userOpt.get();
        existingUser.setRealName(user.getRealName());
        existingUser.setPhone(user.getPhone());
        existingUser.setEmail(user.getEmail());
        existingUser.setRole(user.getRole());
        existingUser.setStatus(user.getStatus());
        existingUser.setAvatar(user.getAvatar());

        if (user.getPassword() != null && !user.getPassword().trim().isEmpty()) {
            existingUser.setPassword(user.getPassword());
        }

        User savedUser = userRepository.save(existingUser);
        savedUser.setPassword("******");
        return Result.success("更新成功", savedUser);
    }

    @PostMapping("/{id}/change-password")
    public Result<String> changePassword(@PathVariable Long id, @RequestBody Map<String, String> passwordData) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
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
        if (!currentPassword.equals(existingUser.getPassword())) {
            return Result.error("当前密码错误");
        }

        existingUser.setPassword(newPassword);
        userRepository.save(existingUser);
        return Result.success("密码修改成功", null);
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        if (!userRepository.existsById(id)) {
            return Result.error("用户不存在");
        }
        userRepository.deleteById(id);
        return Result.success("删除成功", null);
    }

    @GetMapping("/search")
    public Result<List<User>> searchUsers(@RequestParam String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return Result.success(new ArrayList<>());
        }

        String searchTerm = "%" + keyword.trim() + "%";
        List<User> users = userRepository.findByRealNameLikeOrUsernameLike(searchTerm);
        users.forEach(user -> {
            violationRecordService.recalculateUserCreditScoreAndViolationCount(user.getId());
            user.setCreditScore(violationRecordService.getUserCurrentCreditScore(user.getId()));
            user.setViolationCount(violationRecordService.getUserViolationCount(user.getId()));
            user.setPassword("******");
        });
        return Result.success(users);
    }

    private AuthResponse buildAuthResponse(User user) {
        CustomUserPrincipal principal = new CustomUserPrincipal(
                user.getId(),
                user.getUsername(),
                user.getRealName(),
                user.getRole(),
                user.getStatus()
        );
        return new AuthResponse(
                jwtTokenProvider.generateToken(principal),
                jwtTokenProvider.getExpirationMillis(),
                new AuthResponse.UserProfile(
                        user.getId(),
                        user.getUsername(),
                        user.getRealName(),
                        user.getRole(),
                        user.getPhone(),
                        user.getEmail(),
                        user.getAvatar(),
                        user.getStatus()
                )
        );
    }
}
