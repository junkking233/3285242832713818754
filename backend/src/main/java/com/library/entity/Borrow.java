package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.time.LocalDateTime;

@TableName("borrow")
public class Borrow {

    @TableId(type = IdType.AUTO)
    private Integer borrowid;

    private Integer userid;

    private Integer bookid;

    private LocalDateTime borrowtime;

    private LocalDateTime returntime;

    // ---------- 非数据库字段（关联查询用） ----------

    @TableField(exist = false)
    private String username;

    @TableField(exist = false)
    private String realname;

    @TableField(exist = false)
    private String bookname;

    @TableField(exist = false)
    private String author;

    // ---------- getters / setters ----------

    public Integer getBorrowid() {
        return borrowid;
    }

    public void setBorrowid(Integer borrowid) {
        this.borrowid = borrowid;
    }

    public Integer getUserid() {
        return userid;
    }

    public void setUserid(Integer userid) {
        this.userid = userid;
    }

    public Integer getBookid() {
        return bookid;
    }

    public void setBookid(Integer bookid) {
        this.bookid = bookid;
    }

    public LocalDateTime getBorrowtime() {
        return borrowtime;
    }

    public void setBorrowtime(LocalDateTime borrowtime) {
        this.borrowtime = borrowtime;
    }

    public LocalDateTime getReturntime() {
        return returntime;
    }

    public void setReturntime(LocalDateTime returntime) {
        this.returntime = returntime;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getRealname() {
        return realname;
    }

    public void setRealname(String realname) {
        this.realname = realname;
    }

    public String getBookname() {
        return bookname;
    }

    public void setBookname(String bookname) {
        this.bookname = bookname;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }
}
