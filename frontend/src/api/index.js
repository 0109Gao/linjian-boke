import request from './request'

// 认证
export const register = (data) => request.post('/auth/register', data)
export const login = (data) => request.post('/auth/login', data)
export const getMe = () => request.get('/auth/me')
export const updateProfile = (data) => request.put('/auth/profile', data)

// 头像上传（FormData）
export const uploadAvatar = (file) => {
  const formData = new FormData()
  formData.append('file', file)
  return request.post('/auth/avatar', formData, {
    headers: { 'Content-Type': 'multipart/form-data' }
  })
}

// 文章
export const getPosts = (params) => request.get('/posts', { params })
export const getMyPosts = () => request.get('/posts/mine')
export const getPostDetail = (id) => request.get(`/posts/${id}`)
export const createPost = (data) => request.post('/posts', data)
export const updatePost = (id, data) => request.put(`/posts/${id}`, data)
export const deletePost = (id) => request.delete(`/posts/${id}`)

// 评论
export const getComments = (postId) => request.get(`/posts/${postId}/comments`)
export const createComment = (postId, data) => request.post(`/posts/${postId}/comments`, data)

// 点赞收藏
export const toggleLike = (data) => request.post('/likes', data)
export const toggleFavorite = (postId) => request.post(`/favorites/${postId}`)

// 分类标签
export const getCategories = () => request.get('/categories')
export const getTags = () => request.get('/tags')

// 搜索
export const searchPosts = (q) => request.get('/search', { params: { q } })

// 站点配置
export const getSiteConfig = () => request.get('/site/config')
