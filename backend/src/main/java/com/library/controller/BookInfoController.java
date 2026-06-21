package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.BookInfo;
import com.library.service.BookInfoService;
import com.library.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/book")
public class BookInfoController {

    @Autowired
    private BookInfoService bookInfoService;

    @Autowired
    private BorrowService borrowService;

    /**
     * 分页查询图书
     * 对应文档 4.2.1 搜索图书
     */
    @GetMapping("/queryBookInfosByPage")
    public Result<Map<String, Object>> queryBookInfosByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String bookname,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) Integer typeid) {

        Page<BookInfo> p = new Page<>(page, pageSize);
        IPage<BookInfo> result = bookInfoService.searchByPage(p, bookname, author, typeid);
        return Result.list(result.getTotal(), result.getRecords());
    }

    /**
     * 添加图书
     * 对应文档 4.2.2 添加图书
     */
    @PostMapping("/addBookInfo")
    public Result<Void> addBookInfo(@RequestBody BookInfo bookInfo) {
        bookInfoService.addBookInfo(bookInfo);
        return Result.success();
    }

    /**
     * 更新图书信息
     */
    @PutMapping("/updateBookInfo")
    public Result<Void> updateBookInfo(@RequestBody BookInfo bookInfo) {
        if (bookInfo.getBookid() == null) {
            return Result.error(400, "图书ID不能为空");
        }
        bookInfoService.updateBookInfo(bookInfo);
        return Result.success();
    }

    /**
     * 删除图书
     */
    @DeleteMapping("/deleteBookInfo")
    public Result<Void> deleteBookInfo(@RequestBody Map<String, Object> params) {
        Integer bookid = Integer.valueOf(params.get("bookid").toString());
        bookInfoService.deleteBookInfo(bookid);
        return Result.success();
    }

    /**
     * 批量删除图书
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
            bookInfoService.deleteBookInfo(id);
        }
        return Result.success();
    }

    /**
     * 借阅图书
     * 对应文档 4.2.3 借阅图书
     */
    @PostMapping("/borrowBook")
    public Result<Void> borrowBook(@RequestBody Map<String, Object> params, HttpServletRequest request) {
        Integer userid = (Integer) request.getAttribute("userId");
        Object uidObj = params.get("userid");
        if (uidObj != null) {
            userid = Integer.valueOf(uidObj.toString());
        }
        if (userid == null) {
            return Result.error(401, "请先登录");
        }

        Object bidObj = params.get("bookid");
        if (bidObj == null) {
            return Result.error(400, "图书ID不能为空");
        }
        Integer bookid = Integer.valueOf(bidObj.toString());

        int ret = borrowService.borrowBook(userid, bookid);
        if (ret == 0) {
            return Result.error(400, "图书不存在或已被借出");
        }
        return Result.success("借阅成功", null);
    }
}
