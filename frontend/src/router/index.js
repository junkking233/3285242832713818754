import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('@/views/AdminLayout.vue'),
    redirect: '/admin/welcome',
    children: [
      {
        path: 'welcome',
        name: 'Welcome',
        component: () => import('@/views/Welcome.vue'),
        meta: { title: '首页', breadcrumb: ['首页'] }
      },
      {
        path: 'book-info',
        name: 'BookInfoManage',
        component: () => import('@/views/BookInfoManage.vue'),
        meta: { title: '图书信息管理', breadcrumb: ['首页', '图书管理', '图书信息管理'] }
      },
      {
        path: 'book-type',
        name: 'BookTypeManage',
        component: () => import('@/views/BookTypeManage.vue'),
        meta: { title: '图书类型管理', breadcrumb: ['首页', '图书管理', '图书类型管理'] }
      },
      {
        path: 'borrow',
        name: 'BorrowManage',
        component: () => import('@/views/BorrowManage.vue'),
        meta: { title: '借阅信息管理', breadcrumb: ['首页', '图书管理', '借阅信息管理'] }
      },
      {
        path: 'user',
        name: 'UserManage',
        component: () => import('@/views/UserManage.vue'),
        meta: { title: '用户管理', breadcrumb: ['首页', '其他管理', '用户管理'] }
      },
      {
        path: 'change-password',
        name: 'ChangePassword',
        component: () => import('@/views/ChangePassword.vue'),
        meta: { title: '修改密码', breadcrumb: ['首页', '其他管理', '修改密码'] }
      }
    ]
  },
  {
    path: '/',
    redirect: '/login'
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

router.beforeEach((to, from, next) => {
  document.title = (to.meta.title || '图书管理系统') + ' - 图书管理系统'
  const token = localStorage.getItem('token')
  if (to.path !== '/login' && !token) {
    next('/login')
  } else if (to.path === '/login' && token) {
    next('/admin/welcome')
  } else {
    next()
  }
})

export default router
