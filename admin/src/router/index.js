import { createRouter, createWebHistory } from 'vue-router'

const routes = [
  {
    path: '/login',
    name: 'Login',
    component: () => import('../views/login/index.vue'),
    meta: { title: '登录' }
  },
  {
    path: '/',
    component: () => import('../layout/index.vue'),
    redirect: '/dashboard',
    children: [
      {
        path: 'dashboard',
        name: 'Dashboard',
        component: () => import('../views/dashboard/index.vue'),
        meta: { title: '仪表盘', icon: 'Odometer' }
      },
      {
        path: 'posts',
        name: 'Posts',
        component: () => import('../views/post/index.vue'),
        meta: { title: '文章管理', icon: 'Document' }
      },
      {
        path: 'comments',
        name: 'Comments',
        component: () => import('../views/comment/index.vue'),
        meta: { title: '评论管理', icon: 'ChatDotRound' }
      },
      {
        path: 'users',
        name: 'Users',
        component: () => import('../views/user/index.vue'),
        meta: { title: '用户管理', icon: 'User' }
      },
      {
        path: 'categories',
        name: 'Categories',
        component: () => import('../views/category/index.vue'),
        meta: { title: '分类与标签', icon: 'CollectionTag' }
      },
      {
        path: 'config',
        name: 'Config',
        component: () => import('../views/config/index.vue'),
        meta: { title: '站点设置', icon: 'Setting' }
      }
    ]
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// 路由守卫：未登录跳转登录页，非管理员拦截
router.beforeEach((to, from, next) => {
  const token = localStorage.getItem('admin_token')
  if (to.path !== '/login' && !token) {
    next('/login')
    return
  }
  if (to.path === '/login' && token) {
    next('/dashboard')
    return
  }
  next()
})

export default router
