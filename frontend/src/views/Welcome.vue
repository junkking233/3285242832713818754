<template>
  <div class="welcome">
    <!-- 顶部欢迎横幅 -->
    <div class="banner">
      <div class="banner-content">
        <div class="banner-left">
          <h1>欢迎来到图书管理系统</h1>
          <p class="subtitle">{{ greeting }}，{{ userStore.realname || userStore.username }}</p>
          <p class="role-line">
            <el-tag :type="userStore.isAdmin ? 'danger' : 'success'" effect="dark" round>
              {{ userStore.isAdmin ? '管理员' : '读者' }}
            </el-tag>
            <span class="date-text">{{ todayText }}</span>
          </p>
        </div>
        <div class="banner-right">
          <el-icon :size="100" color="rgba(255,255,255,0.25)"><Reading /></el-icon>
        </div>
      </div>
    </div>

    <!-- 统计卡片 -->
    <el-row :gutter="20" class="stat-row">
      <el-col :span="6">
        <div class="stat-card stat-blue">
          <div class="stat-icon">
            <el-icon :size="36"><Reading /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.bookCount }}</div>
            <div class="stat-label">图书总数</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-orange">
          <div class="stat-icon">
            <el-icon :size="36"><Tickets /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.borrowCount }}</div>
            <div class="stat-label">借阅记录</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-green">
          <div class="stat-icon">
            <el-icon :size="36"><UserFilled /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.userCount }}</div>
            <div class="stat-label">注册用户</div>
          </div>
        </div>
      </el-col>
      <el-col :span="6">
        <div class="stat-card stat-cyan">
          <div class="stat-icon">
            <el-icon :size="36"><Files /></el-icon>
          </div>
          <div class="stat-body">
            <div class="stat-value">{{ stats.typeCount }}</div>
            <div class="stat-label">图书类型</div>
          </div>
        </div>
      </el-col>
    </el-row>

    <!-- 快捷入口 + 系统信息 -->
    <el-row :gutter="20" style="margin-top: 20px">
      <el-col :span="16">
        <el-card shadow="never" class="quick-card">
          <template #header>
            <span class="card-title"><el-icon><Grid /></el-icon> 快捷入口</span>
          </template>
          <div class="quick-grid">
            <div class="quick-item" @click="$router.push('/admin/book-info')">
              <el-icon :size="32" color="#409eff"><Reading /></el-icon>
              <span>图书查询</span>
            </div>
            <div class="quick-item" @click="$router.push('/admin/borrow')">
              <el-icon :size="32" color="#e6a23c"><Tickets /></el-icon>
              <span>借阅记录</span>
            </div>
            <template v-if="userStore.isAdmin">
              <div class="quick-item" @click="$router.push('/admin/book-type')">
                <el-icon :size="32" color="#36cfc9"><Files /></el-icon>
                <span>类型管理</span>
              </div>
              <div class="quick-item" @click="$router.push('/admin/user')">
                <el-icon :size="32" color="#f56c6c"><UserFilled /></el-icon>
                <span>用户管理</span>
              </div>
            </template>
            <div class="quick-item" @click="$router.push('/admin/change-password')">
              <el-icon :size="32" color="#67c23a"><Key /></el-icon>
              <span>修改密码</span>
            </div>
          </div>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card shadow="never" class="info-card">
          <template #header>
            <span class="card-title"><el-icon><InfoFilled /></el-icon> 系统信息</span>
          </template>
          <div class="info-list">
            <div class="info-row">
              <span class="info-label">系统名称</span>
              <span class="info-value">图书管理系统</span>
            </div>
            <div class="info-row">
              <span class="info-label">技术架构</span>
              <span class="info-value">SpringBoot + Vue</span>
            </div>
            <div class="info-row">
              <span class="info-label">数据库</span>
              <span class="info-value">MySQL 8.0</span>
            </div>
            <div class="info-row">
              <span class="info-label">当前时间</span>
              <span class="info-value">{{ currentTime }}</span>
            </div>
          </div>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script setup>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useUserStore } from '@/stores/user'
import { queryBookInfosByPage } from '@/api/bookInfo'
import { queryBorrowsByPage } from '@/api/borrow'
import { queryUsersByPage } from '@/api/user'
import { queryBookTypesByPage } from '@/api/bookType'

const userStore = useUserStore()

const stats = ref({
  bookCount: 0,
  borrowCount: 0,
  userCount: 0,
  typeCount: 0
})

const currentTime = ref('')
let timer = null

const greeting = computed(() => {
  const h = new Date().getHours()
  if (h < 6) return '凌晨好'
  if (h < 9) return '早上好'
  if (h < 12) return '上午好'
  if (h < 14) return '中午好'
  if (h < 18) return '下午好'
  return '晚上好'
})

const todayText = computed(() => {
  const d = new Date()
  const week = ['日', '一', '二', '三', '四', '五', '六']
  return `${d.getFullYear()}年${d.getMonth() + 1}月${d.getDate()}日 星期${week[d.getDay()]}`
})

function updateTime() {
  const d = new Date()
  const pad = (n) => String(n).padStart(2, '0')
  currentTime.value = `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())} ${pad(d.getHours())}:${pad(d.getMinutes())}:${pad(d.getSeconds())}`
}

async function loadStats() {
  try {
    const [bookRes, borrowRes, userRes, typeRes] = await Promise.all([
      queryBookInfosByPage({ page: 1, pageSize: 1 }),
      queryBorrowsByPage({ page: 1, pageSize: 1 }),
      queryUsersByPage({ page: 1, pageSize: 1 }),
      queryBookTypesByPage({ page: 1, pageSize: 1 })
    ])
    stats.value.bookCount = bookRes.data.total
    stats.value.borrowCount = borrowRes.data.total
    stats.value.userCount = userRes.data.total
    stats.value.typeCount = typeRes.data.total
  } catch (e) {
    // 静默处理
  }
}

onMounted(() => {
  updateTime()
  timer = setInterval(updateTime, 1000)
  loadStats()
})

onUnmounted(() => {
  if (timer) clearInterval(timer)
})
</script>

<style scoped>
.welcome {
  padding: 0;
}

/* ===== 横幅 ===== */
.banner {
  background: linear-gradient(135deg, #4facfe 0%, #00c6fb 100%);
  border-radius: 10px;
  padding: 36px 40px;
  color: #fff;
  box-shadow: 0 4px 16px rgba(79, 172, 254, 0.3);
}

.banner-content {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.banner-left h1 {
  font-size: 28px;
  margin: 0 0 10px;
  font-weight: 600;
}

.subtitle {
  font-size: 16px;
  opacity: 0.9;
  margin: 0 0 12px;
}

.role-line {
  display: flex;
  align-items: center;
  gap: 12px;
  margin: 0;
}

.date-text {
  font-size: 13px;
  opacity: 0.8;
}

/* ===== 统计卡片 ===== */
.stat-row {
  margin-top: 20px;
}

.stat-card {
  display: flex;
  align-items: center;
  padding: 22px 24px;
  border-radius: 10px;
  gap: 18px;
  color: #fff;
  box-shadow: 0 2px 12px rgba(0, 0, 0, 0.08);
  transition: transform 0.2s;
}

.stat-card:hover {
  transform: translateY(-3px);
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.12);
}

.stat-blue { background: linear-gradient(135deg, #409eff, #36cfc9); }
.stat-orange { background: linear-gradient(135deg, #e6a23c, #f7b955); }
.stat-green { background: linear-gradient(135deg, #67c23a, #85d667); }
.stat-cyan { background: linear-gradient(135deg, #00b4d8, #48cae4); }

.stat-icon {
  width: 60px;
  height: 60px;
  display: flex;
  align-items: center;
  justify-content: center;
  background: rgba(255, 255, 255, 0.2);
  border-radius: 50%;
}

.stat-value {
  font-size: 30px;
  font-weight: 700;
  line-height: 1.2;
}

.stat-label {
  font-size: 14px;
  opacity: 0.9;
  margin-top: 4px;
}

/* ===== 快捷入口 ===== */
.quick-card, .info-card {
  border-radius: 10px;
}

.card-title {
  font-size: 15px;
  font-weight: 600;
  color: #303133;
  display: flex;
  align-items: center;
  gap: 6px;
}

.quick-grid {
  display: grid;
  grid-template-columns: repeat(4, 1fr);
  gap: 16px;
}

.quick-item {
  display: flex;
  flex-direction: column;
  align-items: center;
  gap: 10px;
  padding: 24px 12px;
  border-radius: 8px;
  background: #f5f7fa;
  cursor: pointer;
  transition: all 0.2s;
}

.quick-item:hover {
  background: #ecf5ff;
  transform: translateY(-2px);
}

.quick-item span {
  font-size: 13px;
  color: #606266;
}

/* ===== 系统信息 ===== */
.info-list {
  display: flex;
  flex-direction: column;
  gap: 14px;
}

.info-row {
  display: flex;
  justify-content: space-between;
  align-items: center;
  font-size: 13px;
  padding-bottom: 10px;
  border-bottom: 1px solid #f0f0f0;
}

.info-row:last-child {
  border-bottom: none;
  padding-bottom: 0;
}

.info-label {
  color: #909399;
}

.info-value {
  color: #303133;
  font-weight: 500;
}
</style>
