package com.library.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookType;
import com.library.mapper.BookTypeMapper;
import com.library.service.BookTypeService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BookTypeServiceImpl extends ServiceImpl<BookTypeMapper, BookType> implements BookTypeService {

    @Override
    public IPage<BookType> searchByPage(Page<BookType> page, String typename) {
        return baseMapper.searchByPage(page, typename);
    }

    @Override
    public List<BookType> listAll() {
        return baseMapper.selectList(null);
    }

    @Override
    public int addBookType(BookType bookType) {
        return baseMapper.insert(bookType);
    }

    @Override
    public int updateBookType(BookType bookType) {
        return baseMapper.updateById(bookType);
    }

    @Override
    public int deleteBookType(Integer typeid) {
        return baseMapper.deleteById(typeid);
    }
}
