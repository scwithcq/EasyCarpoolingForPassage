import request from '@/utils/request'

//查询帖子分类列表
export const getCategoryList = () => {
  return request.get('/forum/getCategoryList')
}

//发布帖子
export const addPost = (postMessage) => {
  return request.post('/forum/addPost', postMessage)
}

//分页查询帖子列表（categoryId 为空表示全部）
export const getPostList = (page, pageSize, categoryId) => {
  return request.get('/forum/getPostList', {
    params: {
      page,
      pageSize,
      categoryId
    }
  })
}

//查询帖子详情
export const getPostById = (postId) => {
  return request.get('/forum/getPostById', {
    params: { postId }
  })
}

//删除我发布的帖子
export const deletePost = (postId) => {
  return request.delete('/forum/deletePost', {
    params: { postId }
  })
}

//分页查询我发布的帖子
export const getMyPost = (page, pageSize) => {
  return request.get('/forum/getMyPost', {
    params: {
      page,
      pageSize
    }
  })
}

//发表评论或回复（parentId 为空表示直接评论帖子）
export const addComment = (commentMessage) => {
  return request.post('/forum/addComment', commentMessage)
}

//查询帖子评论列表
export const getCommentList = (postId) => {
  return request.get('/forum/getCommentList', {
    params: { postId }
  })
}

//删除我的评论
export const deleteComment = (commentId) => {
  return request.delete('/forum/deleteComment', {
    params: { commentId }
  })
}

//点赞或取消点赞（targetType: 1-帖子 2-评论）
export const likeTarget = (targetType, targetId) => {
  return request.post('/forum/like', null, {
    params: { targetType, targetId }
  })
}
