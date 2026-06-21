<template>
  <div class="dashboard-container">
    <!-- 欢迎横幅 -->
    <el-card class="welcome-card" shadow="never">
      <div class="welcome-content">
        <div class="welcome-text">
          <h2>欢迎回来，{{ displayName }} 👋</h2>
          <p>欢迎使用图书管理系统，祝您工作愉快！</p>
        </div>
        <el-icon class="welcome-icon"><Reading /></el-icon>
      </div>
    </el-card>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon stat-blue">
              <el-icon><Reading /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.bookTotal }}</div>
              <div class="stat-label">图书总数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon stat-cyan">
              <el-icon><Tickets /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.borrowedTotal }}</div>
              <div class="stat-label">已借出数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon stat-sky">
              <el-icon><Document /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.borrowTotal }}</div>
              <div class="stat-label">借阅记录数</div>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="12" :md="6">
        <el-card shadow="hover" class="stat-card">
          <div class="stat-body">
            <div class="stat-icon stat-deep">
              <el-icon><User /></el-icon>
            </div>
            <div class="stat-info">
              <div class="stat-value">{{ stats.userTotal }}</div>
              <div class="stat-label">用户数</div>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>

    <!-- 概况信息 -->
    <el-row :gutter="20" class="info-row">
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">系统概况</span>
          </template>
          <ul class="info-list">
            <li><span>当前用户</span><span>{{ displayName }}</span></li>
            <li><span>用户角色</span><span>{{ userStore.isAdmin ? '管理员' : '普通用户' }}</span></li>
            <li><span>图书总数</span><span>{{ stats.bookTotal }} 本</span></li>
            <li><span>已借出</span><span>{{ stats.borrowedTotal }} 本</span></li>
            <li><span>可借阅</span><span>{{ stats.bookTotal - stats.borrowedTotal }} 本</span></li>
          </ul>
        </el-card>
      </el-col>
      <el-col :xs="24" :md="12">
        <el-card shadow="never">
          <template #header>
            <span class="card-title">借阅概览</span>
          </template>
          <ul class="info-list">
            <li><span>借阅记录总数</span><span>{{ stats.borrowTotal }} 条</span></li>
            <li><span>注册用户数</span><span>{{ stats.userTotal }} 人</span></li>
            <li><span>系统时间</span><span>{{ currentTime }}</span></li>
          </ul>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted, onBeforeUnmount } from 'vue'
import { queryBookInfosByPage } from '@/api/bookInfo'
import { queryBorrowsByPage } from '@/api/borrow'
import { queryUsersByPage } from '@/api/user'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()
const displayName = computed(() => userStore.realname || userStore.username || '管理员')

const stats = reactive({
  bookTotal: 0,
  borrowedTotal: 0,
  borrowTotal: 0,
  userTotal: 0
})

const currentTime = ref('')
let timer = null

function pad(n) {
  return String(n).padStart(2, '0')
}

function updateTime() {
  const now = new Date()
  currentTime.value = `${now.getFullYear()}-${pad(now.getMonth() + 1)}-${pad(now.getDate())} ${pad(now.getHours())}:${pad(now.getMinutes())}:${pad(now.getSeconds())}`
}

// 加载统计数据
async function loadStats() {
  try {
    const [bookRes, borrowedRes, borrowRes, userRes] = await Promise.all([
      queryBookInfosByPage({ page: 1, pageSize: 1 }),
      queryBookInfosByPage({ page: 1, pageSize: 1, isborrowed: 1 }),
      queryBorrowsByPage({ page: 1, pageSize: 1 }),
      queryUsersByPage({ page: 1, pageSize: 1 })
    ])
    stats.bookTotal = bookRes.data?.total ?? 0
    stats.borrowedTotal = borrowedRes.data?.total ?? 0
    stats.borrowTotal = borrowRes.data?.total ?? 0
    stats.userTotal = userRes.data?.total ?? 0
  } catch (e) {
    // 错误已由拦截器统一提示
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadStats()
})

onBeforeUnmount(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.dashboard-container {
  padding: 20px;
}

/* 欢迎横幅：蓝色渐变，禁止紫色 */
.welcome-card {
  margin-bottom: 20px;
  background: linear-gradient(135deg, #409eff 0%, #2db7f5 100%);
  border: none;
}

.welcome-card :deep(.el-card__body) {
  padding: 24px;
}

.welcome-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
  color: #fff;
}

.welcome-text h2 {
  margin: 0 0 8px;
  font-size: 22px;
  font-weight: 600;
}

.welcome-text p {
  margin: 0;
  font-size: 14px;
  opacity: 0.9;
}

.welcome-icon {
  font-size: 64px;
  opacity: 0.4;
}

/* 统计卡片 */
.stat-row {
  margin-bottom: 20px;
}

.stat-card {
  margin-bottom: 20px;
  border: none;
  border-radius: 8px;
}

.stat-card :deep(.el-card__body) {
  padding: 20px;
}

.stat-body {
  display: flex;
  align-items: center;
}

.stat-icon {
  width: 56px;
  height: 56px;
  border-radius: 12px;
  display: flex;
  align-items: center;
  justify-content: center;
  margin-right: 16px;
  color: #fff;
  font-size: 28px;
}

/* 全部蓝色系配色，不使用紫色 */
.stat-blue {
  background: linear-gradient(135deg, #409eff, #2db7f5);
}

.stat-cyan {
  background: linear-gradient(135deg, #36cfc9, #13c2c2);
}

.stat-sky {
  background: linear-gradient(135deg, #1890ff, #096dd9);
}

.stat-deep {
  background: linear-gradient(135deg, #0050b3, #003a8c);
}

.stat-value {
  font-size: 28px;
  font-weight: 700;
  color: #303133;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 4px;
}

/* 概况信息 */
.info-row .el-card {
  margin-bottom: 20px;
}

.card-title {
  font-weight: 600;
  color: #303133;
}

.info-list {
  list-style: none;
  padding: 0;
  margin: 0;
}

.info-list li {
  display: flex;
  justify-content: space-between;
  padding: 10px 0;
  border-bottom: 1px dashed #ebeef5;
  font-size: 14px;
}

.info-list li:last-child {
  border-bottom: none;
}

.info-list li span:first-child {
  color: #909399;
}

.info-list li span:last-child {
  color: #303133;
  font-weight: 500;
}
</style>
