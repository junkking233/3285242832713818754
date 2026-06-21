package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import com.library.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private JwtUtil jwtUtil;

    /**
     * 登录
     * 对应文档 4.1 登录模块
     */
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@RequestBody Map<String, String> params) {
        String username = params.get("username");
        String password = params.get("password");
        if (username == null || username.isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        if (password == null || password.isEmpty()) {
            return Result.error(400, "密码不能为空");
        }

        User userObj = userService.login(username, password);
        if (userObj == null) {
            return Result.error(420, "账号或密码错误");
        }

        String token = jwtUtil.generateToken(userObj.getUserid(), userObj.getUsername(), userObj.getRole());

        Map<String, Object> data = new HashMap<>();
        data.put("token", token);
        data.put("user", userObj);
        return Result.success("登录成功", data);
    }

    /**
     * 获取当前登录用户信息
     */
    @GetMapping("/user/info")
    public Result<User> info(HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        User user = userService.getById(userId);
        return Result.success(user);
    }

    /**
     * 分页查询用户
     */
    @GetMapping("/user/queryUsersByPage")
    public Result<Map<String, Object>> queryUsersByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String role) {

        Page<User> p = new Page<>(page, pageSize);
        IPage<User> result = userService.searchByPage(p, username, role);
        return Result.list(result.getTotal(), result.getRecords());
    }

    /**
     * 添加用户
     */
    @PostMapping("/user/addUser")
    public Result<Void> addUser(@RequestBody User user) {
        // 检查用户名是否已存在
        User existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            return Result.error(400, "用户名已存在");
        }
        userService.addUser(user);
        return Result.success();
    }

    /**
     * 更新用户
     * 对应文档 4.5.1 编辑用户
     */
    @PutMapping("/user/updateUser")
    public Result<Void> updateUser(@RequestBody User user) {
        if (user.getUserid() == null) {
            return Result.error(400, "用户ID不能为空");
        }
        userService.updateUser(user);
        return Result.success();
    }

    /**
     * 删除用户
     * 对应文档 4.5.2 删除用户
     */
    @DeleteMapping("/user/deleteUser")
    public Result<Void> deleteUser(@RequestBody Map<String, Object> params) {
        Object idObj = params.get("userid");
        if (idObj == null) {
            idObj = params.get("userId");
        }
        if (idObj == null) {
            return Result.error(400, "用户ID不能为空");
        }
        Integer userid = Integer.valueOf(idObj.toString());
        userService.deleteUser(userid);
        return Result.success();
    }
}
