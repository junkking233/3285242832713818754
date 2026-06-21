<template>
  <div class="welcome">
    <el-card shadow="always">
      <div class="welcome-content">
        <el-icon :size="60" color="#409eff"><Reading /></el-icon>
        <h2>欢迎来到图书管理系统</h2>
        <p>当前登录用户：{{ userStore.realname || userStore.username }}</p>
        <p>身份：{{ userStore.isAdmin ? '管理员' : '读者' }}</p>
        <p class="tip">请在左侧菜单选择功能模块进行操作</p>
      </div>
    </el-card>
  </div>
</template>

<script setup>
import { useUserStore } from '@/stores/user'
const userStore = useUserStore()
</script>

<style scoped>
.welcome {
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 400px;
}

.welcome-content {
  text-align: center;
  padding: 40px 60px;
}

.welcome-content h2 {
  margin: 20px 0 10px;
  color: #303133;
}

.welcome-content p {
  color: #606266;
  font-size: 14px;
  line-height: 2;
}

.tip {
  color: #909399 !important;
  margin-top: 10px;
}
</style>
