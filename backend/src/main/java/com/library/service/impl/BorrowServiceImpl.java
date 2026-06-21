package com.library.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.library.entity.BookInfo;
import com.library.entity.Borrow;
import com.library.mapper.BookInfoMapper;
import com.library.mapper.BorrowMapper;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
public class BorrowServiceImpl extends ServiceImpl<BorrowMapper, Borrow> implements BorrowService {

    @Autowired
    private BookInfoMapper bookInfoMapper;

    @Override
    public IPage<Borrow> searchByPage(Page<Borrow> page, String username, String bookname, Integer userid) {
        return baseMapper.searchByPage(page, username, bookname, userid);
    }

    @Override
    @Transactional
    public int borrowBook(Integer userid, Integer bookid) {
        // 检查图书是否存在、是否已借出
        BookInfo theBook = bookInfoMapper.selectById(bookid);
        if (theBook == null || theBook.getIsborrowed() == 1) {
            return 0;
        }

        // 更新图书状态为已借出
        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookid(bookid);
        bookInfo.setIsborrowed(1);
        bookInfoMapper.updateById(bookInfo);

        // 生成借阅记录
        Borrow borrow = new Borrow();
        borrow.setUserid(userid);
        borrow.setBookid(bookid);
        borrow.setBorrowtime(LocalDateTime.now());
        return baseMapper.insert(borrow);
    }

    @Override
    @Transactional
    public int returnBook(Integer borrowid) {
        Borrow borrow = baseMapper.selectById(borrowid);
        if (borrow == null || borrow.getReturntime() != null) {
            return 0;
        }

        // 更新归还时间
        Borrow update = new Borrow();
        update.setBorrowid(borrowid);
        update.setReturntime(LocalDateTime.now());
        baseMapper.updateById(update);

        // 更新图书状态为未借出
        BookInfo bookInfo = new BookInfo();
        bookInfo.setBookid(borrow.getBookid());
        bookInfo.setIsborrowed(0);
        bookInfoMapper.updateById(bookInfo);
        return 1;
    }

    @Override
    public int deleteBorrow(Integer borrowid) {
        return baseMapper.deleteById(borrowid);
    }
}
