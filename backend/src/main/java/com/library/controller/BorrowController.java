package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.Borrow;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Map;

@RestController
@RequestMapping("/api/borrow")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    /**
     * 分页查询借阅记录
     * 对应文档 4.4.1 搜索图书
     */
    @GetMapping("/queryBorrowsByPage")
    public Result<Map<String, Object>> queryBorrowsByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String username,
            @RequestParam(required = false) String bookname,
            @RequestParam(required = false) Integer userid) {

        Page<Borrow> p = new Page<>(page, pageSize);
        IPage<Borrow> result = borrowService.searchByPage(p, username, bookname, userid);
        return Result.list(result.getTotal(), result.getRecords());
    }

    /**
     * 归还图书
     */
    @PostMapping("/returnBook")
    public Result<Void> returnBook(@RequestBody Map<String, Object> params) {
        Integer borrowid = Integer.valueOf(params.get("borrowid").toString());
        int ret = borrowService.returnBook(borrowid);
        if (ret == 0) {
            return Result.error(400, "借阅记录不存在或已归还");
        }
        return Result.success("归还成功", null);
    }

    /**
     * 删除借阅记录
     * 对应文档 4.4.2 删除图书
     */
    @DeleteMapping("/deleteBorrow")
    public Result<Void> deleteBorrow(@RequestBody Map<String, Object> params) {
        Integer borrowid = Integer.valueOf(params.get("borrowid").toString());
        borrowService.deleteBorrow(borrowid);
        return Result.success();
    }
}
