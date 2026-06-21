package com.library.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookInfo;
import com.library.mapper.BookInfoMapper;
import com.library.service.BookInfoService;
import org.springframework.stereotype.Service;

@Service
public class BookInfoServiceImpl extends ServiceImpl<BookInfoMapper, BookInfo> implements BookInfoService {

    @Override
    public IPage<BookInfo> searchByPage(Page<BookInfo> page, String bookname, String author, Integer typeid) {
        return baseMapper.searchByPage(page, bookname, author, typeid);
    }

    @Override
    public BookInfo getById(Integer bookid) {
        return baseMapper.selectById(bookid);
    }

    @Override
    public int addBookInfo(BookInfo bookInfo) {
        if (bookInfo.getIsborrowed() == null) {
            bookInfo.setIsborrowed(0);
        }
        return baseMapper.insert(bookInfo);
    }

    @Override
    public int updateBookInfo(BookInfo bookInfo) {
        return baseMapper.updateById(bookInfo);
    }

    @Override
    public int deleteBookInfo(Integer bookid) {
        return baseMapper.deleteById(bookid);
    }
}
