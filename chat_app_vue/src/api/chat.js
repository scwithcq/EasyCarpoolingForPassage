import request from '@/utils/request'

//分页查询与某人的历史消息（服务端会把对方发来的消息标记为已读）
export const getHistory = (targetName, page, pageSize) => {
  return request.get('/chat/getHistory', {
    params: {
      targetName,
      page,
      pageSize
    }
  })
}

//查询我的未读消息数
export const getUnreadCount = () => {
  return request.get('/chat/getUnreadCount')
}