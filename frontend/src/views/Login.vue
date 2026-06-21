<template>
  <div class="login-container">
    <div class="login-card">
      <h2 class="login-title">登录图书管理系统</h2>
      <el-form ref="formRef" :model="form" :rules="rules" label-width="0" size="large">
        <el-form-item prop="username">
          <el-input v-model="form.username" placeholder="请输入用户名" :prefix-icon="User" />
        </el-form-item>
        <el-form-item prop="password">
          <el-input v-model="form.password" type="password" placeholder="请输入密码"
                    :prefix-icon="Lock" show-password @keyup.enter="handleLogin" />
        </el-form-item>
        <el-form-item prop="role">
          <el-select v-model="form.role" placeholder="请选择身份" style="width: 100%">
            <el-option label="管理员" value="ADMIN" />
            <el-option label="读者" value="READER" />
          </el-select>
        </el-form-item>
        <el-form-item>
          <div class="btn-row">
            <el-button type="primary" :loading="loading" @click="handleLogin">登录</el-button>
            <el-button type="success" @click="goRegister">注册</el-button>
          </div>
        </el-form-item>
      </el-form>

      <!-- 注册弹窗 -->
      <el-dialog v-model="regVisible" title="用户注册" width="420px" center>
        <el-form ref="regFormRef" :model="regForm" :rules="regRules" label-width="80px">
          <el-form-item label="用户名" prop="username">
            <el-input v-model="regForm.username" placeholder="请输入用户名" />
          </el-form-item>
          <el-form-item label="密码" prop="password">
            <el-input v-model="regForm.password" type="password" placeholder="请输入密码" show-password />
          </el-form-item>
          <el-form-item label="真实姓名" prop="realname">
            <el-input v-model="regForm.realname" placeholder="请输入真实姓名" />
          </el-form-item>
        </el-form>
        <template #footer>
          <el-button @click="regVisible = false">取消</el-button>
          <el-button type="primary" :loading="regLoading" @click="handleRegister">注册</el-button>
        </template>
      </el-dialog>

      <div class="login-tips">
        <p>管理员账号：admin / admin123</p>
        <p>读者账号：reader / 123456</p>
      </div>
    </div>
  </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { User, Lock } from '@element-plus/icons-vue'
import { login, register } from '@/api/user'
import { useUserStore } from '@/stores/user'

const router = useRouter()
const userStore = useUserStore()
const formRef = ref()
const loading = ref(false)

const form = reactive({
  username: '',
  password: '',
  role: 'READER'
})

const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
  role: [{ required: true, message: '请选择身份', trigger: 'change' }]
}

async function handleLogin() {
  await formRef.value.validate(async (valid) => {
    if (!valid) return
    loading.value = true
    try {
      const res = await login({ username: form.username, password: form.password })
      if (res.data.user.role !== form.role) {
        ElMessage.error('身份不匹配，请选择正确的身份')
        loading.value = false
        return
      }
      userStore.setAuth(res.data.token, res.data.user)
      ElMessage.success('登录成功')
      router.push('/admin/welcome')
    } catch (e) {
      // 错误已在拦截器处理
    } finally {
      loading.value = false
    }
  })
}

// ---- 注册 ----
const regVisible = ref(false)
const regLoading = ref(false)
const regFormRef = ref()
const regForm = reactive({ username: '', password: '', realname: '' })
const regRules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }]
}

function goRegister() {
  regVisible.value = true
}

async function handleRegister() {
  await regFormRef.value.validate(async (valid) => {
    if (!valid) return
    regLoading.value = true
    try {
      regForm.role = 'READER'
      await register(regForm)
      ElMessage.success('注册成功，请登录')
      regVisible.value = false
      form.username = regForm.username
      form.password = ''
      form.role = 'READER'
    } catch (e) {
      // 错误已在拦截器处理
    } finally {
      regLoading.value = false
    }
  })
}
</script>

<style scoped>
.login-container {
  height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: #2c3e50;
}

.login-card {
  width: 380px;
  padding: 40px 35px;
  background: rgba(255, 255, 255, 0.95);
  border-radius: 8px;
  box-shadow: 0 6px 20px rgba(0, 0, 0, 0.3);
}

.login-title {
  text-align: center;
  margin-bottom: 30px;
  color: #303133;
  font-size: 22px;
  font-weight: 600;
}

.btn-row {
  display: flex;
  gap: 12px;
  width: 100%;
}

.btn-row .el-button {
  flex: 1;
}

.login-tips {
  margin-top: 20px;
  padding: 10px;
  background: #f5f7fa;
  border-radius: 4px;
  font-size: 12px;
  color: #909399;
  line-height: 1.8;
}
</style>
