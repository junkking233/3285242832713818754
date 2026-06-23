package com.library.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.entity.Borrow;

public interface BorrowService {

    IPage<Borrow> searchByPage(Page<Borrow> page, String username, String bookname, Integer userid);

    /**
     * 借阅图书：检查图书状态、更新图书状态、生成借阅记录
     * @return 1 成功，0 失败（图书不存在或已借出）
     */
    int borrowBook(Integer userid, Integer bookid);

    /**
     * 归还图书
     */
    int returnBook(Integer borrowid);

    Borrow getBorrowById(Integer borrowid);

    int deleteBorrow(Integer borrowid);
}
