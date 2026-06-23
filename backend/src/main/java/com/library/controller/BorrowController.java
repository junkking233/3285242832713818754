package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.Borrow;
import com.library.service.BorrowService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
            @RequestParam(required = false) Integer userid,
            HttpServletRequest request) {

        String role = (String) request.getAttribute("role");
        if (!"ADMIN".equals(role)) {
            userid = (Integer) request.getAttribute("userId");
            username = null;
        }

        Page<Borrow> p = new Page<>(page, pageSize);
        IPage<Borrow> result = borrowService.searchByPage(p, username, bookname, userid);
        return Result.list(result.getTotal(), result.getRecords());
    }

    /**
     * 归还图书
     * 普通读者只能归还自己的借阅记录，管理员可以处理全部记录。
     */
    @PostMapping("/returnBook")
    public Result<Void> returnBook(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Object borrowidObj = params.get("borrowid");
        if (borrowidObj == null) {
            return Result.error(400, "借阅ID不能为空");
        }

        Integer borrowid = Integer.valueOf(borrowidObj.toString());
        Borrow borrow = borrowService.getBorrowById(borrowid);
        if (borrow == null) {
            return Result.error(404, "借阅记录不存在");
        }

        Integer currentUserId = (Integer) request.getAttribute("userId");
        String role = (String) request.getAttribute("role");
        boolean isAdmin = "ADMIN".equals(role);
        if (!isAdmin && (currentUserId == null || !currentUserId.equals(borrow.getUserid()))) {
            return Result.error(403, "只能归还自己的借阅记录");
        }

        if (borrow.getReturntime() != null) {
            return Result.error(400, "该图书已归还");
        }

        int ret = borrowService.returnBook(borrowid);
        if (ret == 0) {
            return Result.error(400, "归还失败，请刷新后重试");
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

    /**
     * 批量删除借阅记录
     */
    @DeleteMapping("/batchDelete")
    public Result<Void> batchDelete(@RequestBody Map<String, Object> params) {
        @SuppressWarnings("unchecked")
        List<Object> idList = (List<Object>) params.get("ids");
        if (idList == null || idList.isEmpty()) {
            return Result.error(400, "请选择要删除的记录");
        }
        List<Integer> ids = idList.stream()
                .map(o -> Integer.valueOf(o.toString()))
                .collect(Collectors.toList());
        for (Integer id : ids) {
            borrowService.deleteBorrow(id);
        }
        return Result.success();
    }
}
