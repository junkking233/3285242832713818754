<template>
  <div class="page-container">
    <!-- 搜索栏 -->
    <el-card shadow="never" class="search-card">
      <el-form :model="queryParams" inline>
        <el-form-item label="书名">
          <el-input
            v-model="queryParams.bookname"
            placeholder="请输入书名"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="作者">
          <el-input
            v-model="queryParams.author"
            placeholder="请输入作者"
            clearable
            @keyup.enter="handleSearch"
          />
        </el-form-item>
        <el-form-item label="图书类型">
          <el-select
            v-model="queryParams.typeid"
            placeholder="请选择类型"
            clearable
            style="width: 180px"
          >
            <el-option
              v-for="item in bookTypeOptions"
              :key="item.typeid"
              :label="item.typename"
              :value="item.typeid"
            />
          </el-select>
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
        <el-button type="primary" :icon="Plus" @click="handleAdd">添加图书</el-button>
      </div>

      <el-table v-loading="loading" :data="tableData" stripe border style="width: 100%">
        <el-table-column type="index" label="序号" width="60" align="center" />
        <el-table-column prop="bookid" label="图书ID" width="80" align="center" />
        <el-table-column prop="bookname" label="书名" min-width="160" show-overflow-tooltip />
        <el-table-column prop="author" label="作者" width="120" show-overflow-tooltip />
        <el-table-column label="价格" width="100" align="right">
          <template #default="scope">
            ¥{{ Number(scope.row.price).toFixed(2) }}
          </template>
        </el-table-column>
        <el-table-column prop="typename" label="类型" width="120" align="center" />
        <el-table-column label="状态" width="100" align="center">
          <template #default="scope">
            <el-tag v-if="scope.row.isborrowed === 1" type="warning">已借出</el-tag>
            <el-tag v-else type="success">未借出</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="220" align="center" fixed="right">
          <template #default="scope">
            <el-button type="primary" link :icon="Edit" @click="handleEdit(scope.row)">编辑</el-button>
            <el-button
              v-if="scope.row.isborrowed === 0"
              type="success"
              link
              :icon="Promotion"
              @click="handleBorrow(scope.row)"
            >借阅</el-button>
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
    <el-dialog v-model="dialogVisible" :title="dialogTitle" width="500px" @closed="resetForm">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="书名" prop="bookname">
          <el-input v-model="form.bookname" placeholder="请输入书名" />
        </el-form-item>
        <el-form-item label="作者" prop="author">
          <el-input v-model="form.author" placeholder="请输入作者" />
        </el-form-item>
        <el-form-item label="价格" prop="price">
          <el-input-number
            v-model="form.price"
            :min="0"
            :precision="2"
            :step="1"
            controls-position="right"
            style="width: 100%"
          />
        </el-form-item>
        <el-form-item label="类型" prop="typeid">
          <el-select v-model="form.typeid" placeholder="请选择类型" style="width: 100%">
            <el-option
              v-for="item in bookTypeOptions"
              :key="item.typeid"
              :label="item.typename"
              :value="item.typeid"
            />
          </el-select>
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
import { Search, Refresh, Plus, Edit, Delete, Promotion } from '@element-plus/icons-vue'
import { queryBookInfosByPage, addBookInfo, updateBookInfo, deleteBookInfo, borrowBook } from '@/api/bookInfo'
import { listAllBookTypes } from '@/api/bookType'
import { useUserStore } from '@/stores/user'

const userStore = useUserStore()

const loading = ref(false)
const submitting = ref(false)
const tableData = ref([])
const total = ref(0)
const bookTypeOptions = ref([])

const queryParams = reactive({
  page: 1,
  pageSize: 10,
  bookname: '',
  author: '',
  typeid: ''
})

const dialogVisible = ref(false)
const dialogTitle = ref('添加图书')
const formRef = ref(null)
const form = reactive({
  bookid: null,
  bookname: '',
  author: '',
  price: 0,
  typeid: '',
  description: ''
})

const rules = {
  bookname: [{ required: true, message: '请输入书名', trigger: 'blur' }],
  author: [{ required: true, message: '请输入作者', trigger: 'blur' }],
  price: [{ required: true, message: '请输入价格', trigger: 'blur' }],
  typeid: [{ required: true, message: '请选择类型', trigger: 'change' }]
}

// 加载图书类型下拉选项
async function loadBookTypes() {
  try {
    const res = await listAllBookTypes()
    const d = res.data
    bookTypeOptions.value = Array.isArray(d) ? d : d?.list || []
  } catch (e) {
    bookTypeOptions.value = []
  }
}

// 加载图书列表
async function loadData() {
  loading.value = true
  try {
    const res = await queryBookInfosByPage(queryParams)
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
  queryParams.bookname = ''
  queryParams.author = ''
  queryParams.typeid = ''
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
  dialogTitle.value = '添加图书'
  Object.assign(form, {
    bookid: null,
    bookname: '',
    author: '',
    price: 0,
    typeid: '',
    description: ''
  })
  dialogVisible.value = true
}

function handleEdit(row) {
  dialogTitle.value = '编辑图书'
  Object.assign(form, {
    bookid: row.bookid,
    bookname: row.bookname,
    author: row.author,
    price: Number(row.price),
    typeid: row.typeid,
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
      if (form.bookid) {
        await updateBookInfo({ ...form })
        ElMessage.success('修改成功')
      } else {
        const { bookid, ...data } = form
        await addBookInfo(data)
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
    await ElMessageBox.confirm(`确定要删除图书「${row.bookname}」吗？`, '提示', {
      confirmButtonText: '确定',
      cancelButtonText: '取消',
      type: 'warning'
    })
    await deleteBookInfo({ bookid: row.bookid })
    ElMessage.success('删除成功')
    loadData()
  } catch (e) {
    // 用户取消或请求失败
  }
}

// 借阅图书：携带当前用户ID
async function handleBorrow(row) {
  try {
    await ElMessageBox.confirm(`确定要借阅图书「${row.bookname}」吗？`, '借阅确认', {
      confirmButtonText: '确定借阅',
      cancelButtonText: '取消',
      type: 'info'
    })
    const userid = userStore.user?.userid
    await borrowBook({ bookid: row.bookid, userid })
    ElMessage.success('借阅成功')
    loadData()
  } catch (e) {
    // 用户取消或请求失败
  }
}

onMounted(() => {
  loadBookTypes()
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
