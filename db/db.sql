-- MySQL dump 10.13  Distrib 8.0.46, for Linux (aarch64)
--
-- Host: localhost    Database: library_db
-- ------------------------------------------------------
-- Server version	8.0.46

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `book_info`
--

DROP TABLE IF EXISTS `book_info`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_info` (
  `bookid` int NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `bookname` varchar(100) NOT NULL COMMENT '书名',
  `author` varchar(50) DEFAULT NULL COMMENT '作者',
  `price` decimal(10,2) DEFAULT NULL COMMENT '价格',
  `typeid` int DEFAULT NULL COMMENT '图书类型ID',
  `description` varchar(500) DEFAULT NULL COMMENT '图书描述',
  `isborrowed` tinyint DEFAULT '0' COMMENT '是否借出: 0未借出 1已借出',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '录入时间',
  PRIMARY KEY (`bookid`),
  KEY `fk_book_type` (`typeid`),
  CONSTRAINT `fk_book_type` FOREIGN KEY (`typeid`) REFERENCES `book_type` (`typeid`) ON DELETE SET NULL
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图书信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_info`
--

LOCK TABLES `book_info` WRITE;
/*!40000 ALTER TABLE `book_info` DISABLE KEYS */;
INSERT INTO `book_info` VALUES (1,'Spring Boot实战','Craig Walls',79.00,1,'Spring Boot框架入门到精通',0,'2026-06-21 03:00:12'),(2,'Java核心技术 卷I','Cay S. Horstmann',119.00,1,'Java编程基础与进阶',0,'2026-06-21 03:00:12'),(3,'Vue.js前端开发实战','梁灏',89.00,1,'Vue.js 3 从入门到实战',0,'2026-06-21 03:00:12'),(4,'MySQL数据库原理与应用','徐明华',69.00,1,'MySQL数据库设计与开发',0,'2026-06-21 03:00:12'),(5,'三体','刘慈欣',58.00,2,'科幻文学经典三部曲',0,'2026-06-21 03:00:12'),(6,'活着','余华',35.00,2,'当代文学代表作',0,'2026-06-21 03:00:12'),(7,'百年孤独','加西亚·马尔克斯',45.00,2,'魔幻现实主义文学经典',0,'2026-06-21 03:00:12'),(8,'史记','司马迁',128.00,3,'中国第一部纪传体通史',0,'2026-06-21 03:00:12'),(9,'明朝那些事儿','当年明月',88.00,3,'通俗历史读物',0,'2026-06-21 03:00:12'),(10,'经济学原理','曼昆',99.00,4,'经济学入门经典教材',0,'2026-06-21 03:00:12'),(11,'从0到1','彼得·蒂尔',45.00,4,'创业与商业思维',0,'2026-06-21 03:00:12'),(12,'时间简史','霍金',45.00,5,'宇宙学科普经典',0,'2026-06-21 03:00:12'),(13,'高等数学','同济大学',56.00,5,'大学数学教材',1,'2026-06-21 03:00:12'),(14,'Python编程从入门到实践','Eric Matthes',89.00,1,'Python编程入门',0,'2026-06-21 03:00:12'),(15,'围城','钱钟书',35.00,2,'现代文学经典讽刺小说',0,'2026-06-21 03:00:12');
/*!40000 ALTER TABLE `book_info` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `book_type`
--

DROP TABLE IF EXISTS `book_type`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_type` (
  `typeid` int NOT NULL AUTO_INCREMENT COMMENT '类型ID',
  `typename` varchar(50) NOT NULL COMMENT '类型名称',
  `description` varchar(200) DEFAULT NULL COMMENT '类型描述',
  PRIMARY KEY (`typeid`)
) ENGINE=InnoDB AUTO_INCREMENT=6 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='图书类型表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_type`
--

LOCK TABLES `book_type` WRITE;
/*!40000 ALTER TABLE `book_type` DISABLE KEYS */;
INSERT INTO `book_type` VALUES (1,'计算机科学','计算机与信息技术类图书'),(2,'文学小说','中外文学小说作品'),(3,'历史哲学','历史、哲学、社会科学类图书'),(4,'经济管理','经济学与管理学类图书'),(5,'自然科学','数学、物理、化学、生物等自然科学类图书');
/*!40000 ALTER TABLE `book_type` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `borrow`
--

DROP TABLE IF EXISTS `borrow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `borrow` (
  `borrowid` int NOT NULL AUTO_INCREMENT COMMENT '借阅ID',
  `userid` int NOT NULL COMMENT '用户ID',
  `bookid` int NOT NULL COMMENT '图书ID',
  `borrowtime` datetime DEFAULT NULL COMMENT '借阅时间',
  `returntime` datetime DEFAULT NULL COMMENT '归还时间',
  PRIMARY KEY (`borrowid`),
  KEY `fk_borrow_user` (`userid`),
  KEY `fk_borrow_book` (`bookid`),
  CONSTRAINT `fk_borrow_book` FOREIGN KEY (`bookid`) REFERENCES `book_info` (`bookid`) ON DELETE CASCADE,
  CONSTRAINT `fk_borrow_user` FOREIGN KEY (`userid`) REFERENCES `user` (`userid`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='借阅信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `borrow`
--

LOCK TABLES `borrow` WRITE;
/*!40000 ALTER TABLE `borrow` DISABLE KEYS */;
INSERT INTO `borrow` VALUES (4,1,13,'2026-06-23 10:22:33',NULL),(5,2,12,'2026-06-23 10:23:15','2026-06-23 10:28:18'),(6,2,12,'2026-06-23 10:28:23','2026-06-23 10:28:28');
/*!40000 ALTER TABLE `borrow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user`
--

DROP TABLE IF EXISTS `user`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user` (
  `userid` int NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` varchar(50) NOT NULL COMMENT '用户名',
  `password` varchar(64) NOT NULL COMMENT '密码(MD5)',
  `role` varchar(20) NOT NULL DEFAULT 'READER' COMMENT '角色: ADMIN管理员 / READER读者',
  `realname` varchar(50) DEFAULT NULL COMMENT '真实姓名',
  `phone` varchar(20) DEFAULT NULL COMMENT '联系电话',
  `createtime` datetime DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`userid`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='用户信息表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user`
--

LOCK TABLES `user` WRITE;
/*!40000 ALTER TABLE `user` DISABLE KEYS */;
INSERT INTO `user` VALUES (1,'admin','0192023a7bbd73250516f069df18b500','ADMIN','系统管理员','13800000000','2026-06-21 03:00:12'),(2,'reader','e10adc3949ba59abbe56e057f20f883e','READER','张三','13900000001','2026-06-21 03:00:12'),(3,'lisi','e10adc3949ba59abbe56e057f20f883e','READER','李四','13900000002','2026-06-21 03:00:12'),(4,'wangwu','e10adc3949ba59abbe56e057f20f883e','READER','王五','13900000003','2026-06-21 03:00:12');
/*!40000 ALTER TABLE `user` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-06-23  2:39:39
