package com.library.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.User;
import com.library.mapper.UserMapper;
import com.library.service.UserService;
import com.library.util.Md5Util;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public User login(String username, String password) {
        String md5Password = Md5Util.encrypt(password);
        return baseMapper.findByUsernameAndPassword(username, md5Password);
    }

    @Override
    public User getById(Integer userid) {
        return baseMapper.selectById(userid);
    }

    @Override
    public User findByUsername(String username) {
        return baseMapper.findByUsername(username);
    }

    @Override
    public IPage<User> searchByPage(Page<User> page, String username, String role) {
        QueryWrapper<User> qw = new QueryWrapper<>();
        if (username != null && !username.isEmpty()) {
            qw.and(w -> w.like("username", username).or().like("realname", username));
        }
        if (role != null && !role.isEmpty()) {
            qw.eq("role", role);
        }
        qw.orderByDesc("userid");
        // 不返回密码
        return baseMapper.selectPage(page, qw);
    }

    @Override
    public int addUser(User user) {
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(Md5Util.encrypt(user.getPassword()));
        }
        if (user.getRole() == null || user.getRole().isEmpty()) {
            user.setRole("READER");
        }
        return baseMapper.insert(user);
    }

    @Override
    public int updateUser(User user) {
        // 如果传了密码，则 MD5 加密
        if (user.getPassword() != null && !user.getPassword().isEmpty()) {
            user.setPassword(Md5Util.encrypt(user.getPassword()));
        } else {
            // 不更新密码字段
            user.setPassword(null);
        }
        return baseMapper.updateById(user);
    }

    @Override
    public int deleteUser(Integer userid) {
        return baseMapper.deleteById(userid);
    }
}
