<template>
  <div class="user-manage">
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
        <el-form-item label="角色">
          <el-select v-model="queryParams.role" placeholder="请选择角色" clearable style="width: 160px">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="读者" value="READER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
          <el-button :icon="Refresh" @click="handleReset">重置</el-button>
        </el-form-item>
      </el-form>
    </el-card>

    <!-- 表格 -->
    <el-card class="table-card" shadow="never">
      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加用户</el-button>
      </div>
      <el-table :data="tableData" v-loading="loading" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="userid" label="用户ID" width="80" align="center" />
        <el-table-column prop="username" label="用户名" width="120" show-overflow-tooltip />
        <el-table-column prop="realname" label="真实姓名" width="120" show-overflow-tooltip />
        <el-table-column label="角色" width="100" align="center">
          <template #default="{ row }">
            <el-tag v-if="row.role === 'ADMIN'" type="danger" size="small">管理员</el-tag>
            <el-tag v-else type="info" size="small">读者</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="电话" width="140" />
        <el-table-column prop="createtime" label="创建时间" width="170" align="center" />
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
    </el-card>

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
          <el-input v-model="form.username" placeholder="请输入用户名" :disabled="isEdit" />
        </el-form-item>
        <el-form-item label="密码" prop="password">
          <el-input
            v-model="form.password"
            type="password"
            show-password
            :placeholder="isEdit ? '留空则不修改密码' : '请输入密码'"
          />
        </el-form-item>
        <el-form-item label="真实姓名" prop="realname">
          <el-input v-model="form.realname" placeholder="请输入真实姓名" />
        </el-form-item>
        <el-form-item label="角色" prop="role">
          <el-select v-model="form.role" placeholder="请选择角色" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="读者" value="READER" />
          </el-select>
        </el-form-item>
        <el-form-item label="电话" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入电话" />
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
import { Search, Refresh, Plus } from '@element-plus/icons-vue'
import { queryUsersByPage, addUser, updateUser, deleteUser } from '@/api/user'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  username: '',
  role: ''
})

const dialogVisible = ref(false)
const formRef = ref()
const isEdit = ref(false)

const dialogTitle = computed(() => (isEdit.value ? '编辑用户' : '添加用户'))

const form = reactive({
  userid: null,
  username: '',
  password: '',
  realname: '',
  role: 'READER',
  phone: ''
})

// 添加时密码必填，编辑时密码可选（留空不修改）
const rules = computed(() => ({
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: isEdit.value
    ? []
    : [{ required: true, message: '请输入密码', trigger: 'blur' }],
  realname: [{ required: true, message: '请输入真实姓名', trigger: 'blur' }],
  role: [{ required: true, message: '请选择角色', trigger: 'change' }]
}))

async function loadData() {
  loading.value = true
  try {
    const res = await queryUsersByPage(queryParams)
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
  queryParams.role = ''
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

function resetForm() {
  form.userid = null
  form.username = ''
  form.password = ''
  form.realname = ''
  form.role = 'READER'
  form.phone = ''
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
  form.realname = row.realname
  form.role = row.role
  form.phone = row.phone
  form.password = ''
  dialogVisible.value = true
}

function handleDialogClosed() {
  formRef.value?.resetFields()
  resetForm()
}

async function handleSubmit() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (isEdit.value) {
        const data = {
          userid: form.userid,
          username: form.username,
          realname: form.realname,
          role: form.role,
          phone: form.phone
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
          realname: form.realname,
          role: form.role,
          phone: form.phone
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.user-manage {
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

.table-toolbar {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
