package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.User;

public interface UserService {

    /**
     * 登录验证
     */
    User login(String username, String password);

    /**
     * 根据 ID 查询
     */
    User getById(Integer userid);

    /**
     * 根据用户名查询
     */
    User findByUsername(String username);

    /**
     * 分页查询用户
     */
    IPage<User> searchByPage(Page<User> page, String username, String role);

    /**
     * 添加用户
     */
    int addUser(User user);

    /**
     * 更新用户
     */
    int updateUser(User user);

    /**
     * 删除用户
     */
    int deleteUser(Integer userid);
}
