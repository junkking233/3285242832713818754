<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="类型名称">
          <el-input
            v-model="queryParams.typename"
            placeholder="请输入类型名称"
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
    <el-card shadow="never" class="table-card">
      <div class="table-toolbar">
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加类型</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="typeid" label="类型ID" width="100" align="center" />
        <el-table-column prop="typename" label="类型名称" min-width="160" show-overflow-tooltip />
        <el-table-column prop="description" label="描述" min-width="240" show-overflow-tooltip />
        <el-table-column label="操作" width="160" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button type="danger" link :icon="Delete" @click="handleDelete(scope.row)">删除</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="460px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="类型名称" prop="typename">
          <el-input v-model="form.typename" placeholder="请输入类型名称" />
        </el-form-item>
        <el-form-item label="描述" prop="description">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入描述" />
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
import { ref, reactive, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search, Refresh, Plus, Edit, Delete } from '@element-plus/icons-vue'
import { queryBookTypesByPage, addBookType, updateBookType, deleteBookType } from '@/api/bookType'

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  typename: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加类型')
const formRef = ref(null)
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

function handleReset() {
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

function handleAdd() {
  dialogTitle.value = '添加类型'
  Object.assign(form, { typeid: null, typename: '', description: '' })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑类型'
  Object.assign(form, {
    typeid: row.typeid,
    typename: row.typename,
    description: row.description || ''
  })
  dialogVisible.value = true
}

function resetForm() {
  formRef.value?.clearValidate()
}

async function handleSubmit() {
  if (!formRef.value) return
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    submitting.value = true
    try {
      if (form.typeid) {
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

async function handleDelete(row) {
  try {
    await ElMessageBox.confirm(`确定要删除类型「${row.typename}」吗？`, '提示', {
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

onMounted(() => {
  loadData()
})
</script>

<style scoped>
.page-container {
  padding: 20px;
}

.search-card {
  margin-bottom: 20px;
}

.search-card :deep(.el-form-item) {
  margin-bottom: 0;
}

.table-toolbar {
  margin-bottom: 16px;
}

.pagination {
  margin-top: 16px;
  justify-content: flex-end;
}
</style>
