package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookType;

import java.util.List;

public interface BookTypeService {

    IPage<BookType> searchByPage(Page<BookType> page, String typename);

    List<BookType> listAll();

    int addBookType(BookType bookType);

    int updateBookType(BookType bookType);

    int deleteBookType(Integer typeid);
}
