<template>
  <div class="page-container">
    <!-- 操作工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.username"
          placeholder="用户名"
          clearable
          style="width: 180px"
          @keyup.enter="handleSearch"
        />
        <el-input
          v-model="queryParams.bookname"
          placeholder="图书名"
          clearable
          style="width: 180px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" :icon="Refresh" @click="handleShowAll">显示全部</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
      </div>
    </div>

    <!-- 表格 -->
    <el-table
      :data="tableData"
      v-loading="loading"
      stripe
      border
      style="width: 100%"
      @selection-change="handleSelectionChange"
    >
      <el-table-column type="selection" width="50" align="center" />
      <el-table-column type="index" label="序号" width="60" align="center" />
      <el-table-column prop="username" label="用户名" min-width="120" show-overflow-tooltip />
      <el-table-column prop="bookname" label="图书名" min-width="150" show-overflow-tooltip />
      <el-table-column prop="borrowtime" label="借书时间" width="170" align="center" />
      <el-table-column label="还书时间" width="170" align="center">
        <template #default="{ row }">
          <span>{{ row.returntime || '-' }}</span>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="100" align="center" fixed="right">
        <template #default="{ row }">
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
  </div>
</template>

<script setup>
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Delete } from '@element-plus/icons-vue'
import { queryBorrowsByPage, deleteBorrow, batchDeleteBorrows } from '@/api/borrow'

const loading = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])

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
    tableData.value = res.data.list || []
    total.value = res.data.total || 0
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

function handleShowAll() {
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

function handleSelectionChange(rows) {
  selectedRows.value = rows
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

function handleBatchDelete() {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  ElMessageBox.confirm(
    `确定要删除选中的 ${selectedRows.value.length} 条借阅记录吗？`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map((row) => row.borrowid)
      await batchDeleteBorrows({ ids })
      ElMessage.success('批量删除成功')
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
.page-container {
  padding: 20px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 16px;
  flex-wrap: wrap;
  gap: 10px;
}

.toolbar-left {
  display: flex;
  align-items: center;
  gap: 10px;
  flex-wrap: wrap;
}

.toolbar-right {
  display: flex;
  align-items: center;
  gap: 10px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
