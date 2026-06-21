package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.BookInfo;

public interface BookInfoService {

    IPage<BookInfo> searchByPage(Page<BookInfo> page, String bookname, String author, Integer typeid);

    BookInfo getById(Integer bookid);

    int addBookInfo(BookInfo bookInfo);

    int updateBookInfo(BookInfo bookInfo);

    int deleteBookInfo(Integer bookid);
}
