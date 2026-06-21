-- ============================================================
-- 图书管理系统数据库 library_db
-- 基于SpringBoot + Vue的图书管理系统
-- ============================================================

DROP DATABASE IF EXISTS library_db;
CREATE DATABASE library_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE library_db;

-- ------------------------------------------------------------
-- 用户表（管理员 + 读者）
-- ------------------------------------------------------------
DROP TABLE IF EXISTS user;
CREATE TABLE user (
    userid       INT AUTO_INCREMENT PRIMARY KEY              COMMENT '用户ID',
    username     VARCHAR(50)  NOT NULL                       COMMENT '用户名',
    password     VARCHAR(64)  NOT NULL                       COMMENT '密码(MD5)',
    role         VARCHAR(20)  NOT NULL DEFAULT 'READER'      COMMENT '角色: ADMIN管理员 / READER读者',
    realname     VARCHAR(50)                                 COMMENT '真实姓名',
    phone        VARCHAR(20)                                 COMMENT '联系电话',
    createtime   DATETIME DEFAULT CURRENT_TIMESTAMP          COMMENT '创建时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户信息表';

-- ------------------------------------------------------------
-- 图书类型表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS book_type;
CREATE TABLE book_type (
    typeid        INT AUTO_INCREMENT PRIMARY KEY             COMMENT '类型ID',
    typename      VARCHAR(50) NOT NULL                       COMMENT '类型名称',
    description   VARCHAR(200)                               COMMENT '类型描述'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书类型表';

-- ------------------------------------------------------------
-- 图书信息表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS book_info;
CREATE TABLE book_info (
    bookid        INT AUTO_INCREMENT PRIMARY KEY             COMMENT '图书ID',
    bookname      VARCHAR(100) NOT NULL                      COMMENT '书名',
    author        VARCHAR(50)                                COMMENT '作者',
    price         DECIMAL(10,2)                              COMMENT '价格',
    typeid        INT                                        COMMENT '图书类型ID',
    description   VARCHAR(500)                               COMMENT '图书描述',
    isborrowed    TINYINT DEFAULT 0                          COMMENT '是否借出: 0未借出 1已借出',
    createtime    DATETIME DEFAULT CURRENT_TIMESTAMP         COMMENT '录入时间',
    CONSTRAINT fk_book_type FOREIGN KEY (typeid) REFERENCES book_type(typeid) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书信息表';

-- ------------------------------------------------------------
-- 借阅信息表
-- ------------------------------------------------------------
DROP TABLE IF EXISTS borrow;
CREATE TABLE borrow (
    borrowid      INT AUTO_INCREMENT PRIMARY KEY             COMMENT '借阅ID',
    userid        INT NOT NULL                               COMMENT '用户ID',
    bookid        INT NOT NULL                               COMMENT '图书ID',
    borrowtime    DATETIME                                   COMMENT '借阅时间',
    returntime    DATETIME                                   COMMENT '归还时间',
    CONSTRAINT fk_borrow_user FOREIGN KEY (userid) REFERENCES user(userid) ON DELETE CASCADE,
    CONSTRAINT fk_borrow_book FOREIGN KEY (bookid) REFERENCES book_info(bookid) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅信息表';

-- ============================================================
-- 初始数据
-- ============================================================

-- 用户：管理员 admin/admin123, 读者 reader/123456
INSERT INTO user (username, password, role, realname, phone) VALUES
('admin',   '0192023a7bbd73250516f069df18b500', 'ADMIN',  '系统管理员', '13800000000'),
('reader',  'e10adc3949ba59abbe56e057f20f883e', 'READER', '张三',       '13900000001'),
('lisi',    'e10adc3949ba59abbe56e057f20f883e', 'READER', '李四',       '13900000002'),
('wangwu',  'e10adc3949ba59abbe56e057f20f883e', 'READER', '王五',       '13900000003');

-- 图书类型
INSERT INTO book_type (typename, description) VALUES
('计算机科学', '计算机与信息技术类图书'),
('文学小说',   '中外文学小说作品'),
('历史哲学',   '历史、哲学、社会科学类图书'),
('经济管理',   '经济学与管理学类图书'),
('自然科学',   '数学、物理、化学、生物等自然科学类图书');

-- 图书信息
INSERT INTO book_info (bookname, author, price, typeid, description, isborrowed) VALUES
('Spring Boot实战',         'Craig Walls',   79.00, 1, 'Spring Boot框架入门到精通',              0),
('Java核心技术 卷I',        'Cay S. Horstmann', 119.00, 1, 'Java编程基础与进阶',                    0),
('Vue.js前端开发实战',      '梁灏',           89.00, 1, 'Vue.js 3 从入门到实战',                  0),
('MySQL数据库原理与应用',   '徐明华',         69.00, 1, 'MySQL数据库设计与开发',                  0),
('三体',                    '刘慈欣',         58.00, 2, '科幻文学经典三部曲',                     1),
('活着',                    '余华',           35.00, 2, '当代文学代表作',                         0),
('百年孤独',                '加西亚·马尔克斯', 45.00, 2, '魔幻现实主义文学经典',                   0),
('史记',                    '司马迁',         128.00, 3, '中国第一部纪传体通史',                   0),
('明朝那些事儿',            '当年明月',       88.00, 3, '通俗历史读物',                           0),
('经济学原理',              '曼昆',           99.00, 4, '经济学入门经典教材',                     0),
('从0到1',                  '彼得·蒂尔',      45.00, 4, '创业与商业思维',                         0),
('时间简史',                '霍金',           45.00, 5, '宇宙学科普经典',                         0),
('高等数学',                '同济大学',       56.00, 5, '大学数学教材',                           0),
('Python编程从入门到实践',  'Eric Matthes',   89.00, 1, 'Python编程入门',                         0),
('围城',                    '钱钟书',         35.00, 2, '现代文学经典讽刺小说',                   0);

-- 借阅记录（三体已被借出）
INSERT INTO borrow (userid, bookid, borrowtime, returntime) VALUES
(2, 5, '2026-06-15 10:30:00', NULL);
