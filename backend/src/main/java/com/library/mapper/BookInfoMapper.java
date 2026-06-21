package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookInfo;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface BookInfoMapper extends BaseMapper<BookInfo> {

    /**
     * 分页查询图书（关联图书类型名）
     */
    @Select("""
            <script>
            SELECT b.*, t.typename
            FROM book_info b
            LEFT JOIN book_type t ON b.typeid = t.typeid
            <where>
                <if test="bookname != null and bookname != ''">
                    AND b.bookname LIKE CONCAT('%', #{bookname}, '%')
                </if>
                <if test="author != null and author != ''">
                    AND b.author LIKE CONCAT('%', #{author}, '%')
                </if>
                <if test="typeid != null">
                    AND b.typeid = #{typeid}
                </if>
            </where>
            ORDER BY b.bookid DESC
            </script>
            """)
    IPage<BookInfo> searchByPage(Page<BookInfo> page,
                                 @Param("bookname") String bookname,
                                 @Param("author") String author,
                                 @Param("typeid") Integer typeid);
}
