import request from '@/utils/request'

//发表留言或回复（parentId 为空表示直接留言）
export const addMessage = (messageMessage) => {
  return request.post('/message/addMessage', messageMessage)
}

//分页查询留言墙
export const getMessageList = (page, pageSize) => {
  return request.get('/message/getMessageList', {
    params: {
      page,
      pageSize
    }
  })
}

//删除我的留言
export const deleteMessage = (messageId) => {
  return request.delete('/message/deleteMessage', {
    params: { messageId }
  })
}
