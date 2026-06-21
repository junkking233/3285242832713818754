package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.User;
import com.library.service.UserService;
import com.library.util.JwtUtil;
import com.library.util.Md5Util;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * 注册
     */
    @PostMapping("/register")
    public Result<Void> register(@RequestBody User user) {
        if (user.getUsername() == null || user.getUsername().isEmpty()) {
            return Result.error(400, "用户名不能为空");
        }
        if (user.getPassword() == null || user.getPassword().isEmpty()) {
            return Result.error(400, "密码不能为空");
        }
        User existing = userService.findByUsername(user.getUsername());
        if (existing != null) {
            return Result.error(400, "用户名已存在");
        }
        userService.addUser(user);
        return Result.success("注册成功", null);
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
     * 修改密码
     */
    @PutMapping("/user/changePassword")
    public Result<Void> changePassword(@RequestBody Map<String, String> params, HttpServletRequest request) {
        Integer userId = (Integer) request.getAttribute("userId");
        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        User user = userService.getById(userId);
        if (user == null) {
            return Result.error(400, "用户不存在");
        }
        if (!user.getPassword().equals(Md5Util.encrypt(oldPassword))) {
            return Result.error(400, "原密码错误");
        }
        User update = new User();
        update.setUserid(userId);
        update.setPassword(newPassword);
        userService.updateUser(update);
        return Result.success("密码修改成功", null);
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

    /**
     * 批量删除用户
     */
    @DeleteMapping("/user/batchDelete")
    public Result<Void> batchDeleteUsers(@RequestBody Map<String, Object> params) {
        Object idsObj = params.get("ids");
        if (idsObj == null) {
            return Result.error(400, "请选择要删除的记录");
        }
        @SuppressWarnings("unchecked")
        List<Object> idList = (List<Object>) idsObj;
        List<Integer> ids = idList.stream()
                .map(o -> Integer.valueOf(o.toString()))
                .collect(Collectors.toList());
        for (Integer id : ids) {
            userService.deleteUser(id);
        }
        return Result.success();
    }
}
