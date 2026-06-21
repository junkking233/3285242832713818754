package com.library.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@TableName("book_info")
public class BookInfo {

    @TableId(type = IdType.AUTO)
    private Integer bookid;

    private String bookname;

    private String author;

    private BigDecimal price;

    private Integer typeid;

    private String description;

    private Integer isborrowed;

    private LocalDateTime createtime;

    // ---------- 非数据库字段（关联查询用） ----------

    @com.baomidou.mybatisplus.annotation.TableField(exist = false)
    private String typename;

    // ---------- getters / setters ----------

    public Integer getBookid() {
        return bookid;
    }

    public void setBookid(Integer bookid) {
        this.bookid = bookid;
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

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getTypeid() {
        return typeid;
    }

    public void setTypeid(Integer typeid) {
        this.typeid = typeid;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getIsborrowed() {
        return isborrowed;
    }

    public void setIsborrowed(Integer isborrowed) {
        this.isborrowed = isborrowed;
    }

    public LocalDateTime getCreatetime() {
        return createtime;
    }

    public void setCreatetime(LocalDateTime createtime) {
        this.createtime = createtime;
    }

    public String getTypename() {
        return typename;
    }

    public void setTypename(String typename) {
        this.typename = typename;
    }
}
