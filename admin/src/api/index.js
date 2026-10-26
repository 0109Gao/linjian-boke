import request from './request'

// ============ 认证 ============
export const login = (data) => request.post('/auth/login', data)
export const getMe = () => request.get('/auth/me')

// ============ 仪表盘 ============
export const getDashboard = () => request.get('/admin/dashboard')

// ============ 文章管理 ============
export const getPosts = (params) => request.get('/admin/posts', { params })
export const auditPost = (id, status) => request.put(`/admin/posts/${id}/audit`, { status })
export const togglePin = (id, pinned) => request.put(`/admin/posts/${id}/pin`, { pinned })
export const deletePost = (id) => request.delete(`/admin/posts/${id}`)

// ============ 评论管理 ============
export const getComments = (params) => request.get('/admin/comments', { params })
export const auditComment = (id, status) => request.put(`/admin/comments/${id}/audit`, { status })
export const deleteComment = (id) => request.delete(`/admin/comments/${id}`)

// ============ 用户管理 ============
export const getUsers = (params) => request.get('/admin/users', { params })
export const toggleUserStatus = (id, status) => request.put(`/admin/users/${id}/status`, { status })
export const updateUserRole = (id, role) => request.put(`/admin/users/${id}/role`, { role })

// ============ 分类管理 ============
export const getCategories = () => request.get('/admin/categories')
export const saveCategory = (data) => data.id ? request.put('/admin/categories', data) : request.post('/admin/categories', data)
export const deleteCategory = (id) => request.delete(`/admin/categories/${id}`)

// ============ 标签管理 ============
export const getTags = () => request.get('/admin/tags')
export const saveTag = (data) => data.id ? request.put('/admin/tags', data) : request.post('/admin/tags', data)
export const deleteTag = (id) => request.delete(`/admin/tags/${id}`)

// ============ 站点配置 ============
export const getConfigs = () => request.get('/admin/configs')
export const saveConfig = (data) => request.put('/admin/configs', data)
