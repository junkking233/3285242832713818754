<template>
  <div class="borrow-manage">
    <!-- 搜索栏 -->
    <el-card class="search-card" shadow="never">
      <el-form :inline="true" :model="queryParams" class="search-form">
        <el-form-item label="用户名">
          <el-input
            v-model="queryParams.username"
            placeholder="请输入用户名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="书名">
          <el-input
            v-model="queryParams.bookname"
            placeholder="请输入书名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="borrowid" label="借阅ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="realname" label="真实姓名" width="100" show-overflow-tooltip />
        <el-table-column prop="bookname" label="书名" min-width="150" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" show-overflow-tooltip />
        <el-table-column prop="borrowtime" label="借阅时间" width="170" align="center" />
        <el-table-column label="归还时间" width="170" align="center">
          <template #default="{ row }">
            <span>{{ row.returntime || '—' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="状态" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="!row.returntime" type="warning" size="small">借阅中</el-tag>
            <el-tag v-else type="success" size="small">已归还</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="{ row }">
            <el-button
              v-if="!row.returntime"
              type="primary"
              link
              size="small"
              @click="handleReturn(row)"
            >归还</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pagination"
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[10, 20, 50, 100]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh } from '@element-plus/icons-vue'
import { queryBorrowsByPage, returnBook, deleteBorrow } from '@/api/borrow'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  username: '',
  bookname: ''
})

async function loadData() {
  loading.value = true
  try {
    const res = await queryBorrowsByPage(queryParams)
    tableData.value = res.data.list
    total.value = res.data.total
  } catch (e) {
    // 错误已在拦截器处理
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadData()
}

function handleReset() {
  queryParams.username = ''
  queryParams.bookname = ''
  queryParams.page = 1
  loadData()
}

function handleSizeChange(size) {
  queryParams.pageSize = size
  queryParams.page = 1
  loadData()
}

function handleCurrentChange(page) {
  queryParams.page = page
  loadData()
}

function handleReturn(row) {
  ElMessageBox.confirm(`确定要归还《${row.bookname}》吗？`, '还书确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await returnBook({ borrowid: row.borrowid })
      ElMessage.success('归还成功')
      loadData()
    } catch (e) {
      // 错误已在拦截器处理
    }
  }).catch(() => {})
}

function handleDelete(row) {
  ElMessageBox.confirm('确定要删除该借阅记录吗？', '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteBorrow({ borrowid: row.borrowid })
      ElMessage.success('删除成功')
      loadData()
    } catch (e) {
      // 错误已在拦截器处理
    }
  }).catch(() => {})
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.borrow-manage {
  display: flex;
  flex-direction: column;
  gap: 16px;
}

.search-card {
  border-radius: 8px;
}

.search-form {
  display: flex;
  flex-wrap: wrap;
}

.table-card {
  border-radius: 8px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
