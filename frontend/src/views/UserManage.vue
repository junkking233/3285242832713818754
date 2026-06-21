<template>
  <div class="page-container">
    <!-- 操作工具栏 -->
    <div class="toolbar">
      <div class="toolbar-left">
        <el-input
          v-model="queryParams.username"
          placeholder="用户名"
          clearable
          style="width: 200px"
          @keyup.enter="handleSearch"
        />
        <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
        <el-button type="primary" :icon="Refresh" @click="handleShowAll">显示全部</el-button>
      </div>
      <div class="toolbar-right">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加用户</el-button>
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
      <el-table-column prop="username" label="用户名称" min-width="120" show-overflow-tooltip />
      <el-table-column prop="password" label="用户密码" min-width="120" show-overflow-tooltip />
      <el-table-column label="用户身份" width="120" align="center">
        <template #default="{ row }">
          <el-tag v-if="row.role === 'ADMIN'" type="warning" size="small">管理员</el-tag>
          <el-tag v-else type="success" size="small">读者</el-tag>
        </template>
      </el-table-column>
      <el-table-column label="操作" width="140" align="center" fixed="right">
        <template #default="{ row }">
          <el-button type="primary" link size="small" @click="handleEdit(row)">编辑</el-button>
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

    <!-- 添加/编辑对话框 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="500px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="身份" prop="role">
          <el-select v-model="form.role" placeholder="请选择身份" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="读者" value="READER" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取 消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确 定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { queryUsersByPage, addUser, updateUser, deleteUser, batchDeleteUsers } from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  username: ''
})

const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)

const dialogTitle = computed(() => (isEdit.value ? '编辑用户' : '添加用户'))

const form = reactive({
  userid: null,
  username: '',
  password: '',
  role: 'READER'
})

// 添加时密码必填，编辑时密码可选（留空不修改）
const rules = computed(() => ({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: isEdit.value
    ? []
    : [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择身份', trigger: 'change' }]
}))

async function loadData() {
  loading.value = true
  try {
    const res = await queryUsersByPage(queryParams)
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

function resetForm() {
  form.userid = null
  form.username = ''
  form.password = ''
  form.role = 'READER'
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  resetForm()
  form.userid = row.userid
  form.username = row.username
  form.role = row.role
  form.password = ''
  dialogVisible.value = true
}

function handleDialogClosed() {
  formRef.value?.resetFields()
  resetForm()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) {
        const data = {
          userid: form.userid,
          username: form.username,
          role: form.role
        }
        // 密码留空表示不修改
        if (form.password) {
          data.password = form.password
        }
        await updateUser(data)
        ElMessage.success('编辑成功')
      } else {
        await addUser({
          username: form.username,
          password: form.password,
          role: form.role
        })
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已在拦截器处理
    } finally {
      submitting.value = false
    }
  })
}

function handleDelete(row) {
  ElMessageBox.confirm(`确定要删除用户「${row.username}」吗？`, '删除确认', {
    confirmButtonText: '确定',
    cancelButtonText: '取消',
    type: 'warning'
  }).then(async () => {
    try {
      await deleteUser({ userid: row.userid })
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
    `确定要删除选中的 ${selectedRows.value.length} 个用户吗？`,
    '批量删除确认',
    {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    }
  ).then(async () => {
    try {
      const ids = selectedRows.value.map((row) => row.userid)
      await batchDeleteUsers({ ids })
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
