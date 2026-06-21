package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.library.entity.User;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username} AND password = #{password} LIMIT 1")
    User findByUsernameAndPassword(@Param("username") String username, @Param("password") String password);

    @Select("SELECT * FROM user WHERE username = #{username} LIMIT 1")
    User findByUsername(@Param("username") String username);
}
