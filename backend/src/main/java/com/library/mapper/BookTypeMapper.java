package com.library.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookType;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

public interface BookTypeMapper extends BaseMapper<BookType> {

    /**
     * 分页查询图书类型
     */
    @Select("""
            <script>
            SELECT * FROM book_type
            <where>
                <if test="typename != null and typename != ''">
                    AND typename LIKE CONCAT('%', #{typename}, '%')
                </if>
            </where>
            ORDER BY typeid DESC
            </script>
            """)
    IPage<BookType> searchByPage(Page<BookType> page, @Param("typename") String typename);
}
