package com.library.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.library.common.Result;
import com.library.entity.BookType;
import com.library.service.BookTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/bookType")
public class BookTypeController {

    @Autowired
    private BookTypeService bookTypeService;

    /**
     * 分页查询图书类型
     * 对应文档 4.3.1 搜索图书
     */
    @GetMapping("/queryBookTypesByPage")
    public Result<Map<String, Object>> queryBookTypesByPage(
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int pageSize,
            @RequestParam(required = false) String typename) {

        Page<BookType> p = new Page<>(page, pageSize);
        IPage<BookType> result = bookTypeService.searchByPage(p, typename);
        return Result.list(result.getTotal(), result.getRecords());
    }

    /**
     * 查询全部图书类型（下拉框用）
     */
    @GetMapping("/listAll")
    public Result<List<BookType>> listAll() {
        return Result.success(bookTypeService.listAll());
    }

    /**
     * 添加图书类型
     */
    @PostMapping("/addBookType")
    public Result<Void> addBookType(@RequestBody BookType bookType) {
        bookTypeService.addBookType(bookType);
        return Result.success();
    }

    /**
     * 更新图书类型
     * 对应文档 4.3.2 编辑图书
     */
    @PutMapping("/updateBookType")
    public Result<Void> updateBookType(@RequestBody BookType bookType) {
        if (bookType.getTypeid() == null) {
            return Result.error(400, "类型ID不能为空");
        }
        bookTypeService.updateBookType(bookType);
        return Result.success();
    }

    /**
     * 删除图书类型
     * 对应文档 4.3.3 删除图书
     */
    @DeleteMapping("/deleteBookType")
    public Result<Void> deleteBookType(@RequestBody Map<String, Object> params) {
        Integer typeid = Integer.valueOf(params.get("typeid").toString());
        bookTypeService.deleteBookType(typeid);
        return Result.success();
    }

    /**
     * 批量删除图书类型
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
            bookTypeService.deleteBookType(id);
        }
        return Result.success();
    }
}
