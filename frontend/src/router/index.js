import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: () => import('../views/Home.vue'),
    meta: { title: '林间博客' }
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/Login.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('../views/Register.vue'),
    meta: { title: '注册' }
  },
  {
    path: '/post/:id',
    name: 'PostDetail',
    component: () => import('../views/PostDetail.vue'),
    meta: { title: '文章详情' }
  },
  {
    path: '/write',
    name: 'Write',
    component: () => import('../views/Write.vue'),
    meta: { title: '写文章', requiresAuth: true }
  },
  {
    path: '/me',
    name: 'Me',
    component: () => import('../views/Me.vue'),
    meta: { title: '我的主页', requiresAuth: true }
  },
  {
    path: '/search',
    name: 'Search',
    component: () => import('../views/Search.vue'),
    meta: { title: '搜索' }
  },
  {
    path: '/admin',
    name: 'Admin',
    component: () => import('../views/Admin.vue'),
    meta: { title: '管理后台', requiresAuth: true, requiresAdmin: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 全局守卫：权限控制 + 页面标题
router.beforeEach((to, from, next) => {
  const title = to.meta.title
  document.title = title && title !== '林间博客' ? `${title} - 林间博客` : '林间博客'

  const token = localStorage.getItem('token')
  const user = JSON.parse(localStorage.getItem('user') || 'null')

  if (to.meta.requiresAuth && !token) {
    next('/login')
    return
  }
  if (to.meta.requiresAdmin && user?.role !== 'ADMIN') {
    next('/')
    return
  }
  next()
})

export default router
