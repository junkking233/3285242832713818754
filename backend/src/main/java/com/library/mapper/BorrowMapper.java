package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Borrow;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface BorrowMapper extends BaseMapper<Borrow> {

    /**
     * 分页查询借阅记录（关联用户名、图书名）
     */
    @Select("""
            <script>
            SELECT br.*,
                   u.username,
                   u.realname,
                   b.bookname,
                   b.author
            FROM borrow br
            LEFT JOIN user u ON br.userid = u.userid
            LEFT JOIN book_info b ON br.bookid = b.bookid
            <where>
                <if test="username != null and username != ''">
                    AND (u.username LIKE CONCAT('%', #{username}, '%')
                         OR u.realname LIKE CONCAT('%', #{username}, '%'))
                </if>
                <if test="bookname != null and bookname != ''">
                    AND b.bookname LIKE CONCAT('%', #{bookname}, '%')
                </if>
                <if test="userid != null">
                    AND br.userid = #{userid}
                </if>
            </where>
            ORDER BY br.borrowid DESC
            </script>
            """)
    IPage<Borrow> searchByPage(Page<Borrow> page,
                               @Param("username") String username,
                               @Param("bookname") String bookname,
                               @Param("userid") Integer userid);
}
