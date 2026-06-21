<template>
  <div class="page-container">
    <!-- 操作工具栏 -->
    <el-card shadow="never" class="toolbar-card">
      <div class="toolbar">
        <el-form :inline="true" :model="queryParams" class="toolbar-form">
          <el-form-item label="类型名">
            <el-input
              v-model="queryParams.typename"
              placeholder="请输入类型名"
              clearable
              @keyup.enter="handleSearch"
            />
          </el-form-item>
          <el-form-item>
            <el-button type="primary" :icon="Search" @click="handleSearch">搜索</el-button>
            <el-button type="primary" :icon="Refresh" @click="handleShowAll">显示全部</el-button>
          </el-form-item>
        </el-form>
        <div class="toolbar-actions">
          <el-button type="primary" :icon="Plus" @click="handleAdd">添加类型</el-button>
          <el-button type="danger" :icon="Delete" @click="handleBatchDelete">批量删除</el-button>
        </div>
      </div>
    </el-card>

    <!-- 表格 -->
    <el-card shadow="never" class="table-card">
      <el-table
        ref="tableRef"
        v-loading="loading"
        :data="tableData"
        stripe
        border
        style="width: 100%"
        @selection-change="handleSelectionChange"
      >
        <el-table-column type="selection" width="50" align="center" />
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="typename" label="类型名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="description" label="类型描述" min-width="240" show-overflow-tooltip />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link size="small" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link size="small" @click="handleDelete(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>

      <!-- 分页 -->
      <el-pagination
        class="pagination"
        v-model:current-page="queryParams.page"
        v-model:page-size="queryParams.pageSize"
        :page-sizes="[5, 10, 20, 50]"
        :total="total"
        layout="total, sizes, prev, pager, next, jumper"
        background
        @size-change="handleSizeChange"
        @current-change="handleCurrentChange"
      />
    </el-card>

    <!-- 添加/编辑弹窗 -->
    <el-dialog
      v-model="dialogVisible"
      :title="dialogTitle"
      width="460px"
      :close-on-click-modal="false"
      @closed="handleDialogClosed"
    >
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型名称" prop="typename">
          <el-input v-model="form.typename" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="类型描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入类型描述" />
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitting" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Delete } from '@element-plus/icons-vue'
import { queryBookTypesByPage, addBookType, updateBookType, deleteBookType, batchDeleteBookTypes } from '@/api/bookType'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const selectedRows = ref([])
const tableRef = ref(null)
const formRef = ref(null)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  typename: ''
})

const dialogVisible = ref(false)
const isEdit = ref(false)
const dialogTitle = computed(() => (isEdit.value ? '编辑类型' : '添加类型'))

const form = reactive({
  typeid: null,
  typename: '',
  description: ''
})

const rules = {
  typename: [{ required: true, message: '请输入类型名称', trigger: 'blur' }]
}

// 加载类型列表
async function loadData() {
  loading.value = true
  try {
    const res = await queryBookTypesByPage(queryParams)
    tableData.value = res.data?.list || []
    total.value = res.data?.total || 0
  } catch (e) {
    tableData.value = []
    total.value = 0
  } finally {
    loading.value = false
  }
}

function handleSearch() {
  queryParams.page = 1
  loadData()
}

// 显示全部：清空搜索条件并重新加载
function handleShowAll() {
  queryParams.typename = ''
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
  Object.assign(form, { typeid: null, typename: '', description: '' })
}

function handleAdd() {
  isEdit.value = false
  resetForm()
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  Object.assign(form, {
    typeid: row.typeid,
    typename: row.typename,
    description: row.description || ''
  })
  dialogVisible.value = true
}

// 弹窗关闭后 resetFields 清空表单
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
        await updateBookType({ ...form })
        ElMessage.success('修改成功')
      } else {
        const { typeid, ...data } = form
        await addBookType(data)
        ElMessage.success('添加成功')
      }
      dialogVisible.value = false
      loadData()
    } catch (e) {
      // 错误已由拦截器统一提示
    } finally {
      submitting.value = false
    }
  })
}

// 单个删除
async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除类型「${row.typename}」吗？`, '删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBookType({ typeid: row.typeid })
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 用户取消或请求失败
  }
}

// 批量删除
async function handleBatchDelete() {
  if (selectedRows.value.length === 0) {
    ElMessage.warning('请至少选择一条记录')
    return
  }
  try {
    await ElMessageBox.confirm(`确定要删除选中的 ${selectedRows.value.length} 条类型吗？`, '批量删除确认', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    const ids = selectedRows.value.map((row) => row.typeid)
    await batchDeleteBookTypes({ ids })
    ElMessage.success('批量删除成功')
    tableRef.value?.clearSelection()
    loadData()
  } catch (e) {
    // 用户取消或请求失败
  }
}

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.toolbar-card {
  margin-bottom: 16px;
}

.toolbar {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
  gap: 12px;
}

.toolbar-form :deep(.el-form-item) {
  margin-bottom: 0;
}

.toolbar-actions {
  display: flex;
  gap: 8px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
